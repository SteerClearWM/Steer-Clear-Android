package steer.clear;

import java.util.Locale;

import steer.clear.AdapterAutoComplete.AdapterAutoCompleteItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
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

/**
 * Class that deals with all GoogleMaps stuff. 
 * @author Miles Peele
 *
 */
public class FragmentMap extends Fragment
	implements OnMapReadyCallback, AdapterView.OnItemClickListener, OnTouchListener, OnMarkerClickListener, OnClickListener {
	
	// Global views
	private ViewDelayAutoComplete input;
	private TextView inputHint;
	private ImageButton previousFragment;
	private ProgressBar inputSuggestionsLoading;
	private MapView mapView;

	// Stores the user's LatLng and the LatLng they chose from the AutoComplete results
	private static LatLng userLatLng;
	public static LatLng chosenLatLng;
	public static CharSequence chosenLocationName;
	
	// Controls if this mapfragment is going to ask for pickup or dropoff
	private final static String PICKUP = "pickup";
	private final static String DROPOFF = "dropoff";
	private final static String TAG = "tag";
	
	// Save instance state tags
	private final static String INPUT_TEXT = "input";
	
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
	public static FragmentMap newInstance(String tag, LatLng currentLatLng) {
		FragmentMap frag = new FragmentMap();
		Bundle args = new Bundle();
		args.putString(TAG, tag);
		frag.setArguments(args);
		userLatLng = currentLatLng;
		return frag;
	}
	
	@Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
        
        if (!input.getText().toString().isEmpty()) {
        	outState.putString(INPUT_TEXT, input.getText().toString());
        }
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
		progress = new ProgressDialog(getActivity(), ProgressDialog.STYLE_HORIZONTAL);
		progress.setMessage("Locating...");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_map, container, false);

		input = (ViewDelayAutoComplete) rootView.findViewById(R.id.fragment_map_input);
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
		mapView.getMapAsync(this);
		
		mAdapter = new AdapterAutoComplete(getActivity(), android.R.layout.simple_dropdown_item_1line, 
				listener.getGoogleApiClient(), BOUNDS_WILLIAMSBURG, null);
		input.setAdapter(mAdapter);
		
		input.setOnItemClickListener(this);
		input.setOnTouchListener(this);
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    
	    if (savedInstanceState != null) {
	    	input.setText(savedInstanceState.getString(INPUT_TEXT));
	    }
	}

	@Override
	public void onMapReady(GoogleMap map) {
		map.setOnMarkerClickListener(this);
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
		showProgressDialog();
		if (requestCode == 1) {
			if (resultCode == Activity.RESULT_OK) {
				Place place = PlacePicker.getPlace(data, getActivity());
				
				if (!BOUNDS_WILLIAMSBURG.contains(place.getLatLng())) {
					Toast.makeText(getActivity(), "Steer Clear does not service chosen location.", Toast.LENGTH_SHORT).show();
					return;
				}
				
				chosenLatLng = place.getLatLng();
	            chosenLocationName = place.getName();
	            
	            input.setText(chosenLocationName + " " + place.getAddress());
	            
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
        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>()	{

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
	            	return;
	            }
	            
	            chosenLatLng = place.getLatLng();
	            chosenLocationName = place.getName();
	            
	            // Get the LatLng from the Place object and replaces it with the current map marker
	            GoogleMap map = mapView.getMap();
	            map.clear();
	            MarkerOptions marker = new MarkerOptions().position(chosenLatLng);
	            map.addMarker(marker);
	            
	            CameraPosition cameraPosition = new CameraPosition.Builder()
				    .target(chosenLatLng)      
				    .zoom(17)                   
				    .bearing(90)                
				    .tilt(30)                  
				    .build();           
	            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

	            places.release(); 
	            dismissProgressDialog();
	            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
			}
        	
        });
	}
	
	@Override
	public void onClick(View v) {
		getActivity().onBackPressed();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		final ViewDelayAutoComplete view = (ViewDelayAutoComplete) v;
		final int DRAWABLE_LEFT = 0;
        //final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        //final int DRAWABLE_BOTTOM = 3;
        
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getRawX() >= (view.getRight() - view.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
            	goToNextFragment();
            	return true;
            } 
            
            if (event.getRawX() <= (view.getLeft() + view.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
            	view.setText("");
            	return true;
            }
         }
         return false;
	}

	private void goToNextFragment() {
		if (chosenLatLng != null) {
			String tag = getArguments().getString(TAG);
			if (tag == PICKUP) {
				listener.setPickup(chosenLatLng, chosenLocationName);
			} else {
				listener.setDropoff(chosenLatLng, chosenLocationName);
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

}
