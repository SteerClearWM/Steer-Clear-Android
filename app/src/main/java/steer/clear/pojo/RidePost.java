package steer.clear.pojo;

/**
 * Created by milespeele on 7/2/15.
 */
public class RidePost {

    private int num_passengers;
    private float start_latitude;
    private float start_longitude;
    private float end_latitude;
    private float end_longitude;

    public RidePost(int numPassengers, double pickupLat, double pickupupLong,
                    double dropoffLat, double dropoffLong) {
        num_passengers = numPassengers;
        start_latitude = (float) pickupLat;
        start_longitude = (float) pickupupLong;
        end_latitude = (float) dropoffLat;
        end_longitude = (float) dropoffLong;
    }
}
