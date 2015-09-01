package steer.clear.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.RetrofitError;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import steer.clear.MainApp;
import steer.clear.R;
import steer.clear.event.EventChangePlaces;
import steer.clear.event.EventPlacesChosen;
import steer.clear.event.EventPostPlacesChosen;
import steer.clear.fragment.FragmentHailRide;
import steer.clear.fragment.FragmentMap;
import steer.clear.pojo.RideObject;
import steer.clear.retrofit.Client;
import steer.clear.util.ErrorDialog;
import steer.clear.util.LoadingDialog;
import steer.clear.util.Locationer;
import steer.clear.util.Logger;
import steer.clear.util.TimeLock;

public class ActivityHome extends AppCompatActivity
	implements OnConnectionFailedListener, ConnectionCallbacks, LocationListener {

    private static LatLng userLatLng;
	private static LatLng pickupLatLng;
	private static CharSequence pickupLocationName;
	private static LatLng dropoffLatLng;
	private static CharSequence dropoffLocationName;

	private final static String MAP = "map";
	private final static String POST = "post";

	private static final int REQUEST_RESOLVE_ERROR = 1001;

	@Inject Client helper;
    @Inject EventBus bus;
    @Inject Locationer locationer;
    private LoadingDialog loadingDialog;
    private LocationRequest locationRequest;
	public GoogleApiClient mGoogleApiClient;
    private AlertDialog settings;

    public static Intent newIntent(Context context) {
        return new Intent(context, ActivityHome.class);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		((MainApp) getApplicationContext()).getApplicationComponent().inject(this);

        loadingDialog = new LoadingDialog(this, R.style.ProgressDialogTheme);

        bus.register(this);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this, 0, this)
				.addApi(Places.GEO_DATA_API)
				.addApi(Places.PLACE_DETECTION_API)
				.addConnectionCallbacks(this)
				.addApi(LocationServices.API)
				.build();

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
	}

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().setBackgroundDrawable(null);
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_RESOLVE_ERROR) {
			if (resultCode == RESULT_OK) {
				if (!mGoogleApiClient.isConnecting() &&
						!mGoogleApiClient.isConnected()) {
					mGoogleApiClient.connect();
				}
			}
		}
	}

	@Override
	public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
	    if (count == 0) {
            moveTaskToBack(true);
	    } else {
	        getFragmentManager().popBackStack();
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
        if (getFragmentManager().findFragmentByTag(MAP) == null) {
            Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (currentLocation != null) {
                userLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
				showMapStuff(userLatLng);
            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        locationRequest, this);
                currentLocation = locationer.getLocation();
                if (currentLocation != null) {
                    userLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    showMapStuff(userLatLng);
                } else {
                    showSettingsAlert();
                }
            }
        }
	}

	private void showMapStuff(LatLng userLatLng) {
        stopLocationUpdates();
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.activity_home_fragment_frame,
                FragmentMap.newInstance(userLatLng), MAP);
		fragmentTransaction.commit();
        new Handler().postDelayed(() -> getWindow().setBackgroundDrawable(null), 3000);
	}

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        locationer.stopListeningForLocation();
    }

    @Override
	public void onConnectionSuspended(int cause) {

	}

    public void showSettingsAlert() {
        if (settings == null) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(getResources().getString(R.string.dialog_no_gps_title));
            alertDialog.setMessage(getResources().getString(R.string.dialog_no_gps_body));
            alertDialog.setPositiveButton(getResources().getString(R.string.dialog_no_gps_pos_button_text),
                    (dialog, which) -> {
                        dialog.dismiss();
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    });

            alertDialog.setNegativeButton(getResources().getString(R.string.dialog_no_gps_neg_button_text),
                    (dialog, which) -> {
                        finish();
                    });
            settings = alertDialog.create();
            settings.show();
        }

        if (!settings.isShowing()) {
            settings.show();
        }
    }

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Logger.log("CONNECTION FAILED WITH CODE: " + connectionResult.getErrorCode());
        }
	}

    public void onEvent(EventPlacesChosen eventPlacesChosen) {
        pickupLatLng = eventPlacesChosen.pickupLatLng;
        dropoffLatLng = eventPlacesChosen.dropoffLatLng;

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        FragmentHailRide fragment = FragmentHailRide.newInstance(eventPlacesChosen.pickupName,
                eventPlacesChosen.dropoffName);
        ft.addToBackStack(MAP);
        ft.replace(R.id.activity_home_fragment_frame, fragment, POST).commit();
    }

    public void onEvent(EventChangePlaces eventChangePlaces) {
        FragmentMap fragmentMap = (FragmentMap) getFragmentManager().findFragmentByTag(MAP);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (fragmentMap != null) {
            ft.replace(R.id.activity_home_fragment_frame, fragmentMap, MAP).commit();
        } else {
            ft.replace(R.id.activity_home_fragment_frame, FragmentMap.newInstance(userLatLng), MAP).commit();
        }
    }


    public void onEvent(EventPostPlacesChosen eventPostPlacesChosen) {
//        if (TimeLock.isSteerClearRunning()) {
//            if (!isFinishing()) {
//                loadingDialog.show();
//            }
//
//            helper.addRide(eventPostPlacesChosen.numPassengers,
//                    pickupLatLng.latitude, pickupLatLng.longitude,
//                    dropoffLatLng.latitude, dropoffLatLng.longitude)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(this::onRideObjectReceived, this::onRideObjectPostError);
//        } else {
//            ErrorDialog.steerClearNotRunning(this);
//        }
        if (!isFinishing()) {
            loadingDialog.show();
        }

        helper.addRide(eventPostPlacesChosen.numPassengers,
                pickupLatLng.latitude, pickupLatLng.longitude,
                dropoffLatLng.latitude, dropoffLatLng.longitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onRideObjectReceived, this::onRideObjectPostError);
    }

    public void onRideObjectReceived(RideObject rideObject) {
        RideObject.RideInfo info = rideObject.getRideInfo();
        String pickupTime = info.getPickupTime();
        int cancelId = info.getId();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss",
                    Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("est"));

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(dateFormat.parse(pickupTime));

            new Handler().postDelayed(() -> {
                loadingDialog.dismiss();
                startActivity(ActivityEta.newIntent(this,
                        String.format("%02d : %02d", calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE)),
                        cancelId));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }, 2000);
        } catch (ParseException p) {
            p.printStackTrace();
        }
    }

    public void onRideObjectPostError(Throwable throwable) {
        loadingDialog.dismiss();
        throwable.printStackTrace();
        if (throwable instanceof RetrofitError) {
            RetrofitError error = (RetrofitError) throwable;
            ErrorDialog.createFromHttpErrorCode(this, error.getResponse() != null ?
                    error.getResponse().getStatus() : 404).show();
        } else {
            ErrorDialog.createFromHttpErrorCode(this, 404).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
    }
}