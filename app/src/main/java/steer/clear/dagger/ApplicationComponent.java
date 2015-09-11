package steer.clear.dagger;

import javax.inject.Singleton;

import dagger.Component;
import steer.clear.activity.ActivityAuthenticate;
import steer.clear.activity.ActivityEta;
import steer.clear.activity.ActivityHome;
import steer.clear.fragment.FragmentAuthenticate;
import steer.clear.fragment.FragmentHailRide;
import steer.clear.fragment.FragmentMap;
import steer.clear.retrofit.Client;
import steer.clear.util.ErrorDialog;
import steer.clear.view.ViewHeader;
import steer.clear.view.ViewMarkerSelectLayout;

/**
 * Created by Miles Peele on 6/20/2015.
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    void inject(ActivityHome activity);
    void inject(ActivityEta activity);
    void inject(ActivityAuthenticate activityAuthenticate);

    void inject(FragmentMap fragmentMap);
    void inject(FragmentHailRide fragmentHailRide);
    void inject(FragmentAuthenticate fragmentAuthenticate);

    void inject(Client client);

    void inject(ViewMarkerSelectLayout markerSelectLayout);
    void inject(ViewHeader viewHeader);
}
