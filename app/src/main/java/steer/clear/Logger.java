package steer.clear;

import android.util.Log;

/**
 * Simple helper class to help with logging.
 * Having to type the tag "Miles" every time I wanted ot log something was getting real annoying.
 * @author Miles Peele
 *
 */
public class Logger {

	public static void log(String string) {
		Log.v("Miles", string);
	}
}
