package steer.clear.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import steer.clear.Logger;
import steer.clear.R;
import steer.clear.dagger.ContextModule;
import steer.clear.dagger.DaggerContextComponent;
import steer.clear.service.ServiceHttp;
import steer.clear.service.ServiceHttpInterface;

public class ActivityEta extends AppCompatActivity
        implements View.OnClickListener, ServiceHttpInterface {

    @InjectView(R.id.activity_eta_time) public TextView etaTime;
    @InjectView(R.id.activity_eta_cancel_ride) public Button cancelRide;
    private static int cancelId;
    private static String eta;

    private final static String ETA = "eta";
    private final static String CANCEL_ID = "cancel";

    @Inject public ServiceHttp helper;
    @Inject public ProgressDialog httpProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eta);
        ButterKnife.inject(this);

        DaggerContextComponent.builder()
                .contextModule(new ContextModule(this))
                .build()
                .inject(this);

        helper.registerListener(this);

        if (getIntent() != null) {
            Intent extras = getIntent();
            int pickupHour = extras.getIntExtra("PICKUP_HOUR", 0);
            int pickupMinute = extras.getIntExtra("PICKUP_MINUTE", 0);
            cancelId = extras.getIntExtra("CANCEL_ID", 0);
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
        outState.putInt(CANCEL_ID, cancelId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        eta = savedInstanceState.getString(ETA);
        cancelId = savedInstanceState.getInt(CANCEL_ID);

        etaTime.setText(eta);
    }

    @Override
    @OnClick(R.id.activity_eta_cancel_ride)
    public void onClick(View v) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Cancel Ride");
        alertDialog.setMessage("Would you like to cancel your requested ride? This will " +
                "destroy all data entered and close the app.");
        alertDialog.setPositiveButton("Sure", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                showHttpProgress();
                helper.cancelRide(cancelId);
            }

        });

        alertDialog.setNegativeButton("Nah", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });

        alertDialog.show();
    }

    @Override
    public void onDeleteSuccess(Response response) {
        dismissHttpProgress();
        finish();
    }

    @Override
    public void onPostSuccess(Response response) {}

    @Override
    public void onFailure(Request request, IOException exception) {
        dismissHttpProgress();
        Logger.log("ON FAILURE DELETE " + request.body());
    }

    private void showHttpProgress() {
        httpProgress.show();
    }

    private void dismissHttpProgress() {
        httpProgress.dismiss();
    }
}
