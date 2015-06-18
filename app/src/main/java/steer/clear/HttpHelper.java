package steer.clear;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HttpHelper {
	
	final static String POST_TAG = "POST"; // Tag used for all POST requests when adding to the RequestQueue
	final static String GET_TAG = "GET";  // Tag used for all GET requests when adding to the RequestQueue
	
	// URLS FOR USE WITH GENYMOTION ONLY
	private final static String URL_ADD_RIDE = "http://10.0.3.2:5000/rides";
	private final static String URL_GET_RIDES = "http://10.0.3.2:5000/rides";
	private final static String URL_DELETE_RIDE = "http://10.0.3.2:5000/rides";
	
	private static HttpHelper mInstance; // Singleton construct
	
	private final HttpHelperInterface listener; // You know what this is
	
	/**
	 * Creates a new instance of HttpHelper with the listener attached to the context specified
	 */
	public HttpHelper(HttpHelperInterface listener) {
		mInstance = this;
		this.listener = listener;
	}
	
	/**
	 * Singleton construct.
	 * If the instance is null, creates a new HttpHelper class with a listener attached to the given context.
	 * @return HttpHelper
	 */
	public static synchronized HttpHelper getInstance(Context context) {
		return mInstance = new HttpHelper((HttpHelperInterface) context);
    }
	
	/**
	 * Method used to get a list of all rides currently in the queue.
	 * If successful, calls through the HttpHelperInterface onGetSuccess()
	 * If failure, calls through the HttpHelperInterface onVolleyError()
	 */
	public void getRides() {
		JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.GET, URL_GET_RIDES, 
				null, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject arg0) {
				// TODO Auto-generated method stub
				listener.onGetSuccess(arg0);
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				listener.onVolleyError(arg0);
			}
		});

        ApplicationInitialize.getInstance().addToRequestQueue(myReq, GET_TAG);
	}
	
	/**
	 * Method used to add a ride to the queue. Parameters must be formatted exactly as shown
	 * If successful, calls through the HttpHelperInterface onPostSuccess()
	 * If failure, calls through the HttpHelperInterface onVolleyError()
	 */
	public void addRide(final Integer num_passengers, final Double start_latitude, final Double start_longitude,
			final Double end_latitude, final Double end_longitude) {
		
		StringRequest myReq = new StringRequest(Request.Method.POST, URL_ADD_RIDE, new Response.Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				// TODO Auto-generated method stub
				try {
					JSONObject json = new JSONObject(arg0);
					listener.onPostSuccess(json);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        	
        }, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				listener.onVolleyError(arg0);
			}
			
        }) {
			
			@Override
			protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
    	        params.put("num_passengers", Integer.toString(num_passengers));
    	        params.put("start_latitude", Double.toString(start_latitude));
    	        params.put("start_longitude", Double.toString(start_longitude));
    	        params.put("end_latitude", Double.toString(end_latitude));
    	        params.put("end_longitude", Double.toString(end_longitude));
                return params;
            }
			
		};

		ApplicationInitialize.getInstance().addToRequestQueue(myReq, POST_TAG);
	}

	public void cancelRide(int cancelId) {
		String deleteUrl = URL_DELETE_RIDE + "/" + cancelId;
		StringRequest myReq = new StringRequest(Request.Method.DELETE, deleteUrl,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String s) {
						listener.onDeleteSuccess(s);
					}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError volleyError) {
				listener.onVolleyError(volleyError);
			}

		});

		ApplicationInitialize.getInstance().addToRequestQueue(myReq, POST_TAG);
	}
}
