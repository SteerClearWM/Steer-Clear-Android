package steer.clear.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import retrofit.client.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import steer.clear.MainApp;
import steer.clear.R;
import steer.clear.retrofit.Client;
import steer.clear.util.Datastore;
import steer.clear.util.Utils;
import steer.clear.view.ViewFooter;
import steer.clear.view.ViewTypefaceTextView;

public class ActivityEta extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.activity_eta_time_prefix) ViewTypefaceTextView prefix;
    @Bind(R.id.activity_eta_time) ViewTypefaceTextView etaTime;
    @Bind(R.id.activity_eta_cancel_ride) ViewFooter cancelRide;

    private static int cancelId;
    private static String eta;
    private boolean saveInfo = true;

    public final static String ETA = "eta";
    public final static String CANCEL = "CANCEL_ID";

    @Inject Client helper;
    @Inject Datastore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eta);
        ButterKnife.bind(this);

        ((MainApp) getApplicationContext()).getApplicationComponent().inject(this);

        if (savedInstanceState != null) {
            etaTime.setText(savedInstanceState.getString(ETA));
        } else {
            if (getIntent() != null) {
                Intent extras = getIntent();
                eta = extras.getStringExtra(ETA);
                cancelId = extras.getIntExtra(CANCEL, 0);
                etaTime.setText(eta);
            } else {
                eta = "11:49";
                cancelId = -1;
                etaTime.setText(eta);
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ETA, eta);
        outState.putInt(CANCEL, cancelId);
    }

    @Override
    protected void onPause() {
        if (saveInfo) {
            store.putRideInfo(eta, cancelId);
        }
        super.onPause();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        eta = savedInstanceState.getString(ETA);
        cancelId = savedInstanceState.getInt(CANCEL);
        etaTime.setText(eta);
    }

    @Override
    @OnClick(R.id.activity_eta_cancel_ride)
    public void onClick(View v) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
            .setTitle(getResources().getString(R.string.dialog_cancel_ride_title))
            .setMessage(getResources().getString(R.string.dialog_cancel_ride_body))
            .setPositiveButton(
                getResources().getString(R.string.dialog_cancel_ride_pos_button_text),
                (dialog, which) -> {
                    saveInfo = false;
                    store.clearRideInfo();
                    finish();
            }).setNegativeButton(
                getResources().getString(R.string.dialog_cancel_ride_neg_button_text),
                (dialog, which) -> {
                    dialog.dismiss();
            });

        alertDialog.show();
    }

    public void onResponseReceived(Response response) {
       // finish();
    }

    public void onErrorReceived(Throwable throwable) {
        store.clearRideInfo();
        throwable.printStackTrace();
    }
}
