package com.example.android.rgbcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static BluetoothDevice mmDevice;
    private static BluetoothSocket mmSocket;
    private static ConnectedThread mmThread;

    private Button buttonEnable;
    private Button buttonList;
    private Button ledControl;
    private Button buttonDisconect;

    private static boolean switch_state;
    private static boolean connected;
    private SharedPreferences sharedPref;
    String deviceNameSaved;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        buttonList.setClickable(false);
                        buttonEnable.setText("Enable BT");

                        mmDevice = null;
                        if (mmThread != null) {
                            mmThread.cancel();
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        buttonList.setClickable(true);
                        buttonEnable.setText("Disable BT");

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
                status_msg();
            }
        }
    };

    public static void setDevice(BluetoothDevice device) {
        mmDevice = device;
    }

    public static void setSocket(BluetoothSocket socket) {
        mmSocket = socket;
    }

    public static void setThread(ConnectedThread thread) {
        mmThread = thread;
    }

    public static void setConnected(boolean connect) {
        connected = connect;
    }

    public static BluetoothDevice getDevice() {
        return mmDevice;
    }

    public static BluetoothSocket getSocket() {
        return mmSocket;
    }

    public static ConnectedThread getThread() {
        return mmThread;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonEnable = findViewById(R.id.enableDisableButton);
        buttonList = findViewById(R.id.listButton);
        buttonDisconect = findViewById(R.id.disconnectButton);


        sharedPref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Your device does not support Bluetooth.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                buttonEnable.setText("Enable BT");
                buttonList.setClickable(false);
                buttonDisconect.setClickable(false);
                mmDevice = null;
            } else {
                buttonEnable.setText("Disable BT");
                buttonList.setClickable(true);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPref != null) {
            switch_state = sharedPref.getBoolean("Switch", switch_state);
            deviceNameSaved = sharedPref.getString("Name", deviceNameSaved);
        }
        if (mmDevice != null) {
            if (mmDevice.getBondState() == 12) {
                buttonDisconect.setClickable(true);
            }
        } else {
            if (switch_state) {
                autoConnect();
            } else {
                Toast.makeText(this, "No Bluetooth connection!", Toast.LENGTH_LONG).show();
            }
        }
        status_msg();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.item_0: {
                break;
            }
            case R.id.item_1: {
                intent = new Intent(MainActivity.this, Settings.class);
                //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
            case R.id.item_2: {
                intent = new Intent(MainActivity.this, RGBControl.class);
                //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
            case R.id.item_3: {
                intent = new Intent(MainActivity.this, Relays.class);
                //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
            case R.id.item_4: {
                intent = new Intent(MainActivity.this, Motor.class);
                //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);

    }

    public void btEnable(View view) {
        final int REQUEST_ENABLE_BT = 1;
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            mBluetoothAdapter.disable();
        }
    }

    public void btList(View view) {
        Intent intent;
        intent = new Intent(MainActivity.this, PairedDevices.class);
        Bundle a = new Bundle();
        startActivity(intent);
        // finish();
    }

    public void disconnect(View view) {
        if (mmSocket != null) {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("Main Activity", e.toString());
            }
        }
    }

    private void status_msg() {
        ImageView dot = findViewById(R.id.dot);
        TextView status = findViewById(R.id.status);
        if (mmDevice != null) {
            if (mmDevice.getBondState() == 12) {
                Toast.makeText(this, "Connected to: " + mmDevice.getName(), Toast.LENGTH_LONG).show();
                dot.setImageResource(R.drawable.green_dot);
                status.setText("Connected to: " + mmDevice.getName());
            }
        } else {
            Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show();
            dot.setImageResource(R.drawable.red_dot);
            status.setText("Disconnected");
        }
    }

    private void autoConnect() {
        final Set<BluetoothDevice> pairedDevices = MainActivity.mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {

            for (final BluetoothDevice device : pairedDevices) {
                //   final String deviceName = device.getName();
                for (int i = 1; i < pairedDevices.size(); i++) {
                    if (device.getName().equals(deviceNameSaved)) {
                        ConnectThread new_thread = new ConnectThread(device);
                        new_thread.run();
                        if (mmDevice != null) {
                            this.recreate();
                        }

                    }
                }
            }
        }
    }

    public void exit(View view) {

        try {
            mmSocket.close();
        } catch (IOException e) {

        }
        System.exit(1);
        mBluetoothAdapter.disable();
      /*  moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
       */
    }
}