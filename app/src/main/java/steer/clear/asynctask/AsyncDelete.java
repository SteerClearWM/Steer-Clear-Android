package steer.clear.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import retrofit.RetrofitError;
import retrofit.client.Response;
import steer.clear.MainApp;
import steer.clear.activity.ActivityEta;
import steer.clear.service.RetrofitClient;

/**
 * Created by milespeele on 7/2/15.
 */
public class AsyncDelete extends AsyncTask<Void, Void, Response> {

    private WeakReference<ActivityEta> weakCxt;
    private int cancelId;

    @Inject
    RetrofitClient client;

    public AsyncDelete(ActivityEta activityEta, int cancelId) {
        ((MainApp) activityEta.getApplicationContext()).getApplicationComponent().inject(this);
        weakCxt = new WeakReference<>(activityEta);
        this.cancelId = cancelId;
    }

    @Override
    protected Response doInBackground(Void... params) {
        try {
            return client.cancelRide(cancelId);
        } catch (RetrofitError error) {
            return null;
        }
    }

    @Override
    public void onPostExecute(Response response) {
        super.onPostExecute(response);
        ActivityEta activity = weakCxt.get();
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
