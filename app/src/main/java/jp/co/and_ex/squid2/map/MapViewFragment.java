package jp.co.and_ex.squid2.map;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import jp.co.and_ex.squid2.R;

/**
 * Created by obata on 2014/09/05.
 */
public class MapViewFragment extends MapFragment {
    MapFragment mMapFragment;
    GoogleMap googleMap;


    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        View root = inflater.inflate(R.layout.fragment_map, container, false);
        initilizeMap();


        return root;
    }

    private void initilizeMap() {
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapView);
        if (mMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mMapFragment = MapFragment.newInstance();
            fragmentTransaction.replace(R.id.mapView, mMapFragment).commit();
        }
        if (mMapFragment != null) {
            googleMap = mMapFragment.getMap();
            if (googleMap != null) {
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng point) {
                        //TODO: your onclick stuffs
                    }
                });
                googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
            } else {
                Log.d("Map", "Google Map is null");
            }
        }
    }
}

