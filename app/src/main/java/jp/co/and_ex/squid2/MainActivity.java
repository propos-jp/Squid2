package jp.co.and_ex.squid2;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;


import Graph.GraphFragment;
import Graph.GraphListener;
import jp.co.and_ex.squid2.list.ListViewFragment;
import jp.co.and_ex.squid2.map.MyMapFragment;
import jp.co.and_ex.squid2.observe.DeviceListFragment;
import jp.co.and_ex.squid2.observe.ObserveViewFragment;
import jp.co.and_ex.squid2.util.OnFragmentInteractionListener;
import jp.co.and_ex.squid2.util.TabListener;


public class MainActivity extends SherlockActivity implements OnFragmentInteractionListener, GraphListener, ObserveViewFragment.ObserveViewListener, DeviceListFragment.DeviceListListener ,LocationListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private LocationManager locationManager = null;

    public Location getLocation() {
        return location;
    }

    private Location location; // location


    // flag for GPS status
	boolean isGPSEnabled = false;
    // flag for network status
	boolean isNetworkEnabled = false;
	// flag for GPS status
	boolean canGetLocation = false;


    // The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 30 * 1; // 1 minute


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //ActionBarをGetしてTabModeをセット
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        setTheme(R.style.Sherlock___Theme_DarkActionBar);
        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.list_tab_name))
                .setTabListener(new TabListener<ListViewFragment>(
                        this, getString(R.string.list_tab_tag), ListViewFragment.class)));
        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.map_tab_name))
                .setTabListener(new TabListener<MyMapFragment>(
                        this, getString(R.string.map_tab_tag), MyMapFragment.class)));
        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.observe_tab_name))
                .setTabListener(new TabListener<ObserveViewFragment>(
                        this, getString(R.string.observe_tab_tag), ObserveViewFragment.class)));

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS != resultCode) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = ErrorDialogFragment.newInstance(dialog);
                errorFragment.show(getFragmentManager(),"aaa");
            }
        }
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);


    }

	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
      com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
      inflater.inflate(R.menu.optionmenu, menu);
      return true;
    }




    @Override
    public void onFragmentInteraction(Integer id) {
        GraphFragment.show(getFragmentManager(), this, id);
    }

    @Override
    public void onCloseButtonClick() {
        GraphFragment.hide(getFragmentManager());

    }


    @Override
    public void viewDeviceList() {
        DeviceListFragment.show(getFragmentManager(), this);

    }


    @Override
    public void closeWindow() {
        DeviceListFragment.hide(getFragmentManager());
    }

    @Override
    public void listSelected(String address) {
        Log.d("Selected Address", address);
        DeviceListFragment.hide(getFragmentManager());

        ObserveViewFragment fragment = (ObserveViewFragment) getFragmentManager().findFragmentByTag(getString(R.string.observe_tab_tag));
        if (fragment != null) {
            fragment.setDeviceAddress(address);
            return;
        }
        fragment.connectStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        try{
             // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				Toast.makeText(this, "位置情報サービスに異常が発生しました", Toast.LENGTH_SHORT).show();
			} else {
				this.canGetLocation = true;
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						}
					}
				}
			}

		} catch (Exception e) {
			Log.d(TAG,e.getMessage());
            Toast.makeText(this, "位置情報サービスに異常が発生しました",Toast.LENGTH_SHORT).show();
		}

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationManager != null){
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}



