package steerclear.wm.ui.fragment;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mbpeele on 4/4/16.
 */
public interface IRideConfirm {

    GoogleApiClient getGoogleApiClient();

    void onLocationConfirm(LatLng pickupLatLng, CharSequence pickupName,
                           LatLng dropoffLatLng, CharSequence dropoffName);

    void onRideConfirm(int numberOfPassengers);

    void onLogout();
}
