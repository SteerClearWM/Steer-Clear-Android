package steer.clear;

import android.app.Application;

import steer.clear.dagger.ApplicationComponent;
import steer.clear.dagger.ApplicationModule;
import steer.clear.dagger.DaggerApplicationComponent;

/**
 * Created by milespeele on 7/2/15.
 */
public class MainApp extends Application {

    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return component;
    }
}
