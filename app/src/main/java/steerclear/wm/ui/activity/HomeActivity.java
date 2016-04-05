package steerclear.wm.ui.activity;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.location.LocationManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.Bind;
import icepick.State;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import steerclear.wm.R;
import steerclear.wm.data.LocationService;
import steerclear.wm.data.SimpleLocationListener;
import steerclear.wm.ui.LoadingDialog;
import steerclear.wm.ui.fragment.HailRideFragment;
import steerclear.wm.ui.fragment.IRideConfirm;
import steerclear.wm.ui.fragment.MapFragment;
import steerclear.wm.data.model.RideObject;
import steerclear.wm.data.rx.ActivitySubscriber;
import steerclear.wm.util.Logg;

public class HomeActivity extends BaseActivity
	implements OnConnectionFailedListener, ConnectionCallbacks, IRideConfirm {

    private final static String MAP = "map";
    private final static String POST = "post";
    private final static String SAVE_PICKUP = "pickupLatLng";
    private final static String SAVE_DROPOFF = "dropoffLatLng";
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final int REQUEST_PERMISSION_LOCATION = 1;

    @Bind(R.id.activity_home_toolbar)
    Toolbar toolbar;

    @State LatLng userLatLng, pickupLatLng, dropoffLatLng;
    private LoadingDialog loadingDialog;
	private GoogleApiClient mGoogleApiClient;

    public static Intent newIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.icons));

        String[] permissions = new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (hasPermissions(permissions)) {
            if (LocationService.isLocationEnabled(this)) {
                init();
            } else {
                showSettingsAlert();
            }
        } else {
            requestPermissions(REQUEST_PERMISSION_LOCATION, permissions);
        }
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_home_menu_logout:
                onLogout();
                return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    finishAffinity();
                }
            }
        }
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVE_PICKUP, pickupLatLng);
        outState.putParcelable(SAVE_DROPOFF, dropoffLatLng);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pickupLatLng = savedInstanceState.getParcelable(SAVE_PICKUP);
        dropoffLatLng = savedInstanceState.getParcelable(SAVE_DROPOFF);
    }

	@Override
	public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
	    if (count == 0) {
            super.onBackPressed();
	    } else {
            getSupportFragmentManager().popBackStack();
		}
	}

	@Override
    @SuppressWarnings({"ResourceType"})
	public void onConnected(Bundle connectionHint) {
        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (currentLocation != null) {
            userLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        } else {
            Location location = LocationService.getLastKnownLocation(this);
            if (location != null) {
                userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                SimpleLocationListener simpleLocationListener = new SimpleLocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        LocationService.getLocationManager(HomeActivity.this).removeUpdates(this);
                        userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        showMap(userLatLng);
                    }
                };

                LocationService.getLocationUpdates(this, simpleLocationListener);
            }
        }

        showMap(userLatLng);
	}

    private void showMap(LatLng latLng) {
        Logg.log(latLng.latitude, latLng.longitude);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(MAP) == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.activity_home_fragment_frame, MapFragment.newInstance(latLng), MAP)
                    .commit();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {

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
            Logg.log("CONNECTION FAILED WITH CODE: " + connectionResult.getErrorCode());
        }
    }

    private void init() {
        loadingDialog = new LoadingDialog(this, R.style.ProgressDialogTheme);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void showSettingsAlert() {
        new AlertDialog.Builder(this)
            .setTitle(getResources().getString(R.string.dialog_no_gps_title))
            .setMessage(getResources().getString(R.string.dialog_no_gps_body))
            .setPositiveButton(getResources().getString(R.string.dialog_no_gps_pos_button_text),
                (dialog, which) -> {
                    dialog.dismiss();
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
            .setNegativeButton(getResources().getString(R.string.dialog_no_gps_neg_button_text),
                (dialog, which) -> {
                    finishAffinity();
                })
            .create()
            .show();
    }

    @Override
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void onLocationConfirm(LatLng pickupLatLng, CharSequence pickupName, LatLng dropoffLatLng, CharSequence dropoffName) {
        this.pickupLatLng = pickupLatLng;
        this.dropoffLatLng = dropoffLatLng;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        HailRideFragment fragment = HailRideFragment.newInstance(
                pickupName, dropoffName);
        ft.addToBackStack(MAP);
        ft.setCustomAnimations(R.anim.scale_in, R.anim.scale_out);
        ft.replace(R.id.activity_home_fragment_frame, fragment, POST).commit();
    }

    @Override
    public void onRideConfirm(int numberOfPassengers) {
        Subscriber<RideObject> subscriber = new ActivitySubscriber<RideObject>(this) {

            int hour, minute, cancelId;

            @Override
            public void onCompleted() {
                loadingDialog.dismiss();

                String format = String.format(Locale.getDefault(), "%02d : %02d", hour, minute);
                startActivity(EtaActivity.newIntent(HomeActivity.this, format, cancelId));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                finish();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(RideObject rideObject) {
                RideObject.RideInfo info = rideObject.getRideInfo();
                String pickupTime = info.getPickupTime();
                cancelId = info.getId();
                try {
                    SimpleDateFormat dateFormat =
                            new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss", Locale.US);
                    dateFormat.setTimeZone(TimeZone.getTimeZone("est"));

                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(dateFormat.parse(pickupTime));

                    hour = calendar.get(Calendar.HOUR);
                    minute = calendar.get(Calendar.MINUTE);
                } catch (ParseException p) {
                    p.printStackTrace();
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                loadingDialog.show();
            }
        };

        helper.addRide(numberOfPassengers, pickupLatLng.latitude, pickupLatLng.longitude,
                dropoffLatLng.latitude, dropoffLatLng.longitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    @Override
    public void onLogout() {
        Subscriber<ResponseBody> logoutSubscriber = new ActivitySubscriber<ResponseBody>(this) {
            @Override
            public void onCompleted() {
                store.putCookie("");
                removeSubscription(this);
                loadingDialog.dismiss();
                startActivity(AuthenticateActivity.newIntent(HomeActivity.this, false));
                finish();
            }

            @Override
            public void onError(Throwable e) {
                onCompleted();
            }

            @Override
            public void onNext(ResponseBody response) {

            }

            @Override
            public void onStart() {
                super.onStart();
                loadingDialog.show();
            }
        };

        helper.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(logoutSubscriber);
    }
}