package steer.clear;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class ActivityHome extends Activity implements OnClickListener {

	private Button hailRideButton;
	private EditText addressFieldEditText;
	private Location barretHallLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		hailRideButton = (Button) findViewById(R.id.activity_home_hail_ride_button);
		addressFieldEditText = (EditText) findViewById(R.id.activity_home_address_field);
		
		// For now, uses hardcoded LatLng
		// LatLng set to Barrett Hall, coords from GoogleMaps
		barretHallLocation = new Location("");
		barretHallLocation.setLatitude(37.269613);
		barretHallLocation.setLongitude(-76.711256);
		
		hailRideButton.setOnClickListener(this);
		hailRideButton.performClick();
	}
	
	/**
	 * Skeleton method. In the future, will request a ride by pinging the eventual server backend
	 */
	private void hailRide() {
		// Do something with barrettHallLocation
	}
	
	private void addRide() {
		String url = "http://10.0.2.2:5000/rides";
        
        RequestQueue queue = AppController.getInstance().getRequestQueue();
        StringRequest myReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				// TODO Auto-generated method stub
				Log.v("Miles", "RESPONSE IS " + arg0);
			}
        	
        }, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				Log.v("Miles", "ERROR IS " + arg0.getMessage());
			}
			
        }) {
			
			@Override
			protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("phone_number", "aaa-aaa-aaaa");
    	        params.put("num_passengers", Integer.toString(4));
    	        params.put("start_latitude", Float.toString((float) 40.5));
    	        params.put("start_longitude", Float.toString((float) 100.7));
    	        params.put("end_latitude", Float.toString((float) 40.5));
    	        params.put("end_longitude", Float.toString((float) 100.7));
                return params;
            }
			
		};

		// Adding request to request queue
		queue.add(myReq);
	}
	
	private void getRides() {
		RequestQueue queue = AppController.getInstance().getRequestQueue();
		// FOR THIS URL...
		// If you're using genymotion, use 10.0.3.2
		// If you're using AVD, 10.0.2.2
		// DON'T RUN THRU VIRTUALBOX FOR THE LOVE OF GOD
        StringRequest myReq = new StringRequest(Request.Method.GET, "http://10.0.2.2:5000/rides", new Response.Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				// TODO Auto-generated method stub
				Log.v("Miles", "response is " + arg0);
			}
			
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				Log.v("Miles", "error is " + arg0.getMessage());
			}
		});

        queue.add(myReq);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.activity_home_hail_ride_button:
				hailRide();
				break;
		}
	}
}
