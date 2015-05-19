package steer.clear;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

public class HttpHelper {
	
	final static String POST_TAG = "POST"; // Tag used for all POST requests when adding to the RequestQueue
	final static String GET_TAG = "GET";  // Tag used for all GET requests when adding to the RequestQueue
	
	private static HttpHelper mInstance; // Singleton construct
	
	private final HttpHelperInterface listener; // You know what this is
	/**
	 * Creates a new instance of HttpHelper with the listener attached to the context specified
	 * @param listener
	 */
	public HttpHelper(HttpHelperInterface listener) {
		mInstance = this;
		this.listener = listener;
	}
	
	/**
	 * Singleton construct.
	 * If the instance is null, creates a new HttpHelper class with a listener attached to the given context.
	 * @param context
	 * @return HttpHelper
	 */
	public static synchronized HttpHelper getInstance(Context context) {
		if (mInstance == null) {
            mInstance = new HttpHelper((HttpHelperInterface) context);
        }
 
        return mInstance;
    }
	
	/**
	 * Method used to get a list of all rides currently in the queue.
	 * If successful, calls through the HttpHelperInterface onGetSuccess()
	 * If failure, calls through the HttpHelperInterface onVolleyError()
	 */
	public void getRides() {
		JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.GET, UtilityUrls.URL_GET_RIDES, 
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

        AppController.getInstance().addToRequestQueue(myReq, GET_TAG);
	}
	
	/**
	 * Method used to add a ride to the queue. Parameters must be formatted exactly as shown
	 * If successful, calls through the HttpHelperInterface onPostSuccess()
	 * If failure, calls through the HttpHelperInterface onVolleyError()
	 * @param String phone_number, in the format "xxx-xxx-xxxx"
	 * @param Integer num_passengers
	 * @param Double start_latitude
	 * @param Double start_longitude
	 * @param Double end_latitude
	 * @param Double end_longitude
	 */
	public void addRide(final String phone_number, final Integer num_passengers, 
			final Double start_latitude, final Double start_longitude, final Double end_latitude, final Double end_longitude) {
		if (num_passengers <= 0) {listener.onUserError("Negative passenger limit"); return;}
		
		StringRequest myReq = new StringRequest(Request.Method.POST, "http://10.0.2.2:5000/rides", new Response.Listener<String>() {

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
                params.put("phone_number", phone_number);
    	        params.put("num_passengers", Integer.toString(num_passengers));
    	        params.put("start_latitude", Double.toString(start_latitude));
    	        params.put("start_longitude", Double.toString(start_longitude));
    	        params.put("end_latitude", Double.toString(end_latitude));
    	        params.put("end_longitude", Double.toString(end_longitude));
                return params;
            }
			
		};

		AppController.getInstance().addToRequestQueue(myReq, POST_TAG);
	}
}
