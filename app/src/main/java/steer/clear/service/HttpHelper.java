package steer.clear.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookiePolicy;
import java.util.HashMap;
import java.util.Map;

import steer.clear.ApplicationInitialize;

public class HttpHelper extends Service {
	
	final static String POST_TAG = "POST"; // Tag used for all POST requests when adding to the RequestQueue
	final static String GET_TAG = "GET";  // Tag used for all GET requests when adding to the RequestQueue
	
	// URLS FOR USE WITH GENYMOTION ONLY
	private final static String URL_ADD_RIDE = "http://10.0.3.2:5000/rides";
	private final static String URL_GET_RIDES = "http://10.0.3.2:5000/rides";
	private final static String URL_DELETE_RIDE = "http://10.0.3.2:5000/rides";
	
	private HttpHelperInterface listener; // You know what this is
	private OkHttpClient mClient;
	private final IBinder mBinder = new LocalBinder();

	public HttpHelper() {
		mClient = new OkHttpClient();
	}

	public void registerListener(HttpHelperInterface listener) {
		this.listener = listener;
	}

	public class LocalBinder extends Binder {
		HttpHelper getService() {
			return HttpHelper.this;
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
	
	/**
	 * Method used to add a ride to the queue. Parameters must be formatted exactly as shown
	 * If successful, calls through the HttpHelperInterface onPostSuccess()
	 * If failure, calls through the HttpHelperInterface onVolleyError()
	 */
	public void addRide(final Integer num_passengers, final Double start_latitude, final Double start_longitude,
			final Double end_latitude, final Double end_longitude) {
	}

	public void cancelRide(int cancelId) {
	}
}
