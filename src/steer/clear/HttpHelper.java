package steer.clear;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class HttpHelper {
	
	final static String POST_TAG = "POST";
	final static String GET_TAG = "GET";
	
	private static HttpHelper mInstance;
	
	private final HttpHelperInterface listener;
	
	public HttpHelper(HttpHelperInterface listener) {
		mInstance = this;
		this.listener = listener;
	}
	
	public static synchronized HttpHelper getInstance(Context context) {
		if (mInstance == null) {
            mInstance = new HttpHelper((HttpHelperInterface) context);
        }
 
        return mInstance;
    }
	
	public void getRides() {
        StringRequest myReq = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/rides", new Response.Listener<String>() {

			@Override
			public void onResponse(String arg0) {
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
	
	public void getPulse() {
		StringRequest myReq = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/", new Response.Listener<String>() {

			@Override
			public void onResponse(String arg0) {
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
	
	public void addRide(final String phone_number, final Integer num_passengers, 
			final Double start_latitude, final Double start_longitude, final Double end_latitude, final Double end_longitude) {
		String url = "http://10.0.2.2:5000/rides";
		
        StringRequest myReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				// TODO Auto-generated method stub
				listener.onPostSuccess(arg0);
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

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(myReq, POST_TAG);
	}
}
