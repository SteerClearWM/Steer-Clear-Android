package steerclear.wm.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import steerclear.wm.data.DataStore;
import steerclear.wm.data.retrofit.SteerClearClient;

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
    public SteerClearClient getClient() {
        return new SteerClearClient(mApplication);
    }

    @Provides
    @Singleton
    public DataStore getDatastore(Application mApplication) {
        return new DataStore(mApplication);
    }

}
