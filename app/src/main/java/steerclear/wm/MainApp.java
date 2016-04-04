package steerclear.wm;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import steerclear.wm.dagger.ApplicationComponent;
import steerclear.wm.dagger.ApplicationModule;
import steerclear.wm.dagger.DaggerApplicationComponent;
import steerclear.wm.util.Logg;
import steerclear.wm.util.TimeLock;

/**
 * Created by milespeele on 7/2/15.
 */
public class MainApp extends Application {

    private ApplicationComponent component;
    private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.string.trackingKey);
        }
        return mTracker;
    }

    synchronized public ApplicationComponent getApplicationComponent() {
        return component;
    }
}
