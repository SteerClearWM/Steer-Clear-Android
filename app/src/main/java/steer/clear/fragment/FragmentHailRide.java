package steer.clear.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import steer.clear.MainApp;
import steer.clear.R;
import steer.clear.event.EventChangePlaces;
import steer.clear.event.EventPostPlacesChosen;
import steer.clear.util.LoadingDialog;
import steer.clear.util.Logger;
import steer.clear.view.ViewPassengerSelect;
import steer.clear.view.ViewTypefaceTextView;

public class FragmentHailRide extends Fragment implements OnClickListener {

	@Bind(R.id.fragment_hail_ride_pickup_location) ViewTypefaceTextView pickupLocation;
	@Bind(R.id.fragment_hail_ride_change_pickup) ImageButton changePickup;
	@Bind(R.id.fragment_hail_ride_dropoff_location) ViewTypefaceTextView dropoffLocation;
	@Bind(R.id.fragment_hail_ride_change_dropoff) ImageButton changeDropoff;
    @Bind(R.id.fragment_hail_ride_passenger_select) ViewPassengerSelect test;
	@Bind(R.id.fragment_hail_ride_footer) Button postRide;

	@Inject EventBus bus;

	private final static String PICKUP = "pickupText";
	private final static String DROPOFF = "dropoffText";

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

        pickupLocation.setText(getArguments().getString(PICKUP));
        dropoffLocation.setText(getArguments().getString(DROPOFF));

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
		return animator;
	}

	@Override
	@OnClick({R.id.fragment_hail_ride_change_pickup, R.id.fragment_hail_ride_change_dropoff,
			R.id.fragment_hail_ride_footer})
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.fragment_hail_ride_footer:
				if (test.getPassengers() != 0) {
                    Logger.log("PASSENGERS: " + test.getPassengers());
                    bus.post(new EventPostPlacesChosen(test.getPassengers()));
				} else {
                    Logger.log("0 PASSENGERS");
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

}