package steer.clear.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by mbpeele on 8/28/15.
 */
public class Locationer implements android.location.LocationListener {

    private LocationManager locationManager;
    private boolean isGpsEnabled;
    private boolean isNetworkEnabled;
    private Location lastKnown;

    public Locationer(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGpsEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    this);
        }

        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    this);
        }
    }

    public Location getLocation() {
        if (lastKnown != null) {
            Logger.log("LAST KNOWN NOT NULL");
            return lastKnown;
        }

        try {
            if (!isGpsEnabled && !isNetworkEnabled) {
                return null;
            } else {
                Location location;
                if (isNetworkEnabled) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        return (lastKnown = location);
                    }
                }

                if (isGpsEnabled) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        return (lastKnown = location);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        lastKnown = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
