package jp.co.and_ex.squid2.observe;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import jp.co.and_ex.squid2.MainActivity;
import jp.co.and_ex.squid2.R;
import jp.co.and_ex.squid2.db.ObserveDataContract;
import jp.co.and_ex.squid2.util.BaseFragment;

/**
 * Created by obata on 2014/09/05.
 */
public class ObserveViewFragment extends BaseFragment implements SquidReader {
    private final String TAG = ObserveViewFragment.class.getSimpleName();

    private BluetoothAdapter mBluetoothAdapter = null;


    private enum State {REQUEST_NONE, REQUEST_START_SQUID, REQUEST_RECEIVE_SQUID}

    private State myState = State.REQUEST_NONE;

    private ObserveViewListener observeViewListener;

    private String squidAddress = null;

    private BluetoothService mBlueToothService = null;

    private String messageQueue = null;
    private StringBuffer dataBuffer = null;
    private SquidParser squidParser = null;


    private static final String GET_COMMAND = "@GET\r\n";
    private static final String SET_COMMAND = "@SET\r\n";

    private Timer timeOut = null;

    private int dataCount;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_observe, container, false);
        Button button1 = (Button) view.findViewById(R.id.button_connect);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deviceStart();
            }

        });

        Button button2 = (Button) view.findViewById(R.id.button_receive);
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deviceReceive();
            }

        });
        messageQueue = null;
        dataBuffer = new StringBuffer();
        squidParser = new SquidParser(this);


        return view;
    }

    private void deviceStart() {
        Log.d(getString(R.string.button_connect), "観測開始");
        myState = State.REQUEST_START_SQUID;
        communicateSquid();
    }


    private void deviceReceive() {
        myState = State.REQUEST_RECEIVE_SQUID;
        communicateSquid();
    }

    private void communicateSquid() {
        Toast.makeText(getActivity(), "接続", Toast.LENGTH_LONG).show();
        Log.d(getString(R.string.button_connect), "接続");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), R.string.not_bluetooth_available, Toast.LENGTH_LONG).show();
            Log.e(getString(R.string.button_connect), getString(R.string.not_bluetooth_available));
            return;
        }
        if (mBlueToothService == null) {
            mBlueToothService = new BluetoothService(mHandler);
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(getString(R.string.button_connect), "Buletooth disable");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 0);
            return;
        }
        connectStart();
    }


    public void connectStart() {
        Log.v(getString(R.string.button_connect), "接続開始");
        connectTextWrite("観測開始します");
        tellSquid();
        if (mBlueToothService.getState() == BluetoothService.STATE_NONE) {
            observeViewListener.viewDeviceList();
            return;
        }
        if (mBlueToothService.getState() == BluetoothService.STATE_CONNECTED) {
            queuing();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            connectStart();
        } else {
            Log.d(getString(R.string.button_connect), getString(R.string.not_bluetooth_enable));
            Toast.makeText(getActivity(), R.string.not_bluetooth_enable, Toast.LENGTH_SHORT).show();
            return;
        }
    }


    private void titleTextWrite(String s) {
        if (s != null) {
            EditText text = (EditText) getView().findViewById(R.id.TitleText);
            if (text != null) {
                text.setText(s);
            } else {
                Log.e(getString(R.string.button_connect), "接続Viewが見つからない");
            }
        }
    }


    private void connectTextWrite(String s) {
        if (s != null && getView() != null) {
            TextView textView = (TextView) getView().findViewById(R.id.connectView);
            if (textView != null) {
                textView.setText(s);
            } else {
                Log.e(getString(R.string.button_connect), "接続Viewが見つからない");
            }
        }
    }

    private void receiveTextWrite(String s) {
        if (s != null && getView() != null) {
            TextView textView = (TextView) getView().findViewById(R.id.receiveView);
            if (textView != null) {
                textView.setText(s);
            } else {
                Log.e(getString(R.string.button_connect), "接続Viewが見つからない");
            }
        }
    }


    public void setDeviceAddress(String address) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        tellSquid();
        mBlueToothService.connect(device);

    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);        //(2)現在の年を取得
        int month = cal.get(Calendar.MONTH) + 1;  //(3)現在の月を取得
        int day = cal.get(Calendar.DATE);         //(4)現在の日を取得
        int hour = cal.get(Calendar.HOUR_OF_DAY); //(5)現在の時を取得
        int minute = cal.get(Calendar.MINUTE);    //(6)現在の分を取得
        int second = cal.get(Calendar.SECOND);    //(7)現在の秒を取得
        String dateStr = String.format("%04d/%02d/%02d,%02d:%02d:%02d", year, month, day, hour, minute, second);


        titleTextWrite(dateStr);

      ProgressBar progressBar = (ProgressBar)getActivity().findViewById(R.id.observeProgressBar);
        progressBar.setMax(R.integer.progress_max);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            observeViewListener = (ObserveViewListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DeviceListFragment");
        }
        viewProgressBar(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
        observeViewListener = null;
        cancelCommunicate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        observeViewListener = null;
        cancelCommunicate();
    }

    public interface ObserveViewListener {
        void viewDeviceList();
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BlueToothContract.MESSAGE_STATE_CHANGE:
                    Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Log.i(TAG, "connected");
                            queuing();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Log.i(TAG, "connecting");
                            break;
                        case BluetoothService.STATE_NONE:
                            break;
                    }
                    break;
                case BlueToothContract.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.d(TAG, writeMessage);
                    break;
                case BlueToothContract.MESSAGE_READ:
                    Log.d(TAG, "MESSAGE_STATE_READ:");
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d(TAG, readMessage);
                    squidParser.putData(readMessage);
                    break;
                case BlueToothContract.MESSAGE_DEVICE_NAME:
                    Log.d(TAG, "MESSAGE_STATE_DEVICE_NAME:");
                    // save the connected device's name
                    String connectedDeviceName = msg.getData().getString(BlueToothContract.DEVICE_NAME);
                    Toast.makeText(getActivity(), "Connected to " + connectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;

                case BlueToothContract.MESSAGE_TOAST:
                    Log.d(TAG, msg.getData().getString(BlueToothContract.TOAST));
                    Toast.makeText(getActivity(), msg.getData().getString(BlueToothContract.TOAST),
                            Toast.LENGTH_SHORT).show();
                    cancelCommunicate();
                    break;
            }
        }


    };

    @Override
    public void onStop() {
        super.onStop();
        cancelCommunicate();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        cancelCommunicate();
    }

    private void cancelCommunicate() {
        if (mBlueToothService != null) {
            mBlueToothService.stop();
            Log.d(TAG, "BlueTooth Stop");
        } else {
            Log.d(TAG, "Bluetooth Service is null");
        }
        messageQueue = null;

    }

    private final Handler mTimeOutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "TimeOutしました");
            if (myState == State.REQUEST_START_SQUID) {
                connectTextWrite(getString(R.string.time_out));
            } else if (myState == State.REQUEST_RECEIVE_SQUID) {
                receiveTextWrite(getString(R.string.time_out));
            }
            cancelCommunicate();
        }

    };


    public void tellSquid() {
        String str = null;
        if (myState == State.REQUEST_START_SQUID) {

            str = SET_COMMAND;

        } else if (myState == State.REQUEST_RECEIVE_SQUID) {
            str = GET_COMMAND;
            dataBuffer.setLength(0);
        }


        if (str != null) {
            Log.d(TAG, str);
            putQueue(str);
        } else {
            Log.d(TAG, getString(R.string.internal_error));
            Toast.makeText(getActivity(), getString(R.string.internal_error), Toast.LENGTH_SHORT).show();
        }

    }

    private synchronized void putQueue(String str) {
        if (messageQueue != null) {
            Log.d(TAG, "queue is full");
            return;
        }
        timeOut = new Timer(true);
        timeOut.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "Time Out");
                Message msg = Message.obtain();
                mTimeOutHandler.sendMessage(msg);
            }
        }, 180 * 1000);

        messageQueue = str;
    }

    private synchronized void queuing() {
        if (timeOut != null) {
            timeOut.cancel();
            timeOut = null;
        }

        if (messageQueue == null) {
            Log.d(TAG, "queue is empty");
            return;
        }

        try {
            mBlueToothService.write(messageQueue.getBytes("UTF-8"));
            messageQueue = null;
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void tokenGet(String str) {

    }

    @Override
    public void tokenSet(String str) {

    }

    @Override
    public void tokenOK(String str) {
        Log.d(TAG, str);
        if (myState == State.REQUEST_START_SQUID) {
            connectTextWrite("観測開始しました");
        }
        if (timeOut != null) {
            timeOut.cancel();
        }
    }

    @Override
    public void token200(String str) {
        Log.d(TAG, str);
        if (myState == State.REQUEST_RECEIVE_SQUID) {
            Log.d(TAG, dataBuffer.toString());
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            int year = cal.get(Calendar.YEAR);        //(2)現在の年を取得
            int month = cal.get(Calendar.MONTH) + 1;  //(3)現在の月を取得
            int day = cal.get(Calendar.DATE);         //(4)現在の日を取得
            int hour = cal.get(Calendar.HOUR_OF_DAY); //(5)現在の時を取得
            int minute = cal.get(Calendar.MINUTE);    //(6)現在の分を取得
            int second = cal.get(Calendar.SECOND);    //(7)現在の秒を取得
            String dateStr = String.format("%04d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second);

            Double latitude = 0.0;
            Double longitude = 0.0;

            Location location = ((MainActivity) getActivity()).getLocation();
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                Log.d(TAG, "latitude:" + latitude.toString());
                Log.d(TAG, "longitude:" + longitude.toString());
            }

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String userId = pref.getString("user_name", "unknown");

            ContentValues values = new ContentValues();
            values.put(ObserveDataContract.KEY_GLOBAL_ID, userId + System.nanoTime());
            values.put(ObserveDataContract.KEY_OBSERVE_DATE, dateStr);
            values.put(ObserveDataContract.KEY_LATITUDE, latitude);
            values.put(ObserveDataContract.KEY_LONGITUDE, longitude);
            values.put(ObserveDataContract.KEY_USER_ID, userId);
            values.put(ObserveDataContract.KEY_UPLOADED, 0);
            values.put(ObserveDataContract.KEY_DATA, dataBuffer.toString());

            getActivity().getContentResolver().insert(ObserveDataContract.CONTENT_URI, values);
            receiveTextWrite("受信完了しました");
            if (timeOut != null){
                timeOut.cancel();
            };
        }
    }

    @Override
    public void tokenData(String str) {
        if (dataBuffer.length() == 0){
            receiveTextWrite("受信開始しました");
            dataCount = 0;
            viewProgressBar(true);
        }
        dataBuffer.append(str);
        dataCount++;

        ProgressBar progressBar = (ProgressBar)getActivity().findViewById(R.id.observeProgressBar);
        progressBar.setMax(200);

        progressBar.setProgress(dataCount);

        String displayCount = String.format("%1$03d/%2$03d",dataCount,progressBar.getMax());

        TextView textView = (TextView)getActivity().findViewById(R.id.progressCount);
        textView.setText(displayCount);
    }

    private void viewProgressBar(boolean b){
        View view = getActivity().findViewById(R.id.progressContainer);
        if (view != null){
            if (b){
                view.setVisibility(View.VISIBLE);
            }else{
                view.setVisibility(view.INVISIBLE);
            }
        }
    }

    @Override
    public void tokenError(String str) {
        Log.d(TAG, "受信データにエラーが発生しました:" + str);
        Toast.makeText(getActivity(), "受信データにエラーが発生しました", Toast.LENGTH_SHORT).show();
    }


}
