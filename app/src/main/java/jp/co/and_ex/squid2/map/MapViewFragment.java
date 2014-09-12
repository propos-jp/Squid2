package jp.co.and_ex.squid2.map;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import jp.co.and_ex.squid2.R;
import jp.co.and_ex.squid2.util.BaseFragment;

/**
 * Created by obata on 2014/09/05.
 */
public class MapViewFragment extends BaseFragment {
    private GoogleMap googleMap;

    @Override
  public View onCreateView(
    LayoutInflater inflater,
    ViewGroup container,
    Bundle savedInstanceState) {

    return inflater.inflate(R.layout.fragment_map, container, false);
  }


}
