package steer.clear.activity;

import android.app.Dialog;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import steer.clear.event.EventAuthenticate;
import steer.clear.event.EventGoToRegister;
import steer.clear.util.Datastore;
import steer.clear.MainApp;
import steer.clear.R;
import steer.clear.fragment.FragmentAuthenticate;
import steer.clear.retrofit.Client;
import steer.clear.util.ErrorDialog;
import steer.clear.util.Locationer;
import steer.clear.util.Logger;


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
            helper.login(new WeakReference<>(this),
                    eventAuthenticate.username, eventAuthenticate.password);
        } else {
            helper.register(new WeakReference<>(this),
                    eventAuthenticate.username, eventAuthenticate.password, eventAuthenticate.phone);
        }
    }

    public void onRegisterSuccess() {
        store.userHasRegistered();
        Logger.log("ON REGISTER SUCESSS");
    }

    public void onRegisterError(int errorCode) {
        Logger.log("ON REGISTER ERROR: " + errorCode);
        FragmentAuthenticate fragmentAuthenticate = (FragmentAuthenticate) getFragmentManager().findFragmentByTag(AUTHENTICATE_TAG);
        fragmentAuthenticate.togglePulse();
        Dialog error = ErrorDialog.createFromErrorCode(this, errorCode);
        if (errorCode == 409) {
            store.userHasRegistered();
            error.setOnDismissListener(dialog -> {
                helper.login(new WeakReference<>(this),
                        fragmentAuthenticate.getUsername(),
                        fragmentAuthenticate.getPassword());
            });
        }
        error.show();
    }

    public void onLoginSuccess() {
        Logger.log("ON login SUCCESS");
        startActivity(ActivityHome.newIntent(this));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void onLoginError(int errorCode) {
        Logger.log("ON login ERROR: " + errorCode);
        FragmentAuthenticate fragmentAuthenticate = (FragmentAuthenticate) getFragmentManager().findFragmentByTag(AUTHENTICATE_TAG);
        if (fragmentAuthenticate != null) {
            fragmentAuthenticate.togglePulse();
        }
        ErrorDialog.createFromErrorCode(this, errorCode).show();
    }

}
