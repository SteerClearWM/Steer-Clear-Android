package steer.clear.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import steer.clear.MainApp;
import steer.clear.R;
import steer.clear.event.EventPostPlacesChosen;
import steer.clear.util.ViewUtils;
import steer.clear.view.ViewFooter;
import steer.clear.view.ViewPassengerSelect;
import steer.clear.view.ViewTypefaceTextView;

public class FragmentHailRide extends Fragment implements OnClickListener {

	@Bind(R.id.fragment_hail_ride_pickup_location) ViewTypefaceTextView pickupLocation;
	@Bind(R.id.fragment_hail_ride_change_pickup) AppCompatImageButton changePickup;
	@Bind(R.id.fragment_hail_ride_dropoff_location) ViewTypefaceTextView dropoffLocation;
	@Bind(R.id.fragment_hail_ride_change_dropoff) AppCompatImageButton changeDropoff;
    @Bind(R.id.fragment_hail_ride_passenger_select) ViewPassengerSelect passengerSelect;
	@Bind(R.id.fragment_hail_ride_footer) ViewFooter postRide;

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
	public void onAttach(Context context) {
		super.onAttach(context);
		((MainApp) context.getApplicationContext()).getApplicationComponent().inject(this);
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
			animator = ObjectAnimator.ofFloat(getActivity(), ViewUtils.ALPHA, 0f, 11f);
		} else {
			animator = ObjectAnimator.ofFloat(getActivity(), ViewUtils.ALPHA, 1f, 0f);
		}

		animator.setDuration(750);
		return animator;
	}

	@Override
	@OnClick({R.id.fragment_hail_ride_change_pickup,
			R.id.fragment_hail_ride_change_dropoff,
			R.id.fragment_hail_ride_footer})
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.fragment_hail_ride_footer:
				if (passengerSelect.getPassengers() != 0) {
                    bus.post(new EventPostPlacesChosen(passengerSelect.getPassengers()));
				} else {
                    Snackbar.make(getView(),
                            getResources().getString(R.string.fragment_hail_ride_no_passengers),
                            Snackbar.LENGTH_SHORT).show();
				}
				break;

			case R.id.fragment_hail_ride_change_pickup:
                getActivity().onBackPressed();
				break;

			case R.id.fragment_hail_ride_change_dropoff:
				getActivity().onBackPressed();
				break;
		}
	}

}