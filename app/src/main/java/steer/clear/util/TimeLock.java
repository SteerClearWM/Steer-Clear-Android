package steer.clear.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Miles Peele on 8/29/2015.
 */
public class TimeLock {

    private final static Date thursdayStart = createThursdayStartTime();
    private final static Date thursdayEnd = createThursdayEndTime();

    public static boolean isSteerClearRunning() {
//        Calendar calendar = Calendar.getInstance();
//        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
//            Date rightNow = calendar.getTime();
//
//            calendar.set(Calendar.HOUR, 9);
//            calendar.set(Calendar.MINUTE, 30);
//            calendar.set(Calendar.AM_PM, Calendar.PM);
//            Date startSteerClear = calendar.getTime();
//
//            calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
//            calendar.set(Calendar.HOUR, 1);
//            calendar.set(Calendar.MINUTE, 30);
//            calendar.set(Calendar.AM_PM, Calendar.AM);
//            Date endSteerClear = calendar.getTime();
//            return rightNow.after(startSteerClear) && rightNow.before(endSteerClear);
//        }
//
//        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
//            Date rightNow = calendar.getTime();
//
//            calendar.set(Calendar.HOUR, 1);
//            calendar.set(Calendar.MINUTE, 30);
//            calendar.set(Calendar.AM_PM, Calendar.AM);
//            Date endSteerClear = calendar.getTime();
//            return rightNow.before(endSteerClear);
//        }
        Logger.log("THURSDAY START: " + thursdayStart);
        Logger.log("THURSDAY END: " + thursdayEnd);
        return false;
    }

    private static Date createThursdayStartTime() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        calendar.set(Calendar.HOUR, 9);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        return calendar.getTime();
    }

    private static Date createThursdayEndTime() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        calendar.set(Calendar.HOUR, 1);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        return calendar.getTime();
    }
}
