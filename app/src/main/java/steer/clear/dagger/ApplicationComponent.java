package steer.clear.dagger;

import javax.inject.Singleton;

import dagger.Component;
import steer.clear.activity.ActivityEta;
import steer.clear.activity.ActivityHome;
import steer.clear.asynctask.AsyncAddRide;
import steer.clear.asynctask.AsyncDelete;

/**
 * Created by Miles Peele on 6/20/2015.
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    void inject(ActivityHome activity);
    void inject(ActivityEta activity);
    void inject(AsyncAddRide asyncAddRide);
    void inject(AsyncDelete asyncDelete);

}
