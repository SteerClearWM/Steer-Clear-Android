package steerclear.wm.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import java.util.Collections;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import icepick.State;
import steerclear.wm.R;
import steerclear.wm.util.TimeLock;
import steerclear.wm.ui.view.ViewFooter;
import steerclear.wm.ui.view.ViewPassengerSelect;
import steerclear.wm.ui.view.ViewTypefaceTextView;

public class HailRideFragment extends BaseFragment implements OnClickListener {

	@Bind(R.id.fragment_hail_ride_pickup_location) ViewTypefaceTextView pickupLocation;
	@Bind(R.id.fragment_hail_ride_change_pickup) ImageButton changePickup;
	@Bind(R.id.fragment_hail_ride_dropoff_location) ViewTypefaceTextView dropoffLocation;
	@Bind(R.id.fragment_hail_ride_change_dropoff) ImageButton changeDropoff;
    @Bind(R.id.fragment_hail_ride_passenger_select) ViewPassengerSelect passengerSelect;
	@Bind(R.id.fragment_hail_ride_footer) ViewFooter postRide;

	private final static String PICKUP = "editPickup";
	private final static String DROPOFF = "editDropoff";

    @State CharSequence pickup, dropoff;

    private IRideRequestFlow iRideRequestFlow;

	public HailRideFragment() {}

	public static HailRideFragment newInstance(CharSequence pickupLocationName,
											   CharSequence dropoffLocationName) {
		HailRideFragment frag = new HailRideFragment();
		Bundle args = new Bundle();
		args.putCharSequence(PICKUP, pickupLocationName);
		args.putCharSequence(DROPOFF, dropoffLocationName);
		frag.setArguments(args);
		return frag;
	}

    @Override
    protected Map<Object, Class> getCastMap() {
        return Collections.singletonMap(iRideRequestFlow, IRideRequestFlow.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_hail_ride;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pickup = getArguments().getString(PICKUP);
        dropoff = getArguments().getString(DROPOFF);

        pickupLocation.setText(pickup);
        dropoffLocation.setText(dropoff);
    }

    @Override
	@OnClick({R.id.fragment_hail_ride_change_pickup,
			R.id.fragment_hail_ride_change_dropoff,
			R.id.fragment_hail_ride_footer})
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.fragment_hail_ride_footer:
                int passengers = passengerSelect.getPassengers();
				if (passengers != 0) {
					if (TimeLock.isSteerClearRunning()) {
						iRideRequestFlow.onRideConfirm(passengers);
					} else {
                        TimeLock.showTimeLockDialog(getActivity());
					}
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