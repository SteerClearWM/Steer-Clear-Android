package steer.clear.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import steer.clear.retrofit.Client;
import steer.clear.util.Datastore;
import steer.clear.util.Locationer;

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

    @Provides
    @Singleton
    public Locationer getLocationer() { return new Locationer(mApplication); }
}
