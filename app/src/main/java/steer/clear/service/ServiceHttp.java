package steer.clear.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServiceHttp extends Service {

    public static final MediaType JSON =
            MediaType.parse("application/x-www-form-urlencoded");
	
	// URLS FOR USE WITH GENYMOTION ONLY
	private final static String URL_ADD_RIDE = "http://10.0.3.2:5000/rides";
	private final static String URL_DELETE_RIDE = "http://10.0.3.2:5000/rides";
	
	private ServiceHttpInterface listener;
	private OkHttpClient mClient;

	private final IBinder mBinder = new LocalBinder();

	public ServiceHttp() {
		mClient = new OkHttpClient();
	}

    public class LocalBinder extends Binder {
        ServiceHttp getService() {
            return ServiceHttp.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

	public void registerListener(ServiceHttpInterface listener) {
		this.listener = listener;
	}
	
	/**
	 * Method used to add a ride to the queue. Parameters must be formatted exactly as shown
	 * If successful, calls through the ServiceHttpInterface onPostSuccess()
	 * If failure, calls through the ServiceHttpInterface onVolleyError()
	 */
	public void addRide(final Integer num_passengers, final Double start_latitude, final Double start_longitude,
			final Double end_latitude, final Double end_longitude) {

		final Request postRequest = new Request.Builder()
				.post(formatAddRideBody(num_passengers, start_latitude, start_longitude,
                        end_latitude, end_longitude))
				.url(URL_ADD_RIDE)
				.build();

		mClient.newCall(postRequest).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                listener.onFailure(request, e);
            }

            @Override
			public void onResponse(Response response) throws IOException {
                listener.onPostSuccess(response);
			}

		});
	}

    private RequestBody formatAddRideBody(final Integer num_passengers, final Double start_latitude, final Double start_longitude,
                                          final Double end_latitude, final Double end_longitude) {
        final RequestBody params = new FormEncodingBuilder()
                .addEncoded("contentType", "application/x-www-form-urlencoded")
                .add("num_passengers", Integer.toString(num_passengers))
                .add("start_latitude", Double.toString(start_latitude))
                .add("start_longitude", Double.toString(start_longitude))
                .add("end_latitude", Double.toString(end_latitude))
                .add("end_longitude", Double.toString(end_longitude))
                .build();
        return params;
    }

	public void cancelRide(int cancelId) {
        String deleteUrl = URL_DELETE_RIDE + "/" + cancelId;
        final Request deleteRequest = new Request.Builder()
                .url(deleteUrl)
                .build();

        mClient.newCall(deleteRequest).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                listener.onFailure(request, e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                listener.onDeleteSuccess(response);
            }

        });
	}
}
