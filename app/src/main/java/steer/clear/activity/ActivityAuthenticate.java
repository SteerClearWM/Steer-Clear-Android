package Steer.Clear.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;

import butterknife.Bind;
import retrofit.client.Header;
import retrofit.client.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import Steer.Clear.event.EventAuthenticate;
import Steer.Clear.R;
import Steer.Clear.fragment.FragmentAuthenticate;
import Steer.Clear.util.ErrorUtils;

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

        if (store.hasPreviousRideInfo()) {
            Intent eta = ActivityEta.newIntent(this, store.getEta(), store.getCancelId());
            eta.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(eta);
            return;
        }

        if (store.hasCookie()) {
            Intent home = ActivityHome.newIntent(this);
            home.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(home);
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

                Intent home = ActivityHome.newIntent(ActivityAuthenticate.this);
                home.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(home);
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

    public void contact() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"steerclear@email.wm.edu"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Steer Clear Question from the Android App");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            handleError(getString(R.string.snackbar_no_email));
        }
    }
}
