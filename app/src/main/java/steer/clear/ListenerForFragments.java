package steer.clear;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

public interface ListenerForFragments {
	
	public GoogleApiClient getGoogleApiClient();
    public void changePickup();
    public void setChosenLocation(String fragmentTag, LatLng latlng, CharSequence name);
    public void changeDropoff();
    public void makeHttpPostRequest(int numPassengers);
    public void onChosenLocationChanged(String fragmentTag, LatLng latlng, CharSequence c);
}
