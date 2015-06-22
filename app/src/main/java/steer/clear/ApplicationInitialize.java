package steer.clear;

import android.app.Application;

import steer.clear.dagger.ContextModule;

public class ApplicationInitialize extends Application {

    private ContextModule mContextModule;
 
    @Override
    public void onCreate() {
        super.onCreate();
        mContextModule = new ContextModule(this);
    }

    public ContextModule getApplicationModule() {
        return mContextModule;
    }
}