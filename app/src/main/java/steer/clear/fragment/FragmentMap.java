package steer.clear.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
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
	implements View.OnClickListener, ViewAutoComplete.AutoCompleteListener,
            OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {

	@Bind(R.id.fragment_map_pickup) ViewAutoComplete pickupText;
    @Bind(R.id.fragment_map_dropoff) ViewAutoComplete dropoffText;
	@Bind(R.id.fragment_map_view) MapView mapView;
    @Bind(R.id.fragment_map_post) Button confirm;

    @Inject EventBus bus;

    private final static Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    private ProgressDialog progressDialog;

	private LatLng pickupLatLng;
	private CharSequence pickupName;
    private LatLng dropoffLatLng;
    private CharSequence dropoffName;

	private final static String PICKUP_TEXT = "pickupText";
    private final static String DROPOFF_TEXT = "dropoffText";
    private final static String USER_LATITUDE = "lat";
    private final static String USER_LONGITUDE = "lng";
    private final static String PICKUP_MARKER_TITLE = "Pick Up Location";
    private final static String DROPOFF_MARKER_TITLE = "Drop Off Location";

    private Geocoder geocoder;
    private Marker pickupMarker;
    private Marker dropoffMarker;
    private AdapterAutoComplete mAdapter;
    private static final LatLngBounds BOUNDS_WILLIAMSBURG = new LatLngBounds(
			new LatLng(37.247247, -76.752889), new LatLng(37.307280, -76.685511));

	public FragmentMap() {}

	public static FragmentMap newInstance(LatLng userLocation) {
		FragmentMap frag = new FragmentMap();
		Bundle args = new Bundle();
        args.putDouble(USER_LATITUDE, userLocation.latitude);
        args.putDouble(USER_LONGITUDE, userLocation.longitude);
		frag.setArguments(args);
		return frag;
	}
	
	@Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
		pickupText.setTextNoFilter(getArguments().getString(PICKUP_TEXT), false);
        dropoffText.setTextNoFilter(getArguments().getString(DROPOFF_TEXT), false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
		getArguments().putString(PICKUP_TEXT, pickupText.getText().toString());
        getArguments().putString(DROPOFF_TEXT, dropoffText.getText().toString());
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
        outState.putString(PICKUP_TEXT, pickupText.getText().toString());
        outState.putString(DROPOFF_TEXT, dropoffText.getText().toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
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
        geocoder = new Geocoder(activity, Locale.US);
        progressDialog = new ProgressDialog(getActivity(), R.style.ProgressDialogTheme);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Locating...");
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

        mAdapter = new AdapterAutoComplete(getActivity(), R.layout.adapter_view,
                getGoogleApiClient(), BOUNDS_WILLIAMSBURG);

		pickupText.setAdapter(mAdapter);
        pickupText.setAutoCompleteListener(this);
        pickupText.setOnItemClickListener(pickupAdapterViewClick);

        dropoffText.setAdapter(mAdapter);
        dropoffText.setAutoCompleteListener(this);
        dropoffText.setOnItemClickListener(dropoffAdapterViewClick);

		mapView.onCreate(savedInstanceState);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

	    if (savedInstanceState != null) {
			pickupText.setTextNoFilter(savedInstanceState.getString(PICKUP_TEXT), false);
            dropoffText.setTextNoFilter(savedInstanceState.getString(DROPOFF_TEXT), false);
	    }

        mapView.getMapAsync(this);
	}

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float displayHeight = size.y;

        AnimatorSet animatorSet = new AnimatorSet();
        if (enter) {
            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(getActivity(), "y", displayHeight, 0));
        } else {
            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(getActivity(), "y", 0, displayHeight));
        }

        animatorSet.setDuration(1000);
        animatorSet.setInterpolator(INTERPOLATOR);
        return animatorSet;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
	public void onMapReady(GoogleMap map) {
        map.setOnMarkerDragListener(this);
        map.setOnMarkerClickListener(this);

        LatLng userLocation = new LatLng(getArguments().getDouble(USER_LATITUDE),
                getArguments().getDouble(USER_LONGITUDE));
		MarkerOptions options = new MarkerOptions()
			    .position(userLocation)
                .title(PICKUP_MARKER_TITLE)
                .draggable(true);
        pickupMarker = map.addMarker(options);

		CameraPosition cameraPosition = new CameraPosition.Builder()
		    .target(userLocation)
            .zoom(17)
            .bearing(90)
            .tilt(30)
		    .build();
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(getActivity(), "Long-press then drag the marker to select a location",
                Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng markerPos = marker.getPosition();
        try {
            Observable.just(geocoder.getFromLocation(markerPos.latitude, markerPos.longitude, 1))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(addresses -> {
                        Address address = addresses.get(0);
                        switch (marker.getTitle()) {
                            case PICKUP_MARKER_TITLE:
                                pickupLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                                pickupName = address.getAddressLine(0);
                                pickupText.setTextNoFilter(address.getAddressLine(0) +
                                        address.getAddressLine(1), false);
                                break;
                            case DROPOFF_MARKER_TITLE:
                                dropoffLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                                dropoffName = address.getAddressLine(0);
                                dropoffText.setTextNoFilter(address.getAddressLine(0) +
                                        address.getAddressLine(1), false);
                                break;
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                break;
            case R.id.fragment_map_dropoff:
                dropoffLatLng = null;
                dropoffName = null;
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
        progressDialog.show();

        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(getGoogleApiClient(), placeId);
        placeResult.setResultCallback(places -> {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }

            final Place place = places.get(0);

            if (!BOUNDS_WILLIAMSBURG.contains(place.getLatLng())) {
                places.release();
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_too_far_for_steer_clear),
                        Toast.LENGTH_SHORT).show();
                pickupLatLng = null;
                pickupName = null;
                progressDialog.dismiss();
                return;
            }

            progressDialog.dismiss();

            pickupLatLng = place.getLatLng();
            pickupName = place.getAddress();

            GoogleMap map = mapView.getMap();
            pickupMarker.remove();
            MarkerOptions options = new MarkerOptions()
                    .position(pickupLatLng)
                    .title(PICKUP_MARKER_TITLE)
                    .draggable(true);
            pickupMarker = map.addMarker(options);

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
                imm.hideSoftInputFromWindow(pickupText.getWindowToken(), 0);
            }
        });
    };

    private final AdapterView.OnItemClickListener dropoffAdapterViewClick
            = (parent, view, position, id) -> {
        final AdapterAutoComplete.AdapterAutoCompleteItem item = mAdapter.getItem(position);
        final String placeId = String.valueOf(item.placeId);
        progressDialog.show();

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
                dropoffLatLng = null;
                dropoffName = null;
                progressDialog.dismiss();
                return;
            }

            progressDialog.dismiss();

            dropoffLatLng = place.getLatLng();
            dropoffName = place.getAddress();

            GoogleMap map = mapView.getMap();

            MarkerOptions options = new MarkerOptions()
                    .position(dropoffLatLng)
                    .title(DROPOFF_MARKER_TITLE)
                    .draggable(true);

            if (dropoffMarker != null) {
                dropoffMarker.remove();
            }
            dropoffMarker = map.addMarker(options);

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
                imm.hideSoftInputFromWindow(pickupText.getWindowToken(), 0);
            }
        });
    };
}