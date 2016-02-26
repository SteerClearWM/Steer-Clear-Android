package steerclear.wm.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Miles Peele on 8/29/2015.
 */
public class TimeLock {

    private final static Date thursdayStart = createThursdayStartTime();
    private final static Date thursdayEnd = createThursdayEndTime();
    private final static Date fridayStart = createFridayStartTime();
    private final static Date fridayEnd = createFridayEndTIme();
    private final static Date saturdayStart = createSaturdayStartTime();
    private final static Date saturdayEnd = createSaturdayEndTime();

    public static boolean isSteerClearRunning() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.US);
        Date rightNow = calendar.getTime();
        int ampm = calendar.get(Calendar.AM_PM);
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.THURSDAY:
                return rightNow.after(thursdayStart);
            case Calendar.FRIDAY:
                if (ampm == Calendar.AM) {
                    return rightNow.before(thursdayEnd);
                } else {
                    return rightNow.after(fridayStart);
                }
            case Calendar.SATURDAY:
                if (ampm == Calendar.AM) {
                    return rightNow.before(fridayEnd);
                } else {
                    return rightNow.after(saturdayStart);
                }
            case Calendar.SUNDAY:
                return rightNow.before(saturdayEnd);
        }

        return false;
    }

    private static Date createThursdayStartTime() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 30);
        return calendar.getTime();
    }

    private static Date createThursdayEndTime() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 30);
        return calendar.getTime();
    }

    private static Date createFridayStartTime() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 30);
        return calendar.getTime();
    }

    private static Date createFridayEndTIme() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 30);
        return calendar.getTime();
    }

    private static Date createSaturdayStartTime() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 30);
        return calendar.getTime();
    }

    private static Date createSaturdayEndTime() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 30);
        return calendar.getTime();
    }


}