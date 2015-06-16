package steer.clear;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
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

import java.util.Locale;

import steer.clear.AdapterAutoComplete.AdapterAutoCompleteItem;

/**
 * Class that deals with all GoogleMaps stuff. 
 * @author Miles Peele
 *
 */
public class FragmentMap extends Fragment
	implements OnMapReadyCallback, AdapterView.OnItemClickListener,
        OnMarkerClickListener, OnClickListener, ViewAutoComplete.AutoCompleteListener {
	
	// Global views
	private ViewAutoComplete input;
	private TextView inputHint;
	private ImageButton previousFragment;
	private ProgressBar inputSuggestionsLoading;
	private MapView mapView;

	// Stores the user's LatLng and the LatLng they chose as pickup/dropoff
	private static LatLng userLatLng;
	public LatLng chosenLatLng;
	public CharSequence chosenLocationName;
	
	// Controls if this mapfragment is going to ask for pickup or dropoff
	private final static String PICKUP = "pickup";
	private final static String DROPOFF = "dropoff";
	private final static String TAG = "tag";
	
	// Save instance state tags
	private final static String INPUT_TEXT = "input";

    // Tags for Bundles
    private final static String LATITUDE = "latitude";
    private final static String LONGITUDE = "longitude";
	private final static String FROM_HAIL_RIDE = "fromHailRide";

    // When finding a chosen location
	private ProgressDialog progress;
	
	// For more Google warlock magic stuff
	private AdapterAutoComplete mAdapter;
	private static final LatLngBounds BOUNDS_WILLIAMSBURG = new LatLngBounds(
			new LatLng(37.247247, -76.752889), new LatLng(37.307280, -76.685511));
	
	// Listener defines communication between Fragments and ActivityHome
	private ListenerForFragments listener;
	
	/**
	 * Empty constructor cuz this is needed (still don't know why)
	 */
	public FragmentMap() {}
	
	/**
	 * Creates a new instance of a FragmentMap fragment
	 * Assigns whatever tag (pickup or dropoff) to this fragment's arguments
	 * Stores the passed latlng as a static variable
	 * @param tag
	 * @param currentLatLng
	 * @return FragmentMap fragment
	 */
	public static FragmentMap newInstance(String tag, LatLng currentLatLng, boolean fromHailRide) {
		FragmentMap frag = new FragmentMap();
		Bundle args = new Bundle();
		args.putString(TAG, tag);
        args.putDouble(LATITUDE, currentLatLng.latitude);
        args.putDouble(LONGITUDE, currentLatLng.longitude);
		args.putBoolean(FROM_HAIL_RIDE, fromHailRide);
		frag.setArguments(args);
		return frag;
	}
	
	@Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
		input.setText(getArguments().getString(INPUT_TEXT), false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
		getArguments().putString(INPUT_TEXT, input.getText().toString());
}
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
		outState.putString(INPUT_TEXT, input.getText().toString());
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
        try {
            listener = (ListenerForFragments) activity;
        } catch (ClassCastException e) {
        	e.printStackTrace();
        }
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		progress = new ProgressDialog(getActivity(), ProgressDialog.STYLE_HORIZONTAL);
		progress.setMessage("Locating...");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_map, container, false);

		input = (ViewAutoComplete) rootView.findViewById(R.id.fragment_map_input);
		inputHint = (TextView) rootView.findViewById(R.id.fragment_map_input_hint);
		if (getArguments() != null) {
			String tag = getArguments().getString(TAG);
			if (tag == PICKUP) {
				inputHint.setHint(getActivity().getResources().getString(R.string.fragment_map_pickup_input_hint));
			} else {
				inputHint.setHint(getActivity().getResources().getString(R.string.fragment_map_dropoff_input_hint));
			}
		}
		
		inputSuggestionsLoading = (ProgressBar) rootView.findViewById(R.id.fragment_map_input_suggestions_loading);
		input.setLoadingIndicator(inputSuggestionsLoading);
		
		previousFragment = (ImageButton) rootView.findViewById(R.id.fragment_map_back);
		previousFragment.setOnClickListener(this);

		mapView = (MapView) rootView.findViewById(R.id.fragment_map_view);
		mapView.onCreate(savedInstanceState);
		mapView.setBackground(null);
		mapView.getMapAsync(this);
		
		mAdapter = new AdapterAutoComplete(getActivity(), android.R.layout.simple_dropdown_item_1line, 
				listener.getGoogleApiClient(), BOUNDS_WILLIAMSBURG, null);
		input.setAdapter(mAdapter);
		input.setOnItemClickListener(this);
        input.setAutoCompleteListener(this);
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);

	    if (savedInstanceState != null) {
	    	input.setText(savedInstanceState.getString(INPUT_TEXT), false);
	    }
	}

	@Override
	public void onMapReady(GoogleMap map) {
		map.setOnMarkerClickListener(this);
        double latitude = getArguments().getDouble(LATITUDE);
        double longitude = getArguments().getDouble(LONGITUDE);
        LatLng userLatLng = new LatLng(latitude, longitude);

		MarkerOptions marker = new MarkerOptions()
			.position(userLatLng)
			.title("Your Location");
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
	public void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == Activity.RESULT_OK) {
                showProgressDialog();
				Place place = PlacePicker.getPlace(data, getActivity());
				
				if (!BOUNDS_WILLIAMSBURG.contains(place.getLatLng())) {
					Toast.makeText(getActivity(), "Steer Clear does not service chosen location.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				chosenLatLng = place.getLatLng();
	            chosenLocationName = place.getName();
	            
	            input.setText(chosenLocationName + " " + place.getAddress(), false);
	            
	            goToNextFragment();
	            dismissProgressDialog();
			}
	    } 
	}

	@Override
	public boolean onMarkerClick(final Marker marker) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        String tag = getArguments().getString(TAG);
        alertDialog.setTitle("Choose " + tag.substring(0, 1).toUpperCase(Locale.getDefault()) + tag.substring(1) + " Location");
        alertDialog.setMessage("Would you like to choose a nearby place as your " + tag + " location?" +
                " You must choose a location that has a name, like 'Barrett Hall.'");
        alertDialog.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
        	
            @Override
			public void onClick(DialogInterface dialog,int which) {
            	try {
            		PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
					startActivityForResult(builder.build(getActivity()), 1);
					getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				} catch (GooglePlayServicesRepairableException e) {
					e.printStackTrace();
				} catch (GooglePlayServicesNotAvailableException e) {
					e.printStackTrace();
				}
            }

        });

        alertDialog.setNegativeButton("Nah", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}

		});

		alertDialog.show();

        return false;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		showProgressDialog();
		final AdapterAutoCompleteItem item = mAdapter.getItem(position);
		final String placeId = String.valueOf(item.placeId);
		
        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(listener.getGoogleApiClient(), placeId);
        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {

			@Override
			public void onResult(PlaceBuffer places) {
				if (!places.getStatus().isSuccess()) {
					Log.v("Miles", "Place query did not complete. Error: " + places.getStatus().toString());
					places.release();
					return;
				}

				// Get the Place object from the buffer.
				final Place place = places.get(0);

				if (!BOUNDS_WILLIAMSBURG.contains(place.getLatLng())) {
					places.release();
					dismissProgressDialog();
					Toast.makeText(getActivity(), "SteerClear does not service this location", Toast.LENGTH_SHORT).show();
					clearClicked(input);
					return;
				}

				chosenLatLng = place.getLatLng();
				chosenLocationName = place.getName();

				// Get the LatLng from the Place object and replaces it with the current map marker
				GoogleMap map = mapView.getMap();
				map.clear();
				MarkerOptions marker = new MarkerOptions().position(chosenLatLng);
				marker.title("Chosen Location");
				map.addMarker(marker);

				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(chosenLatLng)
						.zoom(17)
						.bearing(90)
						.tilt(30)
						.build();
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

				places.release();
				final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				if (getView().getWindowToken() != null) {
					imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
				}
				dismissProgressDialog();
			}

		});
	}

	@Override
	public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		// float displayWidth = size.x;
		float displayHeight = size.y;

		Animator animator;
		if (enter) {
			animator = ObjectAnimator.ofFloat(getActivity(), "y", displayHeight, 0);
		} else {
			animator = ObjectAnimator.ofFloat(getActivity(), "y", 0, displayHeight);
        }

        animator.setDuration(1000);
		animator.setInterpolator(new AccelerateDecelerateInterpolator());
		return animator;
	}
	
	@Override
	public void onClick(View v) {
		getActivity().onBackPressed();
	}

	private void goToNextFragment() {
		if (chosenLatLng != null) {
			String tag = getArguments().getString(TAG);
			boolean fromHailRide = getArguments().getBoolean(FROM_HAIL_RIDE);
			if (!fromHailRide) {
				listener.setChosenLocation(tag, chosenLatLng, chosenLocationName);
			} else {
				listener.onChosenLocationChanged(tag, chosenLatLng, chosenLocationName);
			}
		} else {
			Toast.makeText(getActivity(), "Choose a location first!", Toast.LENGTH_SHORT).show();
		}
	}

	private void showProgressDialog() {
		if (progress != null && !progress.isShowing()) {
			progress.show();
		}
	}
	
	private void dismissProgressDialog() {
		if (progress != null && progress.isShowing()) {
			progress.dismiss();
		}
	}

    @Override
    public void arrowClicked(View v) {
		final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (v.getWindowToken() != null) {
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
		goToNextFragment();
    }

    @Override
    public void clearClicked(View v) {
		final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (v.getWindowToken() != null) {
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
		chosenLatLng = null;
		chosenLocationName = null;
		input.setText("");
    }
}