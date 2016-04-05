package steerclear.wm.data.model;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import steerclear.wm.util.Logg;

/**
 * Created by Miles Peele on 6/21/2015.
 */
public class RideObject {

    public RideInfo ride;

    public RideInfo getRideInfo() {
        return ride;
    }

    public DateTime convertStringToDateTime() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("EEE, dd MMM yyyy hh:mm:ss Z")
                .withLocale(Locale.US)
                .withZone(DateTimeZone.forID("America/New_York"));
        String pickuptime = ride.pickup_time;
        String newTime = pickuptime.replace("-0000", "");
        DateTime dateTime = dateTimeFormatter.parseDateTime(newTime + "-04:00");
        return dateTime.plusHours(8);
    }

    public static class RideInfo {

        public int id;
        public Float end_latitude;
        public Float end_longitude;
        public int num_passengers;
        public String pickup_time;
        public Float start_latitude;
        public Float start_longitude;
        public boolean on_campus;

        public boolean getOnCampus() { return on_campus; }

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
