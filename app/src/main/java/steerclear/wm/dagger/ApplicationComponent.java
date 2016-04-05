package steerclear.wm.dagger;

import javax.inject.Singleton;

import dagger.Component;
import steerclear.wm.data.retrofit.SteerClearClient;
import steerclear.wm.ui.activity.BaseActivity;
import steerclear.wm.ui.fragment.BaseFragment;
import steerclear.wm.ui.fragment.HailRideFragment;
import steerclear.wm.ui.fragment.MapFragment;
import steerclear.wm.ui.view.ViewHeader;

/**
 * Created by Miles Peele on 6/20/2015.
 */
@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    void inject(BaseActivity baseActivity);

    void inject(BaseFragment baseFragment);

    void inject(SteerClearClient steerClearClient);

    void inject(ViewHeader viewHeader);
}
