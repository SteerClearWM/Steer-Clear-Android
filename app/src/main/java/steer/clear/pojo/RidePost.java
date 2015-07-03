package steer.clear.pojo;

/**
 * Created by milespeele on 7/2/15.
 */
public class RidePost {

    private int numPassengers;
    private double pickupLat;
    private double pickupLong;
    private double dropoffLat;
    private double dropoffLong;

    public RidePost(int numPassengers, double pickupLat, double pickupupLong,
                    double dropoffLat, double dropoffLong) {
        this.numPassengers = numPassengers;
        this.pickupLat = pickupLat;
        this.pickupLong = pickupupLong;
        this.dropoffLat = dropoffLat;
        this.dropoffLong = dropoffLong;
    }
}
