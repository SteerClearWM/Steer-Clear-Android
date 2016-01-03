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
import rx.schedulers.Schedulers;
import steer.clear.event.EventAuthenticate;
import steer.clear.util.Datastore;
import steer.clear.MainApp;
import steer.clear.R;
import steer.clear.fragment.FragmentAuthenticate;
import steer.clear.retrofit.Client;
import steer.clear.util.ErrorUtils;
import steer.clear.util.Logg;

public class ActivityAuthenticate extends ActivityBase {

    @Bind(R.id.fragment_authenticate_logo) AppCompatImageView logo;

    private String username, password, phone;
    private FragmentAuthenticate fragmentAuthenticate;

    public static Intent newIntent(Context context, boolean shouldLogin) {
        Intent intent = new Intent(context, ActivityAuthenticate.class);
        intent.putExtra("shouldLogin", shouldLogin);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent != null && intent.getBooleanExtra("shouldLogin", false)) {
            addFragmentAuthenticate();
            return;
        }
        if (store.hasCookie()) {
            startActivity(ActivityHome.newIntent(this));
        } else {
            addFragmentAuthenticate();
        }
    }

    private void addFragmentAuthenticate() {
        fragmentAuthenticate = FragmentAuthenticate.newInstance();

        getFragmentManager().beginTransaction()
                .add(android.R.id.content, fragmentAuthenticate)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (!fragmentAuthenticate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(EventAuthenticate eventAuthenticate) {
        username = eventAuthenticate.username;
        password = eventAuthenticate.password;
        phone = eventAuthenticate.phone;

        if (phone == null) {
            login();
        } else {
            register();
        }
    }

    private void register() {
        Subscriber<Response> registerSubscriber = new Subscriber<Response>() {
            @Override
            public void onCompleted() {
                removeSubscription(this);
                login();
            }

            @Override
            public void onError(Throwable throwable) {
                if (ErrorUtils.getErrorCode(throwable) == 409) {
                    onCompleted();
                } else {
                    fragmentAuthenticate.toggleAnimation();
                    handleError(throwable, R.string.snackbar_invalid_creds);
                }
            }

            @Override
            public void onNext(Response response) {
                store.putUserHasRegistered();
                store.putUsername(username);
            }
        };

        addSubscription(helper.register(username, password, phone)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(registerSubscriber));

        fragmentAuthenticate.toggleAnimation();
    }

    private void login() {
        Subscriber<Response> loginSubscriber = new Subscriber<Response>() {
            @Override
            public void onCompleted() {
                removeSubscription(this);
                if (fragmentAuthenticate != null) {
                    fragmentAuthenticate.toggleAnimation();
                }
                startActivity(ActivityHome.newIntent(ActivityAuthenticate.this));
            }

            @Override
            public void onError(Throwable throwable) {
                fragmentAuthenticate.toggleAnimation();
                handleError(throwable, R.string.snackbar_invalid_creds);
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

        addSubscription(helper.login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginSubscriber));

        if (fragmentAuthenticate != null && !fragmentAuthenticate.isAnimating()) {
            fragmentAuthenticate.toggleAnimation();
        }
    }
}
