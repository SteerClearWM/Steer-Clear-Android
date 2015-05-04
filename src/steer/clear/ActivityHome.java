package steer.clear;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActivityHome extends Activity implements OnClickListener {

	private Button hailRideButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		hailRideButton = (Button) findViewById(R.id.activity_home_hail_ride_button);
		hailRideButton.setOnClickListener(this);
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
}
