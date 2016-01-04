package steerclear.wm.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;

import butterknife.Bind;
import retrofit.client.Header;
import retrofit.client.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import steerclear.wm.event.EventAuthenticate;
import steerclear.wm.R;
import steerclear.wm.fragment.FragmentAuthenticate;
import steerclear.wm.util.ErrorUtils;
import steerclear.wm.util.Logg;

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
        setContentView(R.layout.activity_authenticate);

        Intent intent = getIntent();

        if (intent != null && intent.getBooleanExtra("shouldLogin", false)) {
            addFragmentAuthenticate();
            return;
        }

        if (store.hasPreviousRideInfo()) {
            startActivity(ActivityEta.newIntent(this, store.getEta(), store.getCancelId()));
            return;
        }

        if (store.hasCookie()) {
            startActivity(ActivityHome.newIntent(this));
        } else {
            addFragmentAuthenticate();
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentAuthenticate == null) {
            super.onBackPressed();
        } else {
            if (!fragmentAuthenticate.onBackPressed()) {
                super.onBackPressed();
            }
        }
    }

    private void addFragmentAuthenticate() {
        fragmentAuthenticate = FragmentAuthenticate.newInstance();

        getFragmentManager().beginTransaction()
                .add(R.id.activity_authenticate_root, fragmentAuthenticate)
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
        Subscriber<Response> registerSubscriber = new Subscriber<Response>() {
            @Override
            public void onCompleted() {
                store.putUserHasRegistered();
                store.putUsername(username);

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
            }

            @Override
            public void onStart() {
                super.onStart();
                fragmentAuthenticate.toggleAnimation();
            }
        };

        addSubscription(helper.register(username, password, phone)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(registerSubscriber));
    }

    private void login() {
        Subscriber<Response> loginSubscriber = new Subscriber<Response>() {
            @Override
            public void onCompleted() {
                removeSubscription(this);

                startActivity(ActivityHome.newIntent(ActivityAuthenticate.this));
                finish();
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

            @Override
            public void onStart() {
                super.onStart();
                if (fragmentAuthenticate != null && !fragmentAuthenticate.isAnimating()) {
                    fragmentAuthenticate.toggleAnimation();
                }
            }
        };

        addSubscription(helper.login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginSubscriber));
    }

    public void contact() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {
                getResources().getString(R.string.contact_email)
        });
        intent.putExtra(Intent.EXTRA_SUBJECT, "Steer Clear Question from the Android App");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            handleError(getString(R.string.snackbar_no_email));
        }
    }
}
