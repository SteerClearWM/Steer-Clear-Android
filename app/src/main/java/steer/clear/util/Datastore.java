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
}
