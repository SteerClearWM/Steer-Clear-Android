package Steer.Clear.pojo;

/**
 * Created by milespeele on 7/2/15.
 */
public class RidePost {

    public int num_passengers;
    public float start_latitude;
    public float start_longitude;
    public float end_latitude;
    public float end_longitude;

    public RidePost(int numPassengers, double pickupLat, double pickupupLong,
                    double dropoffLat, double dropoffLong) {
        num_passengers = numPassengers;
        start_latitude = (float) pickupLat;
        start_longitude = (float) pickupupLong;
        end_latitude = (float) dropoffLat;
        end_longitude = (float) dropoffLong;
    }
}
