package steerclear.wm.data.retrofit;

import android.app.Application;


import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import steerclear.wm.MainApp;
import steerclear.wm.R;
import steerclear.wm.data.model.LoginPost;
import steerclear.wm.data.model.RegisterPost;
import steerclear.wm.data.model.RideObject;
import steerclear.wm.data.model.RidePost;
import steerclear.wm.data.DataStore;

public class SteerClearClient {

    private ISteerClearApi api;
    private ISteerClearAuth auth;

    @Inject
    DataStore store;

	public SteerClearClient(Application application) {
        ((MainApp) application).getApplicationComponent().inject(this);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        SteerClearInterceptor steerClearInterceptor = new SteerClearInterceptor(store);
        CookieJar cookieJar = new SteerClearCookieHandler(store);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .followRedirects(true)
//                .followSslRedirects(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(steerClearInterceptor)
//                .cookieJar(cookieJar)
                .build();

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(application.getResources().getString(R.string.url_base))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        api = restAdapter.create(ISteerClearApi.class);

        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(application.getResources().getString(R.string.url_authenticate))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        auth = adapter.create(ISteerClearAuth.class);
	}

    public Observable<ResponseBody> checkCookie() {
        return api.checkCookie();
    }

    public Observable<ResponseBody> login(String username, String password) {
        return auth.login(new LoginPost(username, password));
    }

    public Observable<ResponseBody> register(String username, String password, String phone) {
        return auth.register(new RegisterPost(username, password, phone));
    }

	public Observable<RideObject> addRide(final Integer numPassengers,
                        final Double startLag, final Double startLong,
			            final Double endLat, final Double endLong) {
        return api.addRide(new RidePost(numPassengers, startLag, startLong, endLat, endLong));
	}

	public Observable<ResponseBody> cancelRide(int cancelId) {
        return api.deleteRide(cancelId);
	}

    public Observable<ResponseBody> checkRideStatus(int cancelId) { return api.checkRideStatus(cancelId); }

    public Observable<ResponseBody> logout() { return auth.logout(); }
}
