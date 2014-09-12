package jp.co.and_ex.squid2.observe;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import jp.co.and_ex.squid2.R;
import jp.co.and_ex.squid2.util.BaseFragment;

/**
 * Created by obata on 2014/09/05.
 */
public class ObserveViewFragment extends BaseFragment {
    private BluetoothAdapter mBluetoothAdapter = null;
    private static final int REQUEST_CONNECT_SQUID = 1;
    private static final int REQUEST_RECEIVE_SQUID = 2;

    @Override
  public View onCreateView(
    LayoutInflater inflater,
    ViewGroup container,
    Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_observe, container, false);
        Button button1 = (Button)view.findViewById(R.id.button_connect);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                connect();
            }

        });

        Button button2 = (Button)view.findViewById(R.id.button_receive);
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                receive();
            }

        });
    return view;
  }

   private void connect(){
       Toast.makeText(getActivity(), "接続", Toast.LENGTH_LONG).show();
       Log.d(getString(R.string.button_connect),"接続");
       if (mBluetoothAdapter == null) {
           Toast.makeText(getActivity(), R.string.not_bluetooth_available, Toast.LENGTH_LONG).show();
           Log.e(getString(R.string.button_connect),getString(R.string.not_bluetooth_available));
           return;
       }

        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(getString(R.string.button_connect),"Buletooth disable");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_CONNECT_SQUID);
        } else {
            connectExec();
        }
   }

   private void connectExec(){

   }

    private void receive(){
        Toast.makeText(getActivity(), "受信", Toast.LENGTH_LONG).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      switch (requestCode) {
          case REQUEST_CONNECT_SQUID:
          if (resultCode == Activity.RESULT_OK) {
              connectExec();
          } else {
              // User did not enable Bluetooth or an error occured
              Log.d(getString(R.string.button_connect), "BT not enabled");
              Toast.makeText(getActivity(), R.string.not_bluetooth_enable, Toast.LENGTH_SHORT).show();
              return;
          }
        }
    }
}
