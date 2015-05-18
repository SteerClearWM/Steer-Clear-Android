package steer.clear;

import android.app.Activity;
import android.os.Bundle;
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
	 * Skeleton method. In the future, will request a ride by pinging the eventual server backend
	 */
	private void hailRide() {
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
	public void onPostSuccess(String string) {
		// TODO Auto-generated method stub
		Log.v("Miles", "On post success, string is " + string);
	}

	@Override
	public void onGetSuccess(String string) {
		// TODO Auto-generated method stub
		Log.v("Miles", "On get success, string is " + string);
	}

	@Override
	public void onVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		Log.v("Miles", "Volley Error is " + error.getMessage());
	}

	@Override
	public void onUserError(String string) {
		// TODO Auto-generated method stub
		Log.v("Miles", "User Error is " + string);
	}
}
