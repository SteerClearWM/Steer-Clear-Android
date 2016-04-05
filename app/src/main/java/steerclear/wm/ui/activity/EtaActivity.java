package steerclear.wm.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import steerclear.wm.R;
import steerclear.wm.data.model.RideObject;
import steerclear.wm.data.ActivitySubscriber;
import steerclear.wm.ui.LoadingDialog;
import steerclear.wm.ui.view.ViewFooter;
import steerclear.wm.ui.view.ViewTypefaceTextView;

public class EtaActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.activity_eta_time_prefix) ViewTypefaceTextView prefix;
    @Bind(R.id.activity_eta_time) ViewTypefaceTextView etaTime;
    @Bind(R.id.activity_eta_cancel_ride) ViewFooter cancelRide;

    private LoadingDialog loadingDialog;
    private RideObject rideObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eta);

        loadingDialog = new LoadingDialog(this, R.style.ProgressDialogTheme);

        rideObject = store.getRideObject();

        setEtaTime();

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
    }

    private void setEtaTime() {
        RideObject.RideInfo info = rideObject.getRideInfo();
        String pickupTime = info.getPickupTime();
        try {
            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("est"));

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(dateFormat.parse(pickupTime));

            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            String format = String.format(Locale.getDefault(), "%02d : %02d", hour, minute);
            etaTime.setText(format);
        } catch (ParseException p) {
            p.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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

    private void cancelRide() {
        loadingDialog.show();
        store.clearRideInfo();

        Subscriber<ResponseBody> rideCancelSubscriber = new ActivitySubscriber<ResponseBody>(this) {
            @Override
            public void onCompleted() {
                removeSubscription(this);
                loadingDialog.dismiss();
                startActivity(new Intent(EtaActivity.this, HomeActivity.class));
                finish();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                onCompleted();
            }
        };

        helper.cancelRide(rideObject.ride.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rideCancelSubscriber);
    }
}
