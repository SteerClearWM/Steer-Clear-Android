package Steer.Clear.dagger;

import javax.inject.Singleton;

import dagger.Component;
import Steer.Clear.activity.ActivityBase;
import Steer.Clear.fragment.FragmentAuthenticate;
import Steer.Clear.fragment.FragmentHailRide;
import Steer.Clear.fragment.FragmentMap;
import Steer.Clear.retrofit.Client;
import Steer.Clear.view.ViewHeader;
import Steer.Clear.view.ViewMarkerSelectLayout;

/**
 * Created by Miles Peele on 6/20/2015.
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    void inject(ActivityBase activityBase);

    void inject(FragmentMap fragmentMap);
    void inject(FragmentHailRide fragmentHailRide);
    void inject(FragmentAuthenticate fragmentAuthenticate);

    void inject(Client client);

    void inject(ViewMarkerSelectLayout markerSelectLayout);
    void inject(ViewHeader viewHeader);
}
