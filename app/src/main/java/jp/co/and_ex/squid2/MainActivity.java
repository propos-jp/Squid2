package jp.co.and_ex.squid2;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import Graph.GraphFragment;
import Graph.GraphListener;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import jp.co.and_ex.squid2.db.ObserveDataContract;
import jp.co.and_ex.squid2.list.ListViewFragment;
import jp.co.and_ex.squid2.map.MyMapFragment;
import jp.co.and_ex.squid2.observe.DeviceListFragment;
import jp.co.and_ex.squid2.observe.ObserveViewFragment;
import jp.co.and_ex.squid2.util.OnFragmentInteractionListener;
import jp.co.and_ex.squid2.util.TabListener;


public class MainActivity extends SherlockActivity implements OnFragmentInteractionListener, GraphListener, ObserveViewFragment.ObserveViewListener, DeviceListFragment.DeviceListListener, LocationListener {
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

    // download id
    DownloadManager downloadManager;
    DownloadManager.Query query;


    private long download_id = -1;
    private DownloadReceiver downloadReceiver;

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
                errorFragment.show(getFragmentManager(), "aaa");
            }
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        downloadReceiver = new DownloadReceiver();
        registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        String userId = pref.getString("user_name", "unknown");
        if ("unknown".equals(userId)){
             Toast.makeText(this, "ユーザー名を登録してください", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloadReceiver != null) {
            unregisterReceiver(downloadReceiver);
        }
    }

    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.optionmenu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuSetting) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivityForResult(intent, 0);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Integer id) {
        GraphFragment.show(getFragmentManager(), this, id);
    }

    @Override
    public void onFragmentRefresh() {
        downloadCSV();
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

        try {
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
            Log.d(TAG, e.getMessage());
            Toast.makeText(this, "位置情報サービスに異常が発生しました", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationManager != null) {
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

    private void downloadCSV() {

        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        if (download_id != -1) {
            return;
        }

        Uri uri = Uri.parse(getString(R.string.downloadCSV));
        DownloadManager.Request request = new DownloadManager.Request(uri);


        request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, getString(R.string.data_csv));
        request.setTitle("Squid Download");
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);


        File pathExternalDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (!pathExternalDir.exists()) {
            pathExternalDir.mkdirs();
        }

        download_id = downloadManager.enqueue(request);
        Log.d(TAG, "Start Download download Id = " + download_id);

    }

    private void parseCSV(String uri) {
        try {
            CSVReader reader = new CSVReader(new FileReader(Uri.parse(uri).getPath()), ',', '"', 1);
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                Log.d(TAG, nextLine[0] + ":" + nextLine[1] + "etc...");
                Cursor c = null;
                try {
                    c = getContentResolver().query(ObserveDataContract.CONTENT_URI,
                            new String[]{ObserveDataContract.KEY_GLOBAL_ID},
                            ObserveDataContract.KEY_GLOBAL_ID + "=" + nextLine[0], null, null);

                    if (c.getCount() == 0) {
                        double latitude = 0.0;
                        double longitude = 0.0;

                        latitude = Double.parseDouble(nextLine[3]);
                        longitude = Double.parseDouble(nextLine[4]);

                        ContentValues values = new ContentValues();
                        values.put(ObserveDataContract.KEY_GLOBAL_ID, nextLine[1]);
                        values.put(ObserveDataContract.KEY_OBSERVE_DATE, nextLine[2]);
                        values.put(ObserveDataContract.KEY_LATITUDE, latitude);
                        values.put(ObserveDataContract.KEY_LONGITUDE, longitude);
                        values.put(ObserveDataContract.KEY_USER_ID, nextLine[5]);
                        values.put(ObserveDataContract.KEY_UPLOADED, 0);
                        values.put(ObserveDataContract.KEY_DATA, nextLine[6]);

                        getContentResolver().insert(ObserveDataContract.CONTENT_URI, values);
                    }
                } finally {
                    if (c != null){
                        c.close();
                    }

                }

            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void uploadCSV() {
        Cursor cursor = getContentResolver().query(ObserveDataContract.CONTENT_URI, null, null, null, null);

        try {
            if (cursor.getCount() == 0) {
                return;
            }
            if (cursor.moveToFirst()) {
                File file = new File(getExternalCacheDir(), "tmp.csv");
                FileWriter fileWriter = new FileWriter(file);
                CSVWriter writer = new CSVWriter(fileWriter);
                do {
                    String global_id = cursor.getString((ObserveDataContract.FIELD_ORDER.GLOBAL_ID.ordinal()));
                    String observe_date = cursor.getString((ObserveDataContract.FIELD_ORDER.OBSERVE_DATE.ordinal()));
                    String latitude = Double.toString(cursor.getDouble(ObserveDataContract.FIELD_ORDER.LATITUDE.ordinal()));
                    String longitude = Double.toString(cursor.getDouble(ObserveDataContract.FIELD_ORDER.LONGITUDE.ordinal()));
                    String userId = cursor.getString((ObserveDataContract.FIELD_ORDER.USER_ID.ordinal()));
                    String data = cursor.getString((ObserveDataContract.FIELD_ORDER.DATA.ordinal()));
                    String[] entries = {global_id, observe_date, latitude, longitude, userId, data};
                    writer.writeNext(entries);

                } while (cursor.moveToNext());
                try {
                    writer.close();
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }
                new UploadAsyncTask(this).execute(file.getAbsolutePath(), getString(R.string.uploadCSV));
            }
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } finally {
            cursor.close();
        }

    }


    public class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Cursor c = null;
            try {
                if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    Log.d(TAG, "Download Complete ID : " + id);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(download_id);
                    c = downloadManager.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c
                                .getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c
                                .getInt(columnIndex)) {
                            String uriString = c
                                    .getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            parseCSV(uriString);
                            uploadCSV();
                        } else {

                        }
                    }
                } else {
                    Log.d(TAG, intent.getAction());
                }
            } finally {
                download_id = -1;
                if (c != null) {
                    c.close();
                }
            }


        }
    }


}



