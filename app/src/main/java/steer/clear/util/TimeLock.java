package steer.clear.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeLock {

    private final static Date thursdayStart = createThursdayStartTime();
    private final static Date thursdayEnd = createThursdayEndTime();
    private final static Date fridayStart = createFridayStartTime();
    private final static Date fridayEnd = createFridayEndTIme();
    private final static Date saturdayStart = createSaturdayStartTime();
    private final static Date saturdayEnd = createSaturdayEndTime();

    public static boolean isSteerClearRunning() {
        Calendar calendar = Calendar.getInstance();
        Date rightNow = calendar.getTime();
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.THURSDAY:
                return rightNow.after(thursdayStart);
            case Calendar.FRIDAY:
                return (calendar.get(Calendar.AM_PM) == Calendar.AM) ?
                        rightNow.before(thursdayEnd) : rightNow.after(fridayStart);
            case Calendar.SATURDAY:
                return (calendar.get(Calendar.AM_PM) == Calendar.AM) ?
                        rightNow.before(fridayEnd) : rightNow.after(saturdayStart);
            case Calendar.SUNDAY:
                return rightNow.before(saturdayEnd);
        }
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

    private static Date createFridayStartTime() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        calendar.set(Calendar.HOUR, 9);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        return calendar.getTime();
    }

    private static Date createFridayEndTIme() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        calendar.set(Calendar.HOUR, 2);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        return calendar.getTime();
    }

    private static Date createSaturdayStartTime() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        calendar.set(Calendar.HOUR, 9);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        return calendar.getTime();
    }

    private static Date createSaturdayEndTime() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.set(Calendar.HOUR, 2);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        return calendar.getTime();
    }
}
