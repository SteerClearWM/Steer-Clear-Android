package Steer.Clear;

import android.app.Application;

import Steer.Clear.dagger.ApplicationComponent;
import Steer.Clear.dagger.ApplicationModule;
import Steer.Clear.dagger.DaggerApplicationComponent;

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
