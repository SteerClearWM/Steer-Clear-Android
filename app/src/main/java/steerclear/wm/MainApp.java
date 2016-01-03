package steerclear.wm;

import android.app.Application;

import steerclear.wm.dagger.ApplicationComponent;
import steerclear.wm.dagger.ApplicationModule;
import steerclear.wm.dagger.DaggerApplicationComponent;

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
