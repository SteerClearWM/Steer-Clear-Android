package steerclear.wm.ui.fragment;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import icepick.State;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import steerclear.wm.R;
import steerclear.wm.data.model.AutoCompleteItem;
import steerclear.wm.ui.CancelDetector;
import steerclear.wm.data.AutoCompleteAdapter;
import steerclear.wm.ui.view.ViewTypefaceButton;
import steerclear.wm.util.Hue;
import steerclear.wm.ui.view.ViewAutoComplete;
import steerclear.wm.ui.view.ViewFooter;
import steerclear.wm.util.ViewUtils;

public class MapFragment extends BaseFragment
	implements View.OnClickListener, CancelDetector.ICancelDetector, IMapFragment {

	@Bind(R.id.fragment_map_pickup) ViewAutoComplete editPickup;
    @Bind(R.id.fragment_map_dropoff) ViewAutoComplete editDropoff;
	@Bind(R.id.fragment_map_view) MapView mapView;
    @Bind(R.id.fragment_map_post) ViewFooter confirmView;
    @Bind(R.id.fragment_map_show_pickup_location) ViewTypefaceButton pickup;
    @Bind(R.id.fragment_map_show_dropoff_location) ViewTypefaceButton dropoff;
    @Bind(R.id.fragment_map_current_location) ImageButton myLocationButton;

    private final static String USER_LOCATION = "user";
    private final static String PICKUP_MARKER_TITLE = "Pick Up Location";
    private final static String DROPOFF_MARKER_TITLE = "Drop Off Location";

    @State CharSequence pickupName, dropoffName;
    @State LatLng pickupLatLng, dropoffLatLng;
    private Geocoder geocoder;
    private Marker pickupMarker, dropoffMarker;
    private AutoCompleteAdapter mAdapter;
    private IRideRequestFlow iRideRequestFlow;
    private static final LatLngBounds BOUNDS_WILLIAMSBURG = new LatLngBounds(
			new LatLng(37.244926, -76.747861), new LatLng(37.295667, -76.686084));

	public MapFragment() {}

	public static MapFragment newInstance(LatLng userLocation) {
		MapFragment frag = new MapFragment();
		Bundle args = new Bundle();
        args.putParcelable(USER_LOCATION, userLocation);
		frag.setArguments(args);
		return frag;
	}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        geocoder = new Geocoder(context, Locale.US);
        mAdapter = new AutoCompleteAdapter(context, R.layout.adapter_view,
                iRideRequestFlow.getGoogleApiClient(), BOUNDS_WILLIAMSBURG);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected Map<Object, Class> getCastMap() {
        return Collections.singletonMap(iRideRequestFlow, IRideRequestFlow.class);
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_map;
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mapView.onCreate(savedInstanceState);

	    if (savedInstanceState != null) {
			editPickup.setText(pickupName);
            editDropoff.setText(dropoffName);
	    }

        pickup.setChosen();

        editPickup.setAdapter(mAdapter);
        editPickup.setOnItemClickListener(pickupAdapterViewClick);

        editDropoff.setAdapter(mAdapter);
        editDropoff.setOnItemClickListener(dropoffAdapterViewClick);

        CancelDetector.addDetector(editPickup, this);
        CancelDetector.addDetector(editDropoff, this);

        mapView.getMapAsync(this);
    }

    @Override
	public void onMapReady(GoogleMap map) {
        UiSettings settings = map.getUiSettings();
        settings.setCompassEnabled(false);
        settings.setMyLocationButtonEnabled(false);
        settings.setIndoorLevelPickerEnabled(false);
        map.setOnMapClickListener(this);
        map.setOnMarkerDragListener(this);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getActivity(), getResources().getString(R.string.fragment_map_marker_hint),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.fragment_map_marker_drop_hint),
                        Toast.LENGTH_SHORT).show();
            }
        });

        LatLng userLocation = getArguments().getParcelable(USER_LOCATION);
        if (pickupLatLng != null) {
            dropMarkerOnMap(pickupLatLng, PICKUP_MARKER_TITLE);
            animateCameraToLocation(pickupLatLng);
        } else {
            dropMarkerOnMap(userLocation, PICKUP_MARKER_TITLE);
            animateCameraToLocation(userLocation);
        }

        if (dropoffLatLng != null) {
            dropMarkerOnMap(dropoffLatLng, DROPOFF_MARKER_TITLE);
        }

        mapView.setVisibility(View.VISIBLE);
	}

    @Override
    public void onMapClick(LatLng latLng) {
        if (!BOUNDS_WILLIAMSBURG.contains(latLng)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.fragment_map_no_service),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (pickup.isChosen()) {
            pickupLatLng = latLng;

            dropMarkerOnMap(pickupLatLng, PICKUP_MARKER_TITLE);

            reverseGeocodeLocation(pickupLatLng, PICKUP_MARKER_TITLE);

            animateCameraToLocation(pickupLatLng);
        } else if (dropoff.isChosen()) {
            dropoffLatLng = latLng;

            dropMarkerOnMap(dropoffLatLng, DROPOFF_MARKER_TITLE);

            reverseGeocodeLocation(dropoffLatLng, DROPOFF_MARKER_TITLE);

            animateCameraToLocation(dropoffLatLng);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {}

    @Override
    public void onMarkerDrag(Marker marker) {}

    @Override
    public void onMarkerDragEnd(Marker marker) {
        reverseGeocodeLocation(marker.getPosition(), marker.getTitle());
    }

    @Override
    @OnClick({R.id.fragment_map_post, R.id.fragment_map_current_location,
        R.id.fragment_map_show_pickup_location, R.id.fragment_map_show_dropoff_location})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_map_post:
                if (pickupLatLng == null) {
                    editPickup.setError(getResources().getString(R.string.fragment_map_pickup_snackbar_text));
                } else if (dropoffLatLng == null) {
                    editDropoff.setError(getResources().getString(R.string.fragment_map_dropoff_snackbar_text));
                } else if (pickupLatLng.equals(dropoffLatLng)) {
                    Snackbar.make(getView(), getResources().getString(R.string.fragment_map_same_location), Snackbar.LENGTH_SHORT).show();
                } else {
                    iRideRequestFlow.onLocationConfirm(pickupLatLng, pickupName,
                            dropoffLatLng, dropoffName);
                }
                break;
            case R.id.fragment_map_current_location:
                animateCameraToLocation(getArguments().getParcelable(USER_LOCATION));
                break;
            case R.id.fragment_map_show_pickup_location:
                pickup.setChosen();
                dropoff.setNotChosen();
                animateCameraToLocation(pickupLatLng);
                break;
            case R.id.fragment_map_show_dropoff_location:
                dropoff.setChosen();
                pickup.setNotChosen();
                animateCameraToLocation(dropoffLatLng);
                break;
        }
    }

    @Override
    public void onCancelClicked(EditText editText) {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (editText.getWindowToken() != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }

        switch (editText.getId()) {
            case R.id.fragment_map_pickup:
                pickupLatLng = null;
                pickupName = null;
                if (pickupMarker != null) {
                    pickupMarker.remove();
                }
                break;
            case R.id.fragment_map_dropoff:
                dropoffLatLng = null;
                dropoffName = null;
                if (dropoffMarker != null) {
                    dropoffMarker.remove();
                }
                break;
        }
    }

    private final AdapterView.OnItemClickListener pickupAdapterViewClick
            = (parent, view, position, id) -> {
        ViewUtils.closeKeyboard(editPickup);

        final AutoCompleteItem item = mAdapter.getItem(position);
        final String placeId = String.valueOf(item.placeId);

        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(
                iRideRequestFlow.getGoogleApiClient(), placeId);
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
                editPickup.setText("");
                return;
            }

            pickupLatLng = place.getLatLng();
            pickupName = place.getAddress();

            dropMarkerOnMap(pickupLatLng, PICKUP_MARKER_TITLE);

            animateCameraToLocation(pickupLatLng);

            places.release();
        });
    };

    private final AdapterView.OnItemClickListener dropoffAdapterViewClick
            = (parent, view, position, id) -> {
        ViewUtils.closeKeyboard(editDropoff);

        final AutoCompleteItem item = mAdapter.getItem(position);
        final String placeId = String.valueOf(item.placeId);

        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(
                iRideRequestFlow.getGoogleApiClient(), placeId);
        placeResult.setResultCallback(places -> {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }

            final Place place = places.get(0);

            if (!BOUNDS_WILLIAMSBURG.contains(place.getLatLng())) {
                places.release();
                Toast.makeText(getActivity(), getResources().getString(R.string.fragment_map_no_service),
                        Toast.LENGTH_SHORT).show();
                dropoffLatLng = null;
                dropoffName = null;
                editDropoff.setText("");
                return;
            }

            dropoffLatLng = place.getLatLng();
            dropoffName = place.getAddress();

            dropMarkerOnMap(dropoffLatLng, DROPOFF_MARKER_TITLE);

            animateCameraToLocation(dropoffLatLng);

            places.release();
        });
    };

    private void dropMarkerOnMap(LatLng latLng, String whichMarker) {
        switch (whichMarker) {
            case PICKUP_MARKER_TITLE:

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(PICKUP_MARKER_TITLE)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                Hue.getHue(getResources().getColor(R.color.accent))))
                        .draggable(true);

                if (pickupMarker != null) {
                    pickupMarker.remove();
                }

                pickupMarker = mapView.getMap().addMarker(options);
                break;
            case DROPOFF_MARKER_TITLE:

                MarkerOptions options1 = new MarkerOptions()
                        .position(latLng)
                        .title(DROPOFF_MARKER_TITLE)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                Hue.getHue(getResources().getColor(R.color.primary_dark))))
                        .draggable(true);

                if (dropoffMarker != null) {
                    dropoffMarker.remove();
                }
                dropoffMarker = mapView.getMap().addMarker(options1);
                break;
        }
    }

    private void reverseGeocodeLocation(LatLng latLng, String whichMarker) {
        if (!BOUNDS_WILLIAMSBURG.contains(latLng)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.fragment_map_no_service),
                    Toast.LENGTH_SHORT).show();

            switch (whichMarker) {
                case PICKUP_MARKER_TITLE:
                    if (pickupLatLng != null) {
                        pickupMarker.setPosition(pickupLatLng);
                    }
                    break;
                case DROPOFF_MARKER_TITLE:
                    if (dropoffLatLng != null) {
                        dropoffMarker.setPosition(dropoffLatLng);
                    }
                    break;
            }
            return;
        }

        try {
            Observable.just(geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(addresses -> {
                        Address address = addresses.get(0);
                        switch (whichMarker) {
                            case PICKUP_MARKER_TITLE:
                                pickupLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                                pickupName = address.getAddressLine(0) + ", " +
                                        address.getAddressLine(1);
                                editPickup.setText(address.getAddressLine(0) + ", " +
                                        address.getAddressLine(1), false);
                                break;
                            case DROPOFF_MARKER_TITLE:
                                dropoffLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                                dropoffName = address.getAddressLine(0) + ", " +
                                        address.getAddressLine(1);
                                editDropoff.setText(address.getAddressLine(0) + ", " +
                                        address.getAddressLine(1), false);
                                break;
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void animateCameraToLocation(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(17)
                .bearing(45)
                .tilt(45)
                .build();

        mapView.getMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}