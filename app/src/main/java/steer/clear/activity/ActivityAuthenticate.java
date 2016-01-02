package steer.clear.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatImageView;

import javax.inject.Inject;

import butterknife.Bind;
import de.greenrobot.event.EventBus;
import retrofit.client.Header;
import retrofit.client.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import steer.clear.event.EventAuthenticate;
import steer.clear.util.Datastore;
import steer.clear.MainApp;
import steer.clear.R;
import steer.clear.fragment.FragmentAuthenticate;
import steer.clear.retrofit.Client;
import steer.clear.util.ErrorUtils;

public class ActivityAuthenticate extends ActivityBase {

    @Inject Client helper;
    @Inject Datastore store;
    @Inject EventBus bus;

    @Bind(R.id.fragment_authenticate_logo) AppCompatImageView logo;

    private String username, password, phone;
    private Subscriber<Response> loginSubscriber, registerSubscriber;
    private FragmentAuthenticate fragmentAuthenticate;

    public static Intent newIntent(Context context, boolean shouldLogin) {
        return new Intent(context, ActivityAuthenticate.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainApp) getApplication()).getApplicationComponent().inject(this);

        bus.register(this);

        if (store.hasCookie()) {
            startActivity(ActivityHome.newIntent(this));
        } else {
            fragmentAuthenticate = FragmentAuthenticate.newInstance();

            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, fragmentAuthenticate)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (!fragmentAuthenticate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registerSubscriber != null) {
            registerSubscriber.unsubscribe();
        }

        if (loginSubscriber != null) {
            loginSubscriber.unsubscribe();
        }
    }

    public void onEvent(EventAuthenticate eventAuthenticate) {
        if (hasInternet()) {
            username = eventAuthenticate.username;
            password = eventAuthenticate.password;
            phone = eventAuthenticate.phone;

            if (phone == null) {
                login();
            } else {
                register();
            }
        } else {
            Snackbar.make(fragmentAuthenticate.getView(),
                    ErrorUtils.getMessage(this, ErrorUtils.NO_INTERNET),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    private void register() {
        registerSubscriber = new Subscriber<Response>() {
            @Override
            public void onCompleted() {
                login();
            }

            @Override
            public void onError(Throwable throwable) {
                fragmentAuthenticate.toggleAnimation();
            }

            @Override
            public void onNext(Response response) {
                store.putUserHasRegistered();
                store.putUsername(username);
            }
        };

        helper.register(username, password, phone)
                .doOnError(throwable -> showAuthenticationFailedSnackbar())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(registerSubscriber);

        fragmentAuthenticate.toggleAnimation();
    }

    private void login() {
        loginSubscriber = new Subscriber<Response>() {
            @Override
            public void onCompleted() {
                fragmentAuthenticate.toggleAnimation();
                startActivity(ActivityHome.newIntent(ActivityAuthenticate.this));
            }

            @Override
            public void onError(Throwable e) {
                fragmentAuthenticate.toggleAnimation();
            }

            @Override
            public void onNext(Response response) {
                for (Header header: response.getHeaders()) {
                    if (header.getName().contains("Set-Cookie")) {
                        store.putCookie(header.getValue());
                        break;
                    }
                }
            }
        };

        helper.login(username, password)
                .doOnError(throwable -> showAuthenticationFailedSnackbar())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginSubscriber);

        if (!fragmentAuthenticate.isAnimating()) {
            fragmentAuthenticate.toggleAnimation();
        }
    }

    public void showAuthenticationFailedSnackbar() {
        Snackbar.make(fragmentAuthenticate.getView(),
                R.string.fragment_authenticate_fail,
                Snackbar.LENGTH_LONG).show();
    }
}
