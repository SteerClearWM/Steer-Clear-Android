package steer.clear;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

public interface ListenerForFragments {
	
	public GoogleApiClient getGoogleApiClient();
    public void setPickup(LatLng pickupLatLng, CharSequence pickupName);
    public void setDropoff(LatLng dropoff, CharSequence dropoffName);
    public void makeHttpPostRequest(int numPassengers);

}
