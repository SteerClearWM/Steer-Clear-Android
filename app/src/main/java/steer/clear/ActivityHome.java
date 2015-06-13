package steer.clear;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * "HomeScreen" activity of the SteerClear app.
 * Instantiates MapFragments and handles anything having to do with Http.
 * @author Miles Peele
 *
 */
public class ActivityHome extends FragmentActivity
	implements HttpHelperInterface, ListenerForFragments, OnConnectionFailedListener, ConnectionCallbacks {
	
	// After user clicks "Next" in FragmentMap, stores their chosen LatLngs in these variables
	private static LatLng pickupLatLng;
	private static CharSequence pickupLocationName;
	private static LatLng dropoffLatLng;
	private static CharSequence dropoffLocationName; // says it's unused but it is used in makeHttpPostRequest()
	
	// Static strings used as tags for Fragments
	private final static String PICKUP = "pickup";
	private final static String DROPOFF = "dropoff";
	private final static String POST = "post";
	
	// Only one GoogleApiClient ever instantiated
	protected GoogleApiClient mGoogleApiClient;
	
	private ProgressDialog httpProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

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
	        getFragmentManager().popBackStack();
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
				int pickupHour = calendar.get(Calendar.HOUR);
				int pickupMinute = calendar.get(Calendar.MINUTE);

				Intent etaActivity = new Intent(this, ActivityETA.class);
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
		Logger.log("CONNECTION TO GOOGLEAPICLIENT FAILED " + result.toString());
	}

	/**
	 * Called from FragmentMap when user chooses their location and moves on from the "Pickup Location" fragment.
	 * Stores the user's pickupLatLng and pickupLocationName, then adds the "dropoff location" fragment to the layout.
	 */
	@Override
	public void setPickup(LatLng pickupLatLng, CharSequence pickupLocationName) {
		ActivityHome.pickupLatLng = pickupLatLng;
		ActivityHome.pickupLocationName = pickupLocationName;
    	FragmentTransaction ft = getFragmentManager().beginTransaction();

	    Fragment fragment = FragmentMap.newInstance(DROPOFF, pickupLatLng);
	    ft.addToBackStack(DROPOFF);
	    ft.replace(R.id.activity_home_fragment_frame, fragment, DROPOFF);
	    ft.commit();
	}

	/**
	 * Called from FragmentMap when user chooses their location and moves on from the "dropoff Location" fragment.
	 * Stores the user's dropoffLatLng and dropoffLocationName, then adds the "hail ride" fragment to the layout.
	 */
	@Override
	public void setDropoff(LatLng dropoffLatLng, CharSequence dropoffLocationName) {
		if (pickupLocationName.equals(dropoffLocationName)) {
			Toast.makeText(this, "Your pickup and dropoff cannot be the same location", Toast.LENGTH_SHORT).show();
			return;
		}
		
		ActivityHome.dropoffLatLng = dropoffLatLng;
		ActivityHome.dropoffLocationName = dropoffLocationName;
		FragmentTransaction ft = getFragmentManager().beginTransaction();

		FragmentHailRide fragment = FragmentHailRide.newInstance(pickupLocationName, dropoffLocationName);
	    ft.addToBackStack(POST);
	    ft.replace(R.id.activity_home_fragment_frame, fragment, POST);
	    ft.commit();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
        Fragment prev = getFragmentManager().findFragmentByTag(PICKUP);
        if (prev != null) {
            getFragmentManager().beginTransaction().show(prev).commit();
        } else {
            Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (currentLocation != null) {
                LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                FragmentMap fragment = FragmentMap.newInstance(PICKUP, currentLatLng);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.add(R.id.activity_home_fragment_frame, fragment, PICKUP);
                ft.commit();
            } else {
                showSettingsAlert();
            }
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