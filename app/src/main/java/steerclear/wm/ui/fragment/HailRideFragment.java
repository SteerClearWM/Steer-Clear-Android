package steerclear.wm.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import steerclear.wm.MainApp;
import steerclear.wm.R;
import steerclear.wm.data.event.EventPostPlacesChosen;
import steerclear.wm.util.TimeLock;
import steerclear.wm.ui.view.ViewFooter;
import steerclear.wm.ui.view.ViewPassengerSelect;
import steerclear.wm.ui.view.ViewTypefaceTextView;

public class HailRideFragment extends Fragment implements OnClickListener {

	@Bind(R.id.fragment_hail_ride_pickup_location) ViewTypefaceTextView pickupLocation;
	@Bind(R.id.fragment_hail_ride_change_pickup) AppCompatImageButton changePickup;
	@Bind(R.id.fragment_hail_ride_dropoff_location) ViewTypefaceTextView dropoffLocation;
	@Bind(R.id.fragment_hail_ride_change_dropoff) AppCompatImageButton changeDropoff;
    @Bind(R.id.fragment_hail_ride_passenger_select) ViewPassengerSelect passengerSelect;
	@Bind(R.id.fragment_hail_ride_footer) ViewFooter postRide;

	@Inject EventBus bus;

	private final static String PICKUP = "editPickup";
	private final static String DROPOFF = "editDropoff";

	public HailRideFragment(){}

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
	@OnClick({R.id.fragment_hail_ride_change_pickup,
			R.id.fragment_hail_ride_change_dropoff,
			R.id.fragment_hail_ride_footer})
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.fragment_hail_ride_footer:
				if (passengerSelect.getPassengers() != 0) {
					if (TimeLock.isSteerClearRunning()) {
						bus.post(new EventPostPlacesChosen(passengerSelect.getPassengers()));
					} else {
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						builder.setTitle(R.string.error_dialog_not_running_title);
						builder.setMessage(R.string.error_dialog_not_running_body);
						builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            dialog.dismiss();
                        });
						builder.show();
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