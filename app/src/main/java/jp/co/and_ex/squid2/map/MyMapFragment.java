package jp.co.and_ex.squid2.map;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.and_ex.squid2.MainActivity;
import jp.co.and_ex.squid2.db.ObserveDataContract;
import jp.co.and_ex.squid2.db.ObserveDataProvider;
import jp.co.and_ex.squid2.util.Common;
import jp.co.and_ex.squid2.util.OnFragmentInteractionListener;

/**
 * Created by obata on 2014/09/19.
 */
public class MyMapFragment extends MapFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String TAG = MyMapFragment.class.getSimpleName();
    private GoogleMap googleMap;
    private List<Integer> globalId_array;
    private List<LatLng> latLngs;
    private List<String> titles;
    private List<String> userIds;
    private HashMap<String, Integer> hashMap;
    private OnFragmentInteractionListener mListener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        Log.d(TAG, "Init Map");
        if (savedInstanceState == null) {
            initMap();
        }
        // Loaderの初期化
        getLoaderManager().initLoader(0, null, this);
    }

    private void initMap() {
        googleMap = getMap();
        Location location = ((MainActivity) getActivity()).getLocation();
        if (location != null && googleMap != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (googleMap != null) {
                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, 12);
                googleMap.moveCamera(cu);
            }
        }

    }

    private void initMaker() {
        if (latLngs != null && googleMap != null) {
            hashMap = new HashMap<String, Integer>();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String myId = pref.getString("user_name", "");

            for (int i = 0; i < latLngs.size(); i++) {
                MarkerOptions options = new MarkerOptions();
                options.position(latLngs.get(i));
                options.title(Integer.toString(i + 1));
                options.snippet(titles.get(i));

                if (myId.length() > 0 && myId.equals(userIds.get(i))){
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                }else{
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                Marker marker = googleMap.addMarker(options);
                hashMap.put(marker.getId(), globalId_array.get(i));
            }
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(Marker marker) {
                    String id = marker.getId();
                    if (id != null && hashMap != null) {
                        if (hashMap.containsKey(id)) {
                            Integer i = hashMap.get(id);
                            mListener.onFragmentInteraction(i);
                        }
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(getClass().getSimpleName(), "onCreateLoader called.");
        return new CursorLoader(getActivity(), ObserveDataProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        if (cursor.getCount() == 0) {
            globalId_array = null;
            latLngs = null;
            titles = null;
            userIds = null;
            return;
        }

        globalId_array = new ArrayList<Integer>();
        latLngs = new ArrayList<LatLng>();
        titles = new ArrayList<String>();
        userIds = new ArrayList<String>();


        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(ObserveDataContract.KEY_ID);
                int titleIndex = cursor.getColumnIndex(ObserveDataContract.KEY_OBSERVE_DATE);
                int latIndex = cursor.getColumnIndex(ObserveDataContract.KEY_LATITUDE);
                int longIndex = cursor.getColumnIndex(ObserveDataContract.KEY_LONGITUDE);
                int userIdIndex = cursor.getColumnIndex(ObserveDataContract.KEY_USER_ID);
                Log.d(TAG, "id:" + Integer.toString(cursor.getInt(idIndex)));
                Log.d(TAG, "latitude" + Double.toString(cursor.getInt(latIndex)));
                Log.d(TAG, "longitude" + Double.toString(cursor.getInt(longIndex)));
                globalId_array.add(cursor.getInt(idIndex));
                LatLng latLng = new LatLng(cursor.getDouble(latIndex), cursor.getDouble(longIndex));
                latLngs.add(latLng);
                titles.add(cursor.getString(userIdIndex) + ":" + Common.getJST(cursor.getString(titleIndex)));
                userIds.add(cursor.getString(userIdIndex));
            } while (cursor.moveToNext());
        }
        initMaker();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
