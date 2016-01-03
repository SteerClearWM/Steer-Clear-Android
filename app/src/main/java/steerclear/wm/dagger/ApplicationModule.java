package steerclear.wm.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import steerclear.wm.retrofit.Client;
import steerclear.wm.util.Datastore;

/**
 * Created by Miles Peele on 6/20/2015.
 */
@Module
public class ApplicationModule {

    private Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    public Application provideAppContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    public Client getClient() {
        return new Client(mApplication);
    }

    @Provides
    @Singleton
    public Datastore getDatastore(Application mApplication) {
        return new Datastore(mApplication);
    }

    @Provides
    @Singleton
    public EventBus getEventBus() {
        return new EventBus();
    }

}
