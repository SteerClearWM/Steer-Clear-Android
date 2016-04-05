package steerclear.wm.ui.fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by mbpeele on 4/4/16.
 */
interface IMapFragment extends OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener {

    @Override
    void onMapReady(GoogleMap googleMap);

    @Override
    void onMarkerDragStart(Marker marker);

    @Override
    void onMarkerDrag(Marker marker);

    @Override
    void onMarkerDragEnd(Marker marker);
}
