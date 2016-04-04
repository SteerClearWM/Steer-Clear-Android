package steerclear.wm.data.event;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Miles Peele on 8/19/2015.
 */
public class EventPlacesChosen {

    public LatLng pickupLatLng;
    public CharSequence pickupName;
    public LatLng dropoffLatLng;
    public CharSequence dropoffName;

    public EventPlacesChosen(LatLng pickupLatLng, CharSequence pickupName,
                             LatLng dropoffLatLng, CharSequence dropoffName) {
        this.pickupLatLng = pickupLatLng;
        this.pickupName = pickupName;
        this.dropoffLatLng = dropoffLatLng;
        this.dropoffName = dropoffName;
    }
}
