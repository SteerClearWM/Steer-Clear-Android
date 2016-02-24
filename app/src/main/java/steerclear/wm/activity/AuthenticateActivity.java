package steerclear.wm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;

import butterknife.Bind;
import icepick.State;
import retrofit.client.Header;
import retrofit.client.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import steerclear.wm.event.EventAuthenticate;
import steerclear.wm.R;
import steerclear.wm.fragment.AuthenticateFragment;
import steerclear.wm.util.ActivitySubscriber;
import steerclear.wm.util.ErrorUtils;

public class AuthenticateActivity extends BaseActivity {

    @Bind(R.id.fragment_authenticate_logo) AppCompatImageView logo;

    private final static String RELOGIN = "relogin";

    @State String username, password, phone;
    private AuthenticateFragment authenticateFragment;

    public static Intent newIntent(Context context, boolean shouldLogin) {
        Intent intent = new Intent(context, AuthenticateActivity.class);
        intent.putExtra(RELOGIN, shouldLogin);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        Intent intent = getIntent();

        if (intent != null && intent.getBooleanExtra(RELOGIN, false)) {
            addFragmentAuthenticate();
            return;
        }

        if (store.hasPreviousRideInfo()) {
            startActivity(EtaActivity.newIntent(this, store.getEta(), store.getCancelId()));
            return;
        }

        if (store.hasCookie()) {
            startActivity(HomeActivity.newIntent(this));
        } else {
            addFragmentAuthenticate();
        }
    }

    @Override
    public void onBackPressed() {
        if (authenticateFragment == null) {
            super.onBackPressed();
        } else {
            if (!authenticateFragment.onBackPressed()) {
                super.onBackPressed();
            }
        }
    }

    private void addFragmentAuthenticate() {
        authenticateFragment = AuthenticateFragment.newInstance();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_authenticate_root, authenticateFragment)
                .commit();
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
        Subscriber<Response> registerSubscriber = new ActivitySubscriber<Response>(this) {
            @Override
            public void onCompleted() {
                store.putUserHasRegistered();
                store.putUsername(username);
                login();
            }

            @Override
            public void onError(Throwable throwable) {
                if (ErrorUtils.getErrorCode(throwable) == 409) {
                    onCompleted();
                } else {
                    authenticateFragment.toggleAnimation();
                    handleError(throwable, R.string.snackbar_invalid_creds);
                }
            }

            @Override
            public void onNext(Response response) {
            }

            @Override
            public void onStart() {
                super.onStart();
                authenticateFragment.toggleAnimation();
            }
        };

        helper.register(username, password, phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(registerSubscriber);
    }

    private void login() {
        Subscriber<Response> loginSubscriber = new ActivitySubscriber<Response>(this) {
            @Override
            public void onCompleted() {
                startActivity(HomeActivity.newIntent(AuthenticateActivity.this));
                finish();
            }

            @Override
            public void onError(Throwable throwable) {
                authenticateFragment.toggleAnimation();
                handleError(throwable, R.string.snackbar_invalid_creds);
            }

            @Override
            public void onNext(Response response) {
                for (Header header: response.getHeaders()) {
                    if (header.getName().contains("Set-Cookie")) {
                        store.putCookie(header.getValue());
                    }
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                if (authenticateFragment != null && !authenticateFragment.isAnimating()) {
                    authenticateFragment.toggleAnimation();
                }
            }
        };

        helper.login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginSubscriber);
    }
}
