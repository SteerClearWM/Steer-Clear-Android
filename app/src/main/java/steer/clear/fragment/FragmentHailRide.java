package steer.clear.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTouch;
import de.greenrobot.event.EventBus;
import steer.clear.MainApp;
import steer.clear.R;
import steer.clear.event.EventChangePlaces;
import steer.clear.event.EventPostPlacesChosen;
import steer.clear.util.Utils;

public class FragmentHailRide extends Fragment implements OnClickListener, OnTouchListener {

	@Bind(R.id.fragment_hail_ride_pickup_location) TextView pickup;
	@Bind(R.id.fragment_hail_ride_change_pickup) ImageButton changePickup;
	@Bind(R.id.fragment_hail_ride_dropoff_location) TextView dropoff;
	@Bind(R.id.fragment_hail_ride_change_dropoff) ImageButton changeDropoff;
	@Bind(R.id.fragment_hail_ride_post) Button postRide;
	@Bind(R.id.view_passenger_switcher) TextView numPassengers;

	@Inject EventBus bus;

	private static int passengers = 0;

	private final static String PICKUP = "pickup";
	private final static String DROPOFF = "dropoff";

	public FragmentHailRide(){}

	public static FragmentHailRide newInstance(CharSequence pickupLocationName,
											   CharSequence dropoffLocationName) {
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
		((MainApp) activity.getApplication()).getApplicationComponent().inject(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_hail_ride, container, false);
		ButterKnife.bind(this, rootView);
		numPassengers.setTypeface(Utils.getStaticTypeFace(getActivity(), "Antipasto.otf"));

		Bundle args = getArguments();

		pickup.setText("PICKUP LOCATION: \n" + args.getCharSequence(PICKUP));
		dropoff.setText("DROPOFF LOCATION: \n" + args.getCharSequence(DROPOFF));

		return rootView;
	}

	@Override
	public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
		Animator animator;
		if (enter) {
			animator = ObjectAnimator.ofFloat(getActivity(), "alpha", 0, 1);
		} else {
			animator = ObjectAnimator.ofFloat(getActivity(), "alpha", 1, 0);
		}

		animator.setDuration(750);
		animator.setInterpolator(new AccelerateDecelerateInterpolator());
		return animator;
	}

	@Override
	@OnClick({R.id.fragment_hail_ride_change_pickup, R.id.fragment_hail_ride_change_dropoff,
		R.id.fragment_hail_ride_post})
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.fragment_hail_ride_post:
				if (passengers != 0) {
					bus.post(new EventPostPlacesChosen());
				} else {
                    Snackbar.make(getView(),
                            getResources().getString(R.string.fragment_hail_ride_no_passengers),
                            Snackbar.LENGTH_SHORT).show();
				}
				break;

			case R.id.fragment_hail_ride_change_pickup:
                bus.post(new EventChangePlaces());
				break;

			case R.id.fragment_hail_ride_change_dropoff:
                bus.post(new EventChangePlaces());
				break;
		}
	}

	@Override
	@OnTouch(R.id.view_passenger_switcher)
	public boolean onTouch(View v, MotionEvent event) {
		final TextView view = (TextView) v;
		v.performClick();
		final int DRAWABLE_LEFT = 0;
        //final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        //final int DRAWABLE_BOTTOM = 3;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
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
		}
		return true;
	}
}