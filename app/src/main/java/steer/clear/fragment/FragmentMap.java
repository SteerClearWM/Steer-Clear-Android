package steer.clear.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import steer.clear.MainApp;
import steer.clear.R;
import steer.clear.activity.ActivityHome;
import steer.clear.adapter.AdapterAutoComplete;
import steer.clear.event.EventPlacesChosen;
import steer.clear.util.Logger;
import steer.clear.view.ViewAutoComplete;

/**
 * Class that deals with all GoogleMaps stuff. 
 * @author Miles Peele
 *
 */
public class FragmentMap extends Fragment
	implements OnMapReadyCallback, ViewAutoComplete.AutoCompleteListener, View.OnClickListener {

	@Bind(R.id.fragment_map_pickup) ViewAutoComplete pickup;
    @Bind(R.id.fragment_map_dropoff) ViewAutoComplete dropoff;
	@Bind(R.id.fragment_map_view) MapView mapView;
    @Bind(R.id.fragment_map_post) ImageButton post;

    @Inject EventBus bus;

	private LatLng pickupLatLng;
	private CharSequence pickupName;
    private LatLng dropoffLatLng;
    private CharSequence dropoffName;

	private final static String PICKUP_TEXT = "pickup";
    private final static String DROPOFF_TEXT = "dropoff";

    private final static String LATITUDE = "latitude";
    private final static String LONGITUDE = "longitude";

    private AdapterAutoComplete mAdapter;
    private static final LatLngBounds BOUNDS_WILLIAMSBURG = new LatLngBounds(
			new LatLng(37.247247, -76.752889), new LatLng(37.307280, -76.685511));

	public FragmentMap() {}

	public static FragmentMap newInstance(LatLng currentLatLng) {
		FragmentMap frag = new FragmentMap();
		Bundle args = new Bundle();
        args.putDouble(LATITUDE, currentLatLng.latitude);
        args.putDouble(LONGITUDE, currentLatLng.longitude);
		frag.setArguments(args);
		return frag;
	}
	
	@Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
		pickup.setTextNoFilter(getArguments().getString(PICKUP_TEXT), false);
        dropoff.setTextNoFilter(getArguments().getString(DROPOFF_TEXT), false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
		getArguments().putString(PICKUP_TEXT, pickup.getText().toString());
        getArguments().putString(DROPOFF_TEXT, dropoff.getText().toString());
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
        outState.putString(PICKUP_TEXT, pickup.getText().toString());
        outState.putString(DROPOFF_TEXT, dropoff.getText().toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainApp) activity.getApplication()).getApplicationComponent().inject(this);
    }

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_map, container, false);
		ButterKnife.bind(this, rootView);

        mAdapter = new AdapterAutoComplete(getActivity(), android.R.layout.simple_dropdown_item_1line,
                ((ActivityHome) getActivity()).mGoogleApiClient, BOUNDS_WILLIAMSBURG);

		pickup.setAdapter(mAdapter);
		pickup.setAutoCompleteListener(this);
        pickup.setOnItemClickListener(pickupAdapterViewClick);

        dropoff.setAdapter(mAdapter);
        dropoff.setAutoCompleteListener(this);
        dropoff.setOnItemClickListener(dropoffAdapterViewClick);

		mapView.onCreate(savedInstanceState);
        mapView.setBackground(null);
        mapView.getMapAsync(this);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

	    if (savedInstanceState != null) {
			pickup.setTextNoFilter(savedInstanceState.getString(PICKUP_TEXT), false);
            dropoff.setTextNoFilter(savedInstanceState.getString(DROPOFF_TEXT), false);
	    }
	}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
	public void onMapReady(GoogleMap map) {
        double latitude = getArguments().getDouble(LATITUDE);
        double longitude = getArguments().getDouble(LONGITUDE);
        LatLng userLatLng = new LatLng(latitude, longitude);

		MarkerOptions marker = new MarkerOptions()
			.position(userLatLng)
			.title(getResources().getString(R.string.fragment_map_current_location_marker));
		map.addMarker(marker);

		CameraPosition cameraPosition = new CameraPosition.Builder()
		    .target(userLatLng)
                .zoom(17)
                .bearing(90)
                .tilt(30)
		    .build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

    @Override
    @OnClick(R.id.fragment_map_post)
    public void onClick(View v) {
        if (pickupLatLng == null) {
            Snackbar.make(getView(), getResources().getString(R.string.fragment_map_pickup_snackbar_text), Snackbar.LENGTH_SHORT).show();
        } else if (dropoffLatLng == null) {
            Snackbar.make(getView(), getResources().getString(R.string.fragment_map_dropoff_snackbar_text), Snackbar.LENGTH_SHORT).show();
        } else if (pickupLatLng.equals(dropoffLatLng)) {
            Snackbar.make(getView(), getResources().getString(R.string.fragment_map_same_location), Snackbar.LENGTH_SHORT).show();
        } else {
            bus.post(new EventPlacesChosen(pickupLatLng, pickupName, dropoffLatLng, dropoffName));
        }
    }

    @Override
    public void clearClicked(View v) {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (v.getWindowToken() != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        switch (v.getId()) {
            case R.id.fragment_map_pickup:
                pickupLatLng = null;
                pickupName = null;
                pickup.setText("");
                break;
            case R.id.fragment_map_dropoff:
                dropoffLatLng = null;
                dropoffName = null;
                dropoff.setText("");
                break;
        }
    }

    private GoogleApiClient getGoogleApiClient() {
        return ((ActivityHome) getActivity()).mGoogleApiClient;
    }

    private final AdapterView.OnItemClickListener pickupAdapterViewClick
            = (parent, view, position, id) -> {
        final AdapterAutoComplete.AdapterAutoCompleteItem item = mAdapter.getItem(position);
        final String placeId = String.valueOf(item.placeId);

        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(getGoogleApiClient(), placeId);
        placeResult.setResultCallback(places -> {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }

            // Get the Place object from the buffer.
            final Place place = places.get(0);

            if (!BOUNDS_WILLIAMSBURG.contains(place.getLatLng())) {
                places.release();
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_too_far_for_steer_clear),
                        Toast.LENGTH_SHORT).show();
                clearClicked(pickup);
                return;
            }

            pickupLatLng = place.getLatLng();
            pickupName = place.getName();

            GoogleMap map = mapView.getMap();
            MarkerOptions marker = new MarkerOptions().position(pickupLatLng);
            marker.title(getResources().getString(R.string.fragment_map_pickup_location_marker));
            map.addMarker(marker);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(pickupLatLng)
                    .zoom(17)
                    .bearing(90)
                    .tilt(30)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            places.release();
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (getView().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(pickup.getWindowToken(), 0);
            }
        });
    };

    private final AdapterView.OnItemClickListener dropoffAdapterViewClick
            = (parent, view, position, id) -> {
        final AdapterAutoComplete.AdapterAutoCompleteItem item = mAdapter.getItem(position);
        final String placeId = String.valueOf(item.placeId);

        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(getGoogleApiClient(), placeId);
        placeResult.setResultCallback(places -> {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }

            // Get the Place object from the buffer.
            final Place place = places.get(0);

            if (!BOUNDS_WILLIAMSBURG.contains(place.getLatLng())) {
                places.release();
                Snackbar.make(getView(), getResources().getString(R.string.fragment_map_no_service),
                        Snackbar.LENGTH_SHORT).show();
                clearClicked(dropoff);
                return;
            }

            dropoffLatLng = place.getLatLng();
            dropoffName = place.getName();

            GoogleMap map = mapView.getMap();
            MarkerOptions marker = new MarkerOptions().position(dropoffLatLng);
            marker.title(getResources().getString(R.string.fragment_map_dropoff_location_marker));
            map.addMarker(marker);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(dropoffLatLng)
                    .zoom(17)
                    .bearing(90)
                    .tilt(30)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            places.release();
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (getView().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(pickup.getWindowToken(), 0);
            }
        });
    };
}