package steerclear.wm.dagger;

import javax.inject.Singleton;

import dagger.Component;
import steerclear.wm.activity.ActivityBase;
import steerclear.wm.fragment.FragmentAuthenticate;
import steerclear.wm.fragment.FragmentHailRide;
import steerclear.wm.fragment.FragmentMap;
import steerclear.wm.retrofit.Client;
import steerclear.wm.view.ViewHeader;
import steerclear.wm.view.ViewMarkerSelectLayout;

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
