package steerclear.wm.dagger;

import javax.inject.Singleton;

import dagger.Component;
import steerclear.wm.ui.activity.BaseActivity;
import steerclear.wm.ui.fragment.AuthenticateFragment;
import steerclear.wm.ui.fragment.HailRideFragment;
import steerclear.wm.ui.fragment.MapFragment;
import steerclear.wm.data.retrofit.Client;
import steerclear.wm.ui.view.ViewHeader;
import steerclear.wm.ui.view.ViewMarkerSelectLayout;

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
