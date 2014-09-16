package jp.co.and_ex.squid2.observe;



import android.app.DialogFragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.EventListener;
import java.util.Set;
import java.util.regex.Pattern;

import jp.co.and_ex.squid2.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceListFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class DeviceListFragment extends DialogFragment {

    private static final String TAG = DeviceListFragment.class.getSimpleName();

    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
      * @return A new instance of fragment DeviceListFragment.
     */
    public static DeviceListFragment newInstance() {
        DeviceListFragment fragment = new DeviceListFragment();
        return fragment;
    }
    public DeviceListFragment() {
        // Required empty public constructor
    }

    DeviceListListener deviceListListener = null;

    public static final void show(FragmentManager manager,DeviceListListener listener) {

        DeviceListFragment dialog = DeviceListFragment.newInstance();
        dialog.deviceListListener = listener;
        dialog.show(manager, TAG);
    }

    public static final void hide(FragmentManager manager) {

        Fragment temp = manager.findFragmentByTag(TAG);
        if (temp instanceof DeviceListFragment) {
            DeviceListFragment dialog = (DeviceListFragment) temp;
            dialog.deviceListListener = null;
            dialog.dismiss();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_device_list, container, false);

        mListView = (ListView)view.findViewById(R.id.deviceListView);

        mBluetoothAdapter = mBluetoothAdapter.getDefaultAdapter();
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1);

        if(mBluetoothAdapter != null) {
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    Log.d("DeviceListView",device.getName());
                    Log.d("DeviceListView",device.getAddress());
                    mPairedDevicesArrayAdapter.add(device.getName() + ";" + device.getAddress());
                }
            } else {
                String noDevices = getResources().getText(R.string.none_paired).toString();
                mPairedDevicesArrayAdapter.add(noDevices);
            }
        }else{
            Toast.makeText(getActivity(), R.string.not_bluetooth_available, Toast.LENGTH_LONG).show();
            Log.e(getString(R.string.button_connect), getString(R.string.not_bluetooth_available));
        }
        mListView.setAdapter(mPairedDevicesArrayAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = mPairedDevicesArrayAdapter.getItem(position);
                Log.d("DeviceListView","item clicked :" + Integer.toString(position));
                Log.d("DeviceListView","item clicked :" + item);
                if (item != null && item.length() > 0){
                    String[] s = item.split(Pattern.quote(";"));
                    if (s != null && s.length == 2){
                        String address = s[1];
                        Log.d("DeviceListView Selected address", address);
                        deviceListListener.listSelected(address);
                    }
                }
            }
        });

        Button button = (Button)view.findViewById(R.id.closeDeviceButton);
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DeviceListView","clicked ");
                deviceListListener.closeWindow();
            }
        });
        return view;
    }

      public interface DeviceListListener  extends  EventListener{
        public void closeWindow();
        public void listSelected(String address);
    }
}

