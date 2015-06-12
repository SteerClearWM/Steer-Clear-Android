package steer.clear;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.VolleyError;
import org.json.JSONObject;

public class ActivityETA extends Activity
        implements View.OnClickListener, HttpHelperInterface {

    private TextView etaTime;
    private Button cancelRide;
    private static int cancelId;
    private static String eta;

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
            eta = String.format("%02d:%02d", pickupHour, pickupMinute);
            etaTime.setText("Your ride will \n arrive at \n" + eta);
        }

        cancelRide.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onClick(View v) {
        showHttpProgress();
        HttpHelper.getInstance(this).cancelRide(cancelId);
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
