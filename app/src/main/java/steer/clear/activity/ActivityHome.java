package steer.clear.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import steer.clear.MainApp;
import steer.clear.R;
import steer.clear.event.EventChangePlaces;
import steer.clear.event.EventPlacesChosen;
import steer.clear.event.EventPostPlacesChosen;
import steer.clear.fragment.FragmentHailRide;
import steer.clear.fragment.FragmentMap;
import steer.clear.retrofit.Client;
import steer.clear.util.Datastore;
import steer.clear.util.Logger;
import steer.clear.util.Utils;

public class ActivityHome extends AppCompatActivity
	implements OnConnectionFailedListener, ConnectionCallbacks {

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
	public GoogleApiClient mGoogleApiClient;
    private AlertDialog settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		((MainApp) getApplicationContext()).getApplicationComponent().inject(this);

        bus.register(this);

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this, 0, this)
				.addApi(Places.GEO_DATA_API)
				.addApi(Places.PLACE_DETECTION_API)
				.addConnectionCallbacks(this)
				.addApi(LocationServices.API)
				.build();
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
            super.onBackPressed();
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
				currentLocation = Utils.getLocation(this);
                if (currentLocation != null) {
                    userLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
					showMapStuff(userLatLng);
                } else {
                    showSettingsAlert();
                }
            }
        }
	}

	private void showMapStuff(LatLng currentLatLng) {
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.activity_home_fragment_frame,
				FragmentMap.newInstance(currentLatLng), MAP);
		fragmentTransaction.commit();
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
            ft.replace(R.id.activity_home_fragment_frame, FragmentMap.newInstance(userLatLng),
                    MAP).commit();
        }
    }

    public void onEvent(EventPostPlacesChosen eventPostPlacesChosen) {

    }
}