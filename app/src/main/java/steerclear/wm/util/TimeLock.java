package steerclear.wm.util;

import android.app.AlertDialog;
import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import steerclear.wm.R;

/**
 * Created by Miles Peele on 8/29/2015.
 */
public class TimeLock {

    public static boolean isSteerClearRunning() {
//        DateTime dateTime = new DateTime();
//        int hour = dateTime.getHourOfDay();
//        int minute = dateTime.getMinuteOfHour();
//        boolean morningEnd = hour <= 1 && minute <= 30;
//        boolean nightStart = hour >= 21 && minute >= 30;
//
//        switch (dateTime.getDayOfWeek()) {
//            case DateTimeConstants.THURSDAY:
//                return hour >= 22 && minute >= 30;
//            case DateTimeConstants.FRIDAY:
//                return morningEnd || nightStart;
//            case DateTimeConstants.SATURDAY:
//                return morningEnd || nightStart;
//            case DateTimeConstants.SUNDAY:
//                return hour <= 2 && hour <= 30;
//            default:
//                return false;
//        }
        return true;
    }

    public static void showTimeLockDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.error_dialog_not_running_title)
                .setMessage(R.string.error_dialog_not_running_body)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

}