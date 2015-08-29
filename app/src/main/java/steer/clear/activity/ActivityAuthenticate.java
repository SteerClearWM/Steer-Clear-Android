package steer.clear.activity;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import steer.clear.event.EventAuthenticate;
import steer.clear.event.EventGoToRegister;
import steer.clear.util.Datastore;
import steer.clear.MainApp;
import steer.clear.R;
import steer.clear.fragment.FragmentAuthenticate;
import steer.clear.retrofit.Client;
import steer.clear.util.ErrorDialog;


public class ActivityAuthenticate extends AppCompatActivity {

    @Inject Client helper;
    @Inject Datastore store;
    @Inject EventBus bus;

    private static final String AUTHENTICATE_TAG = "authenticate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainApp) getApplication()).getApplicationComponent().inject(this);

        if (store.hasPreviousRideInfo()) {
            startActivity(ActivityEta.newIntent(this, store.getEta(), store.getCancelId()));
            finish();
        } else {
            setContentView(R.layout.activity_authenticate);

            bus.register(this);

            addFragmentAuthenticate();
        }
    }

    private void addFragmentAuthenticate() {
        FragmentManager manager = getFragmentManager();
        FragmentAuthenticate login = (FragmentAuthenticate) manager.findFragmentByTag(AUTHENTICATE_TAG);
        if (login != null) {
            manager.beginTransaction().show(login).commit();
        } else {
            manager.beginTransaction()
                    .add(R.id.activity_authenticate_root,
                            FragmentAuthenticate.newInstance(store.checkRegistered()), AUTHENTICATE_TAG)
                    .commit();
        }
    }

    public void onEvent(EventGoToRegister eventGoToRegister) {
        getFragmentManager().beginTransaction()
                .replace(R.id.activity_authenticate_root,
                        FragmentAuthenticate.newInstance(false), AUTHENTICATE_TAG)
                .commit();
    }

    public void onEvent(EventAuthenticate eventAuthenticate) {
        if (eventAuthenticate.registered) {
            helper.login(eventAuthenticate.username, eventAuthenticate.password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onLoginSuccess, this::onRegisterError);
        } else {
            helper.register(eventAuthenticate.username, eventAuthenticate.password, eventAuthenticate.phone)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Response -> onRegisterSuccess(eventAuthenticate.username,
                            eventAuthenticate.password), this::onRegisterError);
        }
    }

    public void onRegisterSuccess(String username, String password) {
        store.userHasRegistered();
        helper.login(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onLoginSuccess, this::onLoginError);
    }

    public void onRegisterError(Throwable throwable) {
        throwable.printStackTrace();
        toggleLoadingAnimation();
        if (throwable instanceof RetrofitError) {
            RetrofitError error = (RetrofitError) throwable;
            if (error.getResponse() != null) {
                Dialog errorDialog = ErrorDialog.createFromErrorCode(this, error.getResponse().getStatus());
                if (error.getResponse().getStatus() == 409) {
                    store.userHasRegistered();
                    errorDialog.setOnDismissListener(dialog -> {
                        toggleLoadingAnimation();
                        helper.login(getFragmentUsername(), getFragmentPassword())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(this::onLoginSuccess, this::onLoginError);
                    });
                }
                errorDialog.show();
            } else {
                ErrorDialog.createFromErrorCode(this, 404).show();
            }
        } else {
            ErrorDialog.createFromErrorCode(this, 404).show();
        }
    }

    public void onLoginSuccess(Response response) {
        startActivity(ActivityHome.newIntent(this));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void onLoginError(Throwable throwable) {
        throwable.printStackTrace();
        toggleLoadingAnimation();
        if (throwable instanceof RetrofitError) {
            RetrofitError error = (RetrofitError) throwable;
            ErrorDialog.createFromErrorCode(this, error.getResponse() != null ?
                error.getResponse().getStatus() : 404).show();
        } else {
            ErrorDialog.createFromErrorCode(this, 404).show();
        }
    }

    private void toggleLoadingAnimation() {
        FragmentAuthenticate fragmentAuthenticate = (FragmentAuthenticate) getFragmentManager().findFragmentByTag(AUTHENTICATE_TAG);
        if (fragmentAuthenticate != null) {
            fragmentAuthenticate.togglePulse();
        }
    }

    private String getFragmentUsername() {
        FragmentAuthenticate fragmentAuthenticate = (FragmentAuthenticate) getFragmentManager().findFragmentByTag(AUTHENTICATE_TAG);
        return fragmentAuthenticate != null ? fragmentAuthenticate.getUsername() : "";
    }

    private String getFragmentPassword() {
        FragmentAuthenticate fragmentAuthenticate = (FragmentAuthenticate) getFragmentManager().findFragmentByTag(AUTHENTICATE_TAG);
        return fragmentAuthenticate != null ? fragmentAuthenticate.getPassword() : "";
    }

}
