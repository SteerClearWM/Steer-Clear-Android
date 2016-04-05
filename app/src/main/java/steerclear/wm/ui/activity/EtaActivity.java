package steerclear.wm.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import icepick.State;
import okhttp3.Response;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import steerclear.wm.R;
import steerclear.wm.data.event.EventLogout;
import steerclear.wm.ui.LoadingDialog;
import steerclear.wm.ui.view.ViewFooter;
import steerclear.wm.ui.view.ViewTypefaceTextView;

public class EtaActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.activity_eta_time_prefix) ViewTypefaceTextView prefix;
    @Bind(R.id.activity_eta_time) ViewTypefaceTextView etaTime;
    @Bind(R.id.activity_eta_cancel_ride) ViewFooter cancelRide;

    @State int cancelId;
    @State String eta;
    @State boolean saveInfo = true;

    public final static String ETA = "eta";
    public final static String CANCEL = "CANCEL_ID";

    private LoadingDialog loadingDialog;

    public static Intent newIntent(Context context, String eta, int cancelId) {
        Intent etaActivity = new Intent(context, EtaActivity.class);
        etaActivity.putExtra(EtaActivity.ETA, eta);
        etaActivity.putExtra(EtaActivity.CANCEL, cancelId);
        etaActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        etaActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return etaActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eta);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            cancelId = savedInstanceState.getInt(CANCEL);
            eta = savedInstanceState.getString(ETA);
            etaTime.setText(eta);
        } else {
            Intent extras = getIntent();
            eta = extras.getStringExtra(ETA);
            cancelId = extras.getIntExtra(CANCEL, 0);
            etaTime.setText(eta);
        }

        loadingDialog = new LoadingDialog(this, R.style.ProgressDialogTheme);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ETA, eta);
        outState.putInt(CANCEL, cancelId);
    }

    @Override
    protected void onPause() {
        if (saveInfo) { store.putRideInfo(eta, cancelId); }
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setTitle(getResources().getString(R.string.dialog_cancel_ride_title))
            .setMessage(getResources().getString(R.string.dialog_cancel_ride_body))
            .setPositiveButton(
                getResources().getString(R.string.dialog_cancel_ride_pos_button_text),
                (dialog, which) -> {
                    cancelRide();
            }).setNegativeButton(
                getResources().getString(R.string.dialog_cancel_ride_neg_button_text),
                (dialog, which) -> {
                    dialog.dismiss();
            });

        alertDialog.show();
    }

    public void onEvent(EventLogout eventLogout) {

    }

    private void cancelRide() {
        loadingDialog.show();
        saveInfo = false;
        store.clearRideInfo();

        Subscriber<ResponseBody> rideCancelSubscriber = new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                removeSubscription(this);
                loadingDialog.dismiss();
                startActivity(HomeActivity.newIntent(EtaActivity.this));
                finish();
            }

            @Override
            public void onError(Throwable e) {
                saveInfo = false;
                onCompleted();
            }

            @Override
            public void onNext(ResponseBody response) {

            }
        };

        addSubscription(helper.cancelRide(cancelId)
                .subscribeOn(Schedulers.io())
//                .onExceptionResumeNext(rx.Observable.<Response>empty())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rideCancelSubscriber));
    }
}
