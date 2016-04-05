package steerclear.wm.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Cookie;
import steerclear.wm.data.model.StorableCookie;

public class DataStore {

    private final static String HAS_REGISTER = "register";
    private final static String ETA = "eta";
    private final static String CANCEL = "CANCEL_ID";
    private final static String USER = "username";
    private final static String COOKIE = "cookie";

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

    public void putRideInfo(String eta, int cancelId) {
        SharedPreferences.Editor editor = getEditor();
        editor.putString(ETA, eta);
        editor.putInt(CANCEL, cancelId);
        editor.commit();
    }

    public void clearRideInfo() {
        SharedPreferences.Editor editor = getEditor();
        editor.putString(ETA, "");
        editor.putString(CANCEL, "");
        editor.commit();
    }

    public int getCancelId() {
        return getPrefs().getInt(CANCEL, -1);
    }

    public String getEta() {
        return getPrefs().getString(ETA, "");
    }

    public boolean hasPreviousRideInfo() {
        boolean hasPrevInfo = !getPrefs().getString(ETA, "").isEmpty();

        if (hasPrevInfo) {
            String[] time = getEta().split(":");
            int hour = Integer.valueOf(time[0].replace(" ", ""));

            Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.US);
            int hourNow = calendar.get(Calendar.HOUR);

            return Math.abs(hourNow - hour) <= 3;
        }

        return hasPrevInfo;
    }

//    public void putCookie(String cookie) {
//        getEditor().putString(COOKIE, cookie).commit();
////        getEditor().putString(COOKIE, gson.toJson(cookie)).commit();
//    }
//
//    public String getCookie() {
////        String json = prefs.getString(COOKIE, "");
////        return gson.fromJson(json, Cookie.class);
//        return prefs.getString(COOKIE, "");
//    }

    public void putCookie(Cookie cookie) {
        getEditor().putString(COOKIE, gson.toJson(new StorableCookie(cookie))).commit();
    }

    public Cookie getCookie() {
        String cookieString = getPrefs().getString(COOKIE, "");
        StorableCookie storableCookie = gson.fromJson(cookieString, StorableCookie.class);
        if (storableCookie == null) {
            return null;
        }

        return StorableCookie.toCookie(storableCookie);
    }

    public boolean hasCookie() {
        return !getPrefs().getString(COOKIE, "").isEmpty();
    }
}
