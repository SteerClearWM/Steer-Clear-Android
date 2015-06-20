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
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import steer.clear.service.HttpHelper;
import steer.clear.service.HttpHelperInterface;
import steer.clear.Logger;
import steer.clear.R;

public class ActivityEta extends AppCompatActivity
        implements View.OnClickListener, HttpHelperInterface {

    private TextView etaTime;
    private Button cancelRide;
    private static int cancelId;
    private static String eta;

    private final static String ETA = "eta";

    private ProgressDialog httpProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eta);

        httpProgress = new ProgressDialog(this, ProgressDialog.STYLE_HORIZONTAL);
        httpProgress.setMessage("Canceling request...");

        etaTime = (TextView) findViewById(R.id.activity_eta_time);
        cancelRide = (Button) findViewById(R.id.activity_eta_cancel_ride);

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

        cancelRide.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ETA, eta);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        etaTime.setText(savedInstanceState.getString(ETA));
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Cancel Ride");
        alertDialog.setMessage("Would you like to cancel your requested ride? This will " +
                "destroy all data entered and close the app.");
        alertDialog.setPositiveButton("Sure", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                showHttpProgress();
                HttpHelper.getInstance(ActivityEta.this).cancelRide(cancelId);
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
    public void onDeleteSuccess(String string) {
        dismissHttpProgress();
        finish();
    }

    @Override
    public void onPostSuccess(JSONObject object) {}

    @Override
    public void onGetSuccess(JSONObject object) {}

    @Override
    public void onVolleyError(VolleyError error) {
        dismissHttpProgress();
        if (error.networkResponse != null) {
            Logger.log("Error Response code: " + error.networkResponse.statusCode);
        } else {
            Toast.makeText(this, "Unknown network error", Toast.LENGTH_SHORT).show();
            Logger.log("VOLLY ERROR NULL");
        }
    }

    private void showHttpProgress() {
        if (httpProgress != null && !httpProgress.isShowing()) {
            httpProgress.show();
        }
    }

    private void dismissHttpProgress() {
        if (httpProgress != null && httpProgress.isShowing()) {
            httpProgress.dismiss();
        }
    }
}
