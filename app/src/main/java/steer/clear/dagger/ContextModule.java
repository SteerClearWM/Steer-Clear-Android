package steer.clear.dagger;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import steer.clear.service.ServiceHttp;

/**
 * Created by Miles Peele on 6/20/2015.
 */
@Module
public class ContextModule {

    private Context context;

    public ContextModule(Context context) {
        this.context = context;
    }

    @Provides
    public Context provideAppContext() {
        return context;
    }

    @Provides
    @Singleton
    public ServiceHttp getHttpService() {
        return new ServiceHttp();
    }

    @Provides
    @Singleton
    public GoogleApiClient getGoogleApiClient(Context context) {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage((AppCompatActivity) context, 0, (GoogleApiClient.OnConnectionFailedListener) context)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) context)
                .addApi(LocationServices.API)
                .build();
        return mGoogleApiClient;
    }

    @Provides
    @Singleton
    public ProgressDialog getProgressDialog(Context context) {
        ProgressDialog httpProgress = new ProgressDialog(context, ProgressDialog.STYLE_HORIZONTAL);
        httpProgress.setMessage("One moment please...");
        return httpProgress;
    }
}
