package steerclear.wm.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import okhttp3.Cookie;
import steerclear.wm.data.model.RideObject;
import steerclear.wm.data.model.StorableCookie;
import steerclear.wm.util.Logg;

public class DataStore {

    private final static String HAS_REGISTER = "register";
    private final static String ETA = "eta";
    private final static String CANCEL = "cancelId";
    private final static String USER = "username";
    private final static String COOKIE = "cookie";
    private final static String RIDE = "rideObject";

    private SharedPreferences prefs;
    private Gson gson;

    public DataStore(Application application) {
        gson = new Gson();
        prefs = application.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return prefs.edit();
    }

    private SharedPreferences getPrefs() {
        return prefs;
    }

    public void putUserHasRegistered() {
        getEditor().putBoolean(HAS_REGISTER, true).commit();
    }

    public boolean isRegistered() {
        return getPrefs().getBoolean(HAS_REGISTER, false);
    }

    public void putUsername(String username) {
        getEditor().putString(USER, username).commit();
    }

    public String getUsername() {
        return getPrefs().getString(USER, "");
    }

    public void clearRideInfo() {
        getEditor().putString(RIDE, "");
    }

    public void putRideObject(RideObject rideObject) {
        getEditor().putString(RIDE, gson.toJson(rideObject)).commit();
    }

    public RideObject getRideObject() {
        String jsonString = getPrefs().getString(RIDE, "");
        RideObject rideObject = gson.fromJson(jsonString, RideObject.class);
        if (rideObject == null) {
            return null;
        }

        return rideObject;
    }

    public boolean isRideInfoValid() {
        RideObject rideObject = getRideObject();
        if (rideObject == null) {
            return false;
        }

        DateTime now = new DateTime();
        DateTime pickup = rideObject.convertStringToDateTime();
        if (now.isBefore(pickup)) {
            return true;
        } else {
            Minutes minutesBetween = Minutes.minutesBetween(pickup, now);
            return minutesBetween.getMinutes() < 10;
        }
    }

    public void putCookie(Cookie cookie) {
        getEditor().putString(COOKIE, gson.toJson(new StorableCookie(cookie))).commit();
    }

    public Cookie getCookie() {
        String cookieString = getPrefs().getString(COOKIE, "");
        StorableCookie storableCookie = gson.fromJson(cookieString, StorableCookie.class);
        if (storableCookie == null) {
            return null;
        }

        return storableCookie.toCookie();
    }

    public boolean hasCookie() {
        return !getPrefs().getString(COOKIE, "").isEmpty();
    }
}
