package steer.clear.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import steer.clear.MainApp;
import steer.clear.R;
import steer.clear.service.RetrofitClient;

public class ActivityEta extends AppCompatActivity
        implements View.OnClickListener {

    @InjectView(R.id.activity_eta_time) public TextView etaTime;
    @InjectView(R.id.activity_eta_cancel_ride) public Button cancelRide;
    private static int cancelId;
    private static String eta;

    private final static String ETA = "eta";
    private final static String CANCEL = "CANCEL_ID";
    private final static String HOUR = "PICKUP_HOUR";
    private final static String MINUTE = "PICKUP_MINUTE";

    @Inject public RetrofitClient helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eta);
        ButterKnife.inject(this);

        ((MainApp) getApplicationContext()).getApplicationComponent().inject(this);

        if (getIntent() != null) {
            Intent extras = getIntent();
            int pickupHour = extras.getIntExtra(HOUR, 0);
            int pickupMinute = extras.getIntExtra(MINUTE, 0);
            cancelId = extras.getIntExtra(CANCEL, 0);
            if (savedInstanceState != null) {
                etaTime.setText("Your ride will \n arrive at \n" + savedInstanceState.getString(ETA));
            } else {
                eta = String.format("%02d:%02d", pickupHour, pickupMinute);
                etaTime.setText("Your ride will \n arrive at \n" + eta);
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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        eta = savedInstanceState.getString(ETA);
        cancelId = savedInstanceState.getInt(CANCEL);
        etaTime.setText(eta);
    }

    @Override
    @OnClick(R.id.activity_eta_cancel_ride)
    public void onClick(View v) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.dialog_cancel_ride_title));
        alertDialog.setMessage(getResources().getString(R.string.dialog_cancel_ride_body));
        alertDialog.setPositiveButton(
                getResources().getString(R.string.dialog_cancel_ride_pos_button_text),
                new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                helper.cancelRide(cancelId);
            }

        });

        alertDialog.setNegativeButton(
                getResources().getString(R.string.dialog_cancel_ride_neg_button_text),
                new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });

        alertDialog.show();
    }
}
