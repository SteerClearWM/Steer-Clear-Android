package steer.clear.util;

import android.util.Log;

public class Logger {

	public static void log(String string) {
		Log.d("Miles", string);
	}

	public static void log(String string, Throwable throwable) {
		Log.d("Miles", string, throwable);
	}

    public static void log(String... strings) {
        for (String toPrint: strings) {
            Log.d("Miles", toPrint);
        }
    }

    public static void log(Throwable throwable, String... strings) {
        for (String toPrint: strings) {
            Log.d("Miles", toPrint);
        }

        Log.d("Miles", "", throwable);
    }
}
