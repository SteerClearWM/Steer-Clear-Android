package steer.clear;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentHailRide extends Fragment implements OnClickListener, OnTouchListener {
	
	// Global views
	private TextView pickup;
	private TextView dropoff;
	private ImageButton postRide;
	private TextView numPassengers;
	
	// static int used for, you guessed it, storing the current passenger count
	private static int passengers = 0;
	
	// Final static strings used as keys for getArguments()
	private final static String PICKUP = "pickup";
	private final static String DROPOFF = "dropoff";
	
	private ListenerForFragments listener;
	
	public FragmentHailRide(){}
	
	/**
	 * Instantiates newInstance of this fragment with two variables: the name of the pickupLocation and the dropoffLocation
	 * @param pickupLocationName
	 * @param dropoffLocationName
	 * @return
	 */
	public static FragmentHailRide newInstance(CharSequence pickupLocationName, CharSequence dropoffLocationName) {
		FragmentHailRide frag = new FragmentHailRide();
		Bundle args = new Bundle();
		args.putCharSequence(PICKUP, pickupLocationName);
		args.putCharSequence(DROPOFF, dropoffLocationName);
		frag.setArguments(args);
		return frag;
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (ListenerForFragments) activity;
        } catch (ClassCastException e) {
        	e.printStackTrace();
        }
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_hail_ride, container, false);
		
		Bundle args = getArguments();
		
		pickup = (TextView) rootView.findViewById(R.id.fragment_hail_ride_pickup_location);
		pickup.setText("PICKUP LOCATION: \n" + args.getCharSequence(PICKUP));
		pickup.setOnTouchListener(this);
		
		dropoff = (TextView) rootView.findViewById(R.id.fragment_hail_ride_dropoff_location);
		dropoff.setText("DROPOFF LOCATION: \n" + args.getCharSequence(DROPOFF));
		dropoff.setOnTouchListener(this);
		
		numPassengers = (TextView) rootView.findViewById(R.id.fragment_hail_ride_passenger_select);
		numPassengers.setOnTouchListener(this);
		
		postRide = (ImageButton) rootView.findViewById(R.id.fragment_hail_ride_post);
		postRide.setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		if (passengers != 0) {
			listener.makeHttpPostRequest(passengers);
		} else {
			Toast.makeText(getActivity(), "Choose number of passengers", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		final TextView view = (TextView) v;
		v.performClick();
		final int DRAWABLE_LEFT = 0;
        //final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        //final int DRAWABLE_BOTTOM = 3;
        
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
        	switch (v.getId()) {
        		case R.id.fragment_hail_ride_passenger_select:
        			incrementNumPassengers(event, view, DRAWABLE_LEFT, DRAWABLE_RIGHT);
        			break;
        			
        		// To come later...
        		case R.id.fragment_hail_ride_pickup_location:
        			Logger.log("CHANGE PICKUP");
        			break;
        			
        		case R.id.fragment_hail_ride_dropoff_location:
        			Logger.log("CHANGE DROPOFF");
        			break;
        	}
         }
         return true;
	}

	/**
	 * Method name is pretty obvious. Changes the value of passengers when the user clicks on the up/ down arrow accordingly
	 * @param event
	 * @param view
	 * @param DRAWABLE_LEFT
	 * @param DRAWABLE_RIGHT
	 * @return
	 */
	private boolean incrementNumPassengers(MotionEvent event, final TextView view,
			final int DRAWABLE_LEFT, final int DRAWABLE_RIGHT) {
		if (event.getRawX() >= (view.getRight() - view.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
			if (passengers < 8) {
				passengers = passengers + 1;
				view.setText(String.valueOf(passengers));
			}
			return true;
		} 
		 
		if (event.getRawX() <= (view.getLeft() + view.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
			if (passengers > 1) {
				passengers = passengers - 1;
				view.setText(String.valueOf(passengers));
			}
			return true;
		}
		return false;
	}
}
