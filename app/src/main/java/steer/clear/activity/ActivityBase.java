package steer.clear.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by mbpeele on 1/1/16.
 */
public class ActivityBase extends AppCompatActivity {

    public boolean hasInternet() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
