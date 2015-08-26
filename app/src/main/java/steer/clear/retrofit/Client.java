package steer.clear.retrofit;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.squareup.okhttp.OkHttpClient;

import java.lang.ref.WeakReference;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import steer.clear.MainApp;
import steer.clear.R;
import steer.clear.activity.ActivityAuthenticate;
import steer.clear.activity.ActivityEta;
import steer.clear.activity.ActivityHome;
import steer.clear.pojo.LoginPost;
import steer.clear.pojo.RegisterPost;
import steer.clear.pojo.RideObject;
import steer.clear.pojo.RidePost;
import steer.clear.util.Logger;

public class Client {

    private ApiInterface apiInterface;
    private AuthenticateInterface authenticateInterface;
    private Application application;

    @Inject EventBus bus;

	public Client(Application application) {
        this.application = application;
        ((MainApp) application).getApplicationComponent().inject(this);

        OkHttpClient okHttpClient = new OkHttpClient();
        CookieManager cookieHandler = new CookieManager();
        cookieHandler.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        okHttpClient.setFollowRedirects(true);
        okHttpClient.setCookieHandler(cookieHandler);
        okHttpClient.setConnectTimeout(20, TimeUnit.SECONDS);
        OkClient okClient = new OkClient(okHttpClient);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(application.getResources().getString(R.string.url_base))
                .setClient(okClient)
                .build();
        apiInterface = restAdapter.create(ApiInterface.class);

        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(application.getResources().getString(R.string.url_authenticate))
                .setClient(okClient)
                .build();
        authenticateInterface = adapter.create(AuthenticateInterface.class);
	}

    private boolean checkInternet() {
        ConnectivityManager cm = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void login(final WeakReference<ActivityAuthenticate> weakReference,
                      String username, String password) {
        if (checkInternet()) {
            authenticateInterface.login(new LoginPost(username, password))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            response -> {
                                switch (response.getStatus()) {
                                    case 200:
                                        ActivityAuthenticate activityAuthenticate = weakReference.get();
                                        if (activityAuthenticate != null) {
                                            activityAuthenticate.onLoginSuccess();
                                        }
                                        break;
                                    default:
                                        Logger.log("UNHANDLED LOGIN RESPONSE CODE: " + response.getStatus());
                                }
                            }, throwable -> {
                                throwable.printStackTrace();
                                Logger.log("ERROR WITH LOGIN");
                                if (throwable instanceof RetrofitError) {
                                    RetrofitError error = (RetrofitError) throwable;
                                    ActivityAuthenticate activityAuthenticate = weakReference.get();
                                    if (activityAuthenticate != null) {
                                        activityAuthenticate.onLoginError(error.getResponse().getStatus());
                                    }
                                }
                            });
        } else {
            Logger.log("NO HAS INTERNET");
            ActivityAuthenticate activityAuthenticate = weakReference.get();
            if (activityAuthenticate != null) {
                activityAuthenticate.onRegisterError(404);
            }
        }
    }

    public void register(final WeakReference<ActivityAuthenticate> weakReference,
                         String username, String password, String phone) {
        if (checkInternet()) {
            Logger.log("HAS INTERNET");
            authenticateInterface.register(new RegisterPost(username, password, phone))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            response -> {
                                switch (response.getStatus()) {
                                    case 200:
                                    case 302:
                                        ActivityAuthenticate activityAuthenticate = weakReference.get();
                                        if (activityAuthenticate != null) {
                                            activityAuthenticate.onRegisterSuccess();
                                            login(weakReference, username, password);
                                        }
                                        break;
                                    default:
                                        Logger.log("UNHANDLED REGISTER RESPONSE CODE: " + response.getStatus());
                                }
                            }, throwable -> {
                                throwable.printStackTrace();
                                Logger.log("ERROR WITH REGISTER");
                                if (throwable instanceof RetrofitError) {
                                    RetrofitError error = (RetrofitError) throwable;
                                    ActivityAuthenticate activityAuthenticate = weakReference.get();
                                    if (activityAuthenticate != null) {
                                        activityAuthenticate.onRegisterError(error.getResponse().getStatus());
                                    }
                                } else {
                                    ActivityAuthenticate activityAuthenticate = weakReference.get();
                                    if (activityAuthenticate != null) {
                                        activityAuthenticate.onRegisterError(404);
                                    }
                                }
                            });
        } else {
            Logger.log("NO HAS INTERNET");
            ActivityAuthenticate activityAuthenticate = weakReference.get();
            if (activityAuthenticate != null) {
                activityAuthenticate.onRegisterError(404);
            }
        }
    }
	
	/**
	 * Method used to add a ride to the queue. Parameters must be formatted exactly as shown
	 * If successful, calls through the ServiceHttpInterface onPostSuccess()
	 * If failure, calls through the ServiceHttpInterface onVolleyError()
	 */
	public void addRide(final WeakReference<ActivityHome> weakReference,
                        final Integer num_passengers,
                        final Double start_latitude, final Double start_longitude,
			            final Double end_latitude, final Double end_longitude) {
        if (checkInternet()) {
            apiInterface.addRide(new RidePost(num_passengers, start_latitude,
                    start_longitude, end_latitude, end_longitude))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(rideObject -> {
                        ActivityHome activityHome = weakReference.get();
                        if (activityHome != null) {
                            activityHome.onRideObjectReceived(rideObject);
                        }
                    }, throwable -> {
                        throwable.printStackTrace();
                        Logger.log("ERROR WITH REGISTER");
                        if (throwable instanceof RetrofitError) {
                            RetrofitError error = (RetrofitError) throwable;
                            ActivityHome activityAuthenticate = weakReference.get();
                            if (activityAuthenticate != null) {
                                activityAuthenticate.onRideObjectPostError(error.getResponse().getStatus());
                            }
                        } else {
                            ActivityHome activityAuthenticate = weakReference.get();
                            if (activityAuthenticate != null) {
                                activityAuthenticate.onRideObjectPostError(404);
                            }
                        }
                    });
        } else {
            ActivityHome activityHome = weakReference.get();
            if (activityHome != null) {
                activityHome.onRideObjectPostError(404);
            }
        }
	}

	public void cancelRide(final WeakReference<ActivityEta> weakReference, int cancelId) {
        if (checkInternet()) {
            apiInterface.deleteRide(cancelId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        Logger.log("CANCEL RIDE STATUS CODE: " + response.getStatus());
                        switch (response.getStatus()) {
                            case 200:
                                ActivityEta activityEta = weakReference.get();
                                if (activityEta != null) {
                                    activityEta.onRideCanceled(response);
                                }
                        }
                    }, throwable -> {
                        throwable.printStackTrace();
                        Logger.log("ERROR WITH REGISTER");
                        if (throwable instanceof RetrofitError) {
                            RetrofitError error = (RetrofitError) throwable;
                            ActivityEta activityAuthenticate = weakReference.get();
                            if (activityAuthenticate != null) {
                                activityAuthenticate.onRideCancelError(-1);
                            }
                        } else {
                            ActivityEta activityAuthenticate = weakReference.get();
                            if (activityAuthenticate != null) {
                                activityAuthenticate.onRideCancelError(-1);
                            }
                        }
                    });
        } else {
            ActivityEta activityEta = weakReference.get();
            if (activityEta != null) {
                activityEta.onRideCancelError(404);
            }
        }
	}
}
