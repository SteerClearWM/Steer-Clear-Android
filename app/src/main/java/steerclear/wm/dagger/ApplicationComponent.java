package steerclear.wm.dagger;

import javax.inject.Singleton;

import dagger.Component;
import steerclear.wm.activity.BaseActivity;
import steerclear.wm.fragment.AuthenticateFragment;
import steerclear.wm.fragment.HailRideFragment;
import steerclear.wm.fragment.MapFragment;
import steerclear.wm.retrofit.Client;
import steerclear.wm.view.ViewHeader;
import steerclear.wm.view.ViewMarkerSelectLayout;

/**
 * Created by Miles Peele on 6/20/2015.
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    void inject(BaseActivity baseActivity);

    void inject(MapFragment mapFragment);
    void inject(HailRideFragment hailRideFragment);
    void inject(AuthenticateFragment authenticateFragment);

    void inject(Client client);

    void inject(ViewMarkerSelectLayout markerSelectLayout);
    void inject(ViewHeader viewHeader);
}
