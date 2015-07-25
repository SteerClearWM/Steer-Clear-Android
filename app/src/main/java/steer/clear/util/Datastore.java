package steer.clear.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

import retrofit.client.Header;
import steer.clear.Logger;

/**
 * Created by Miles Peele on 7/25/2015.
 */
public class Datastore {

    private SharedPreferences prefs;

    public Datastore(Application application) {
        prefs = application.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return prefs.edit();
    }

    private SharedPreferences getPrefs() {
        return prefs;
    }
}
