package steer.clear.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Miles Peele on 7/25/2015.
 */
public class Datastore {

    private SharedPreferences prefs;
    private final static String HAS_REGISTER = "register";
    private final static String ETA = "eta";
    private final static String CANCEL = "CANCEL_ID";
    private final static String USER = "username";

    public Datastore(Application application) {
        prefs = application.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return prefs.edit();
    }

    private SharedPreferences getPrefs() {
        return prefs;
    }

    public void userHasRegistered() {
        getEditor().putBoolean(HAS_REGISTER, true).commit();
    }

    public boolean checkRegistered() {
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
        return !getPrefs().getString(ETA, "").isEmpty();
    }
}
