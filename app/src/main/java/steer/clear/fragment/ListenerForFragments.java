package steer.clear.fragment;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

public interface ListenerForFragments {

    void authenticate(String username, String password);
    GoogleApiClient getGoogleApiClient();
    void changePickup();
    void setChosenLocation(String fragmentTag, LatLng latlng, CharSequence name);
    void changeDropoff();
    void makeHttpPostRequest(int numPassengers);
    void onChosenLocationChanged(String fragmentTag, LatLng latlng, CharSequence c);
}
