package steer.clear.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import retrofit.RetrofitError;
import steer.clear.MainApp;
import steer.clear.activity.ActivityHome;
import steer.clear.pojo.RideResponse;
import steer.clear.service.RetrofitClient;

/**
 * Created by milespeele on 7/2/15.
 */
public class AsyncAddRide extends AsyncTask<Void, Void, RideResponse> {

    private WeakReference<ActivityHome> weakCxt;
    private int numPassengers;
    private double pickupLat;
    private double pickupLong;
    private double dropoffLat;
    private double dropoffLong;

    @Inject
    RetrofitClient client;

    public AsyncAddRide(ActivityHome activity, int numPassengers, double pickupLat, double pickupupLong,
                        double dropoffLat, double dropoffLong) {
        ((MainApp) activity.getApplicationContext()).getApplicationComponent().inject(this);
        weakCxt = new WeakReference<>(activity);
        this.numPassengers = numPassengers;
        this.pickupLat = pickupLat;
        this.pickupLong = pickupupLong;
        this.dropoffLat = dropoffLat;
        this.dropoffLong = dropoffLong;
    }

    @Override
    protected RideResponse doInBackground(Void... params) {
        try {
            return client.addRide(numPassengers, pickupLat, pickupLong, dropoffLat, dropoffLong);
        } catch (RetrofitError error) {
            return null;
        }
    }

    @Override
    public void onPostExecute(RideResponse response) {
        super.onPostExecute(response);
        ActivityHome activity = weakCxt.get();
        if (activity != null) {
            if (response == null) {
                Log.d("Miles", "RIDE RESPONSE NULL");
                //activity.onAsyncError(new AsyncError(this, "LOGIN"));
            } else {
                Log.d("Miles", "RIDE RESPONSE NOT NULL");
                //activity.onLoginResponse(parseHeaders(result.getHeaders()));
            }
        }
    }
}
