package Steer.Clear.pojo;

/**
 * Created by Miles Peele on 6/21/2015.
 */
public class RideObject {

    public RideInfo ride;

    public RideInfo getRideInfo() {
        return ride;
    }

    public static class RideInfo {

        public int id;
        public Float end_latitude;
        public Float end_longitude;
        public int num_passengers;
        public String pickup_time;
        public Float start_latitude;
        public Float start_longitude;

        public int getId() {
            return id;
        }

        public Float getEndLatitude() {
            return end_latitude;
        }

        public Float getEndLongitude() {
            return end_longitude;
        }

        public int getNumPassengers() {
            return num_passengers;
        }

        public String getPickupTime() {
            return pickup_time;
        }

        public Float getStartLatitude() {
            return start_latitude;
        }

        public Float getStartLongitude() {
            return start_longitude;
        }
    }
}
