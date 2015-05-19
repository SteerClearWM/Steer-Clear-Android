package steer.clear;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.volley.VolleyError;

public class ActivityHome extends Activity implements OnClickListener, HttpHelperInterface {

	private Button hailRideButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		hailRideButton = (Button) findViewById(R.id.activity_home_hail_ride_button);
		
		hailRideButton.setOnClickListener(this);
		hailRideButton.performClick();
	}
	
	/**
	 * Method called when user clicks "Hail Ride."
	 * Uses HttpHelper to submit a POST request to the server.
	 * Calls through to HttpHelperInterface.onPostSuccess() if success, onVolleyError() if otherwise
	 */
	private void hailRide() {
		HttpHelper.getInstance(this).addRide("444-444-4444", 2, 40.5, 40.6, 40.5, 40.5);
		//HttpHelper.getInstance(this).getRides();
	}
	
	/**
	 * Helper method to return a user's phone number properly formatted for a POST request.
	 * Deletes the international code number at the start of the string, and inserts dashes.
	 * @return formatted phone number
	 */
	private String getPhoneNumber() {
		TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String mPhoneNumber = tMgr.getLine1Number();
		StringBuilder str = new StringBuilder(mPhoneNumber)
			.deleteCharAt(0)
			.insert(3, "-")
			.insert(7, "-");
		return str.toString();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.activity_home_hail_ride_button:
				hailRide();
				break;
		}
	}

	@Override
	public void onPostSuccess(JSONObject object) {
		// TODO Auto-generated method stub
		Log.v("Miles", "On post success, response is " + object);
	}

	@Override
	public void onGetSuccess(JSONObject array) {
		// TODO Auto-generated method stub
		Log.v("Miles", "On get success, response is " + array);
	}

	@Override
	public void onVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		if (error.equals(null)) {
			Log.v("Miles", "error is null, is server on?"); 
		} else {
			Log.v("Miles", "Volley Error is " + error.getMessage());
			int errorCode = error.networkResponse.statusCode;
			switch (errorCode) {
				case 404: // Invalid url
					Log.v("Miles", "404 Error, invalid url or badly encoded POST");
					break;
					
				case 405: // Method not allowed, bad url?
					Log.v("Miles" ,"405 Error, method not allowed");
					break;
					
				default:
					Log.v("Miles", "Error not handled " + errorCode);
			}
		}
	}

	@Override
	public void onUserError(String string) {
		// TODO Auto-generated method stub
		Log.v("Miles", "User Error is " + string);
	}
}
