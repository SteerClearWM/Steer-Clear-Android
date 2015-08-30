package steer.clear.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Miles Peele on 7/19/2015.
 */
public class FontUtils {

    private static HashMap<String, Typeface> mFontMap;

    private static void initializeFontMap(Context context) {
        mFontMap = new HashMap<>();
        AssetManager assetManager = context.getAssets();
        try {
            String[] fontFileNames = assetManager.list("fonts");
            for (String fontFileName : fontFileNames) {
                Typeface typeface = Typeface.createFromAsset(assetManager, "fonts/" + fontFileName);
                mFontMap.put(fontFileName, typeface);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Typeface getStaticTypeFace(Context context, String fontFileName) {
        if (mFontMap == null) {
            initializeFontMap(context);
        }
        Typeface typeface = mFontMap.get(fontFileName);
        if (typeface == null) {
            throw new IllegalArgumentException(
                    "Font name must match file name in assets/fonts/ directory: " + fontFileName);
        }
        return typeface;
    }

    public static boolean isSteerClearRunning() {
        Calendar calendar = Calendar.getInstance();
        Date rightNow = calendar.getTime();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            Date thursdayStart = new Date();
            Date thursdayEnd = new Date();
            Logger.log("THURS START: " + thursdayStart);
            Logger.log("THURS END: " + thursdayEnd);
            return rightNow.after(thursdayStart) && rightNow.before(thursdayEnd);
        }
        return false;
    }
}
