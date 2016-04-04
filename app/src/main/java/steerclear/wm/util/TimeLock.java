package steerclear.wm.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Miles Peele on 8/29/2015.
 */
public class TimeLock {

    public static boolean isSteerClearRunning() {
        DateTime dateTime = new DateTime();
        int hour = dateTime.getHourOfDay();
        int minute = dateTime.getMinuteOfHour();
        boolean morningEnd = hour <= 1 && minute <= 30;
        boolean nightStart = hour >= 21 && minute >= 30;

        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.THURSDAY:
                return hour >= 22 && minute >= 30;
            case DateTimeConstants.FRIDAY:
                return morningEnd || nightStart;
            case DateTimeConstants.SATURDAY:
                return morningEnd || nightStart;
            case DateTimeConstants.SUNDAY:
                return hour <= 2 && hour <= 30;
            default:
                return false;
        }
    }

}