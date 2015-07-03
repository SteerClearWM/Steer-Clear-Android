package steer.clear.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import steer.clear.service.RetrofitClient;

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
    public RetrofitClient getHttpService() {
        return new RetrofitClient();
    }
}
