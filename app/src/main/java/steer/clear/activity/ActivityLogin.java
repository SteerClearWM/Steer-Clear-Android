package steer.clear.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import retrofit.client.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import steer.clear.Logger;
import steer.clear.MainApp;
import steer.clear.R;
import steer.clear.fragment.FragmentLogin;
import steer.clear.fragment.ListenerForFragments;
import steer.clear.retrofit.Client;

/**
 * Created by milespeele on 7/30/15.
 */
public class ActivityLogin extends AppCompatActivity implements ListenerForFragments {

    @Inject Client helper;
    private AlertDialog error;

    private static final String LOGIN = "authenticate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ((MainApp) getApplication()).getApplicationComponent().inject(this);

        addFragmentLogin();
    }

    private void addFragmentLogin() {
        FragmentManager manager = getFragmentManager();
        FragmentLogin login = (FragmentLogin) manager.findFragmentByTag(LOGIN);
        if (login != null) {
            manager.beginTransaction().show(login).commit();
        } else {
            manager.beginTransaction()
                    .add(R.id.activity_home_fragment_frame, FragmentLogin.newInstance(), LOGIN)
                    .commit();
        }
    }

    public void onRegisterResponse(Response response, String username, String password) {
        switch (response.getStatus()) {
            case 200:
            case 302:
                helper.login(username, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onLoginResponse, this::onRxError);
                break;
            default:
                Logger.log("UNHANDLED RESPONSE CODE: " + response.getStatus());
        }
    }

    public void onLoginResponse(Response response) {
        switch (response.getStatus()) {
            case 200:
            case 302:
                //showMapStuff();
                break;
            default:
                Logger.log("UNHANDLED LOGIN EXCEPTION " + response.getStatus());
        }
    }

    public void onRxError(Throwable throwable) {
        showErrorDialog();
        Logger.log("RXERROR " + throwable.getLocalizedMessage());
        throwable.printStackTrace();
        FragmentLogin login = (FragmentLogin) getFragmentManager().findFragmentByTag(LOGIN);
        if (login != null) {
            login.onRxError();
        }
    }

    @Override
    public void authenticate(String username, String password, String phone) {
        helper.register(username, password, phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Response -> onRegisterResponse(Response, username, password), this::onRxError);
    }

    private void showErrorDialog() {
        if (error == null) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(getResources().getString(R.string.dialog_cant_login_title));
            alertDialog.setMessage(getResources().getString(R.string.dialog_cant_login_body));
            alertDialog.setPositiveButton(getResources().getString(R.string.dialog_cant_login_positive_button),
                    (dialog, which) -> {
                        dialog.dismiss();
                    });
            error = alertDialog.create();
            error.show();
        } else {
            error.show();
        }
    }

    @Override
    public GoogleApiClient getGoogleApiClient() { return null; }

    @Override
    public void changePickup() {

    }

    @Override
    public void setChosenLocation(String fragmentTag, LatLng latlng, CharSequence name) {

    }

    @Override
    public void changeDropoff() {

    }

    @Override
    public void makeHttpPostRequest(int numPassengers) {

    }

    @Override
    public void onChosenLocationChanged(String fragmentTag, LatLng latlng, CharSequence c) {

    }
}
