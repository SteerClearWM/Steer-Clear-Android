package steer.clear.activity;

import android.support.v7.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import steer.clear.fragment.FragmentHailRide;
import steer.clear.fragment.FragmentMap;
import steer.clear.service.HttpHelper;
import steer.clear.service.HttpHelperInterface;
import steer.clear.fragment.ListenerForFragments;
import steer.clear.Logger;
import steer.clear.R;

/**
 * "HomeScreen" activity of the SteerClear app.
 * Instantiates MapFragments and handles anything having to do with Http.
 * @author Miles Peele
 *
 */
public class ActivityHome extends AppCompatActivity
	implements HttpHelperInterface, ListenerForFragments, OnConnectionFailedListener, ConnectionCallbacks {

	// Stores user's current location
	private static LatLng currentLatLng;

	// After user clicks "Next" in FragmentMap, stores their chosen LatLngs in these variables
	private static LatLng pickupLatLng;
	private static CharSequence pickupLocationName;
	private static LatLng dropoffLatLng;
	private static CharSequence dropoffLocationName; // says it's unused but it is used in makeHttpPostRequest()
	
	// Static strings used as tags for Fragments
	private final static String PICKUP = "pickup";
	private final static String DROPOFF = "dropoff";
	private final static String POST = "post";

	// Request code to use when launching the resolution activity
	private static final int REQUEST_RESOLVE_ERROR = 1001;
	// Bool to track whether the app is already resolving an error
	private boolean mResolvingError = false;
	
	// Only one GoogleApiClient ever instantiated
	protected GoogleApiClient mGoogleApiClient;
	private static final String STATE_RESOLVING_ERROR = "resolving_error";
	
	private ProgressDialog httpProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		mResolvingError = savedInstanceState != null
				&& savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

		httpProgress = new ProgressDialog(this, ProgressDialog.STYLE_HORIZONTAL);
		httpProgress.setMessage("Notifying driver of your request...");
		
		mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .enableAutoManage(this, 0, this)
	        .addApi(Places.GEO_DATA_API)
	        .addApi(Places.PLACE_DETECTION_API)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .addApi(LocationServices.API)
	        .build();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!mResolvingError) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	protected void onStop() {
		mGoogleApiClient.disconnect();
		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_RESOLVE_ERROR) {
			mResolvingError = false;
			if (resultCode == RESULT_OK) {
				// Make sure the app is not already connected or attempting to connect
				if (!mGoogleApiClient.isConnecting() &&
						!mGoogleApiClient.isConnected()) {
					mGoogleApiClient.connect();
				}
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
	}
	
	/**
	 * Handles navigation between fragments.
	 * If the backstack is empty (count == 0), just finishes the activity by calling super.onBackPressed()
	 * If not, pops that fragment off the backstack and shows it
	 */
	@Override
	public void onBackPressed() {
	    int count = getFragmentManager().getBackStackEntryCount();
	    if (count == 0) {
	        super.onBackPressed();
	    } else {
	        getFragmentManager().popBackStackImmediate();
		}
	}

	/**
	 * Called from FragmentHailRide when the app is ready to post a ride to the server.
	 */
	@Override
	public void makeHttpPostRequest(int numPassengers) {
		showHttpProgress();
		HttpHelper.getInstance(this).addRide(numPassengers,
				pickupLatLng.latitude, pickupLatLng.longitude,
				dropoffLatLng.latitude, dropoffLatLng.longitude);
	}

	/**
	 * On a successful HttpPost request, returns JSONObject
	 */
	@Override
	public void onPostSuccess(JSONObject object) {
		try {
			JSONObject rideObject = new JSONObject(object.getString("ride"));
			String pickupTime = rideObject.getString("pickup_time");
			int cancelId = rideObject.getInt("id");
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss");
				dateFormat.setTimeZone(TimeZone.getTimeZone("est"));
				Date eta = dateFormat.parse(pickupTime);

				Calendar calendar = new GregorianCalendar();
				calendar.setTime(eta);
				int pickupHour = calendar.get(Calendar.HOUR_OF_DAY);
				int pickupMinute = calendar.get(Calendar.MINUTE);

				Intent etaActivity = new Intent(this, ActivityEta.class);
				etaActivity.putExtra("PICKUP_HOUR", pickupHour);
				etaActivity.putExtra("PICKUP_MINUTE", pickupMinute);
				etaActivity.putExtra("CANCEL_ID", cancelId);
				startActivity(etaActivity);

				dismissHttpProgress();
				finish();
			} catch (ParseException p) {
				dismissHttpProgress();
				Logger.log("COULDNT PARSE DATE CUZ " + p);
			}
		} catch (JSONException e) {
			dismissHttpProgress();
			Logger.log("JSONEXCEPTION " + e.toString());
		}
	}

	/**
	 * On a successful HttpGet request, returns JSONObject
	 */
	@Override
	public void onGetSuccess(JSONObject object) {
		Log.v("Miles", "ON GET SUCCESS< OBJECT IS " + object);
	}

	/**
	 * On an unsuccessful HttpRequest.
	 * What to do in this? They're pretty much all 404 errors...
	 * For now, let's not worry about it.
	 */
	@Override
	public void onVolleyError(VolleyError error) {
		dismissHttpProgress();
		if (error.networkResponse != null) {
            Logger.log("Error Response code: " + error.networkResponse.statusCode);
        } else {
        	Toast.makeText(this, "Unknown network error", Toast.LENGTH_SHORT).show();
        	Logger.log("VOLLY ERROR NULL");
        }
	}

	@Override
	public void onDeleteSuccess(String string) {
		// NOT CALLED HERE
	}

	private void showHttpProgress() {
		if (httpProgress != null && !httpProgress.isShowing()) {
			httpProgress.show();
		}
	}

	private void dismissHttpProgress() {
		if (httpProgress != null && httpProgress.isShowing()) {
			httpProgress.dismiss();
		}
	}

	/**
	 * Convenience "get" method that fragments can call to get the googleApiClient.
	 * Because of the apiClient's automanage feature, we don't (shouldn't) have to worry about this causing problems.
	 */
	@Override
	public GoogleApiClient getGoogleApiClient() {
		return mGoogleApiClient;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (mResolvingError) {
			// Already attempting to resolve an error.
			return;
		} else if (result.hasResolution()) {
			try {
				mResolvingError = true;
				result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
			} catch (IntentSender.SendIntentException e) {
				// There was an error with the resolution intent. Try again.
				mGoogleApiClient.connect();
			}
		} else {
			// Show dialog using GooglePlayServicesUtil.getErrorDialog()
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 1);
			mResolvingError = true;
		}
	}

	@Override
	public void changePickup() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		FragmentMap fragment = FragmentMap.newInstance(PICKUP, currentLatLng, true);
		ft.addToBackStack(PICKUP);
		ft.add(R.id.activity_home_fragment_frame, fragment, PICKUP);
		ft.commit();
	}

	@Override
	public void changeDropoff() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		FragmentMap fragment = FragmentMap.newInstance(DROPOFF, currentLatLng, true);
		ft.addToBackStack(DROPOFF);
		ft.add(R.id.activity_home_fragment_frame, fragment, DROPOFF);
		ft.commit();
	}

	@Override
	public void setChosenLocation(String fragmentTag, LatLng latlng, CharSequence name) {
		if (fragmentTag == PICKUP) {
			ActivityHome.pickupLatLng = latlng;
			ActivityHome.pickupLocationName = name;

			FragmentTransaction ft = getFragmentManager().beginTransaction();
			Fragment fragment = FragmentMap.newInstance(DROPOFF, currentLatLng, false);
			ft.remove(getFragmentManager().findFragmentByTag(PICKUP));
			ft.addToBackStack(DROPOFF);
			ft.add(R.id.activity_home_fragment_frame, fragment, DROPOFF);
			ft.commit();
		} else {
			if (pickupLocationName.equals(name)) {
				Toast.makeText(this, "Your pickup and dropoff cannot be the same location", Toast.LENGTH_SHORT).show();
				return;
			}

			ActivityHome.dropoffLatLng = latlng;
			ActivityHome.dropoffLocationName = name;

			FragmentTransaction ft = getFragmentManager().beginTransaction();
			FragmentHailRide fragment = FragmentHailRide.newInstance(pickupLocationName, dropoffLocationName);
			ft.remove(getFragmentManager().findFragmentByTag(DROPOFF));
			ft.addToBackStack(POST);
			ft.replace(R.id.activity_home_fragment_frame, fragment, POST);
			ft.commit();
		}
	}

	@Override
	public void onChosenLocationChanged(String fragmentTag, LatLng latlng, CharSequence name) {
		if (name.equals(pickupLocationName) || name.equals(dropoffLocationName)) {
			Toast.makeText(this, "Your pickup and dropoff cannot be the same location", Toast.LENGTH_SHORT).show();
			return;
		}

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (fragmentTag == PICKUP) {
			ActivityHome.pickupLatLng = latlng;
			ActivityHome.pickupLocationName = name;
		} else {
			ActivityHome.dropoffLatLng = latlng;
			ActivityHome.dropoffLocationName = name;
		}
		getFragmentManager().popBackStack();
		FragmentHailRide prevHailRide =
				(FragmentHailRide) getFragmentManager().findFragmentByTag(POST);
		prevHailRide.onLocationChanged(fragmentTag, name);
		ft.show(prevHailRide).commit();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (currentLocation != null) {
			currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

			FragmentMap fragment = FragmentMap.newInstance(PICKUP, currentLatLng, false);
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(R.id.activity_home_fragment_frame, fragment, PICKUP);
			ft.commit();
		} else {
			Logger.log("ON CONNECTED FAIL?");
			showSettingsAlert();
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		switch (cause) {
			case GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST:
				Toast.makeText(this, "Connection to internet has been lost, re-enable to continue using", Toast.LENGTH_SHORT).show();
				break;
				
			case GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED:
				Toast.makeText(this, "Connection to GoogleApiClient has been suspended", Toast.LENGTH_SHORT).show();
				break;
				
			default:
				Logger.log("ONCONNECTIONSUSPENDED " + cause);
				break;
		}
	}
	
	/**
     * Method to show settings alert dialog if GPS could not be found
     * On pressing Settings button will launch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS Settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu? This app will close if you press 'Cancel'.");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
        	
            @Override
			public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                dialog.dismiss();
            }
            
        });
  
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        	
            @Override
			public void onClick(DialogInterface dialog, int which) {
            	dialog.cancel();
            	finish();
            }
            
        });

        alertDialog.show();
    }
}