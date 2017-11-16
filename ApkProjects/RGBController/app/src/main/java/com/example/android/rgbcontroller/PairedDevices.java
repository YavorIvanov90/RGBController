package com.example.android.rgbcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by knock on 14-Nov-17.
 */

public class PairedDevices extends AppCompatActivity {
    public static BluetoothAdapter mBluetoothAdapter = MainActivity.mBluetoothAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paired_devices);

        final Set<BluetoothDevice> pairedDevices = MainActivity.mBluetoothAdapter.getBondedDevices();
        //    LinearLayout layout = (LinearLayout)findViewById(R.layout.paired_devices);

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            LinearLayout layout = (LinearLayout) findViewById(R.id.paired_devices);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


            for (final BluetoothDevice device : pairedDevices) {
                final String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                for (int i = 1; i < pairedDevices.size(); i++) {
                    Button view = new Button(this);
                    view.setText(deviceName + "  " + deviceHardwareAddress);
                    view.setPadding(4, 4, 4, 4);
                    view.setTextSize(16);
                    view.setLayoutParams(param);
                    view.setId(i);
                    layout.addView(view);
                    view.setClickable(true);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBluetoothAdapter.cancelDiscovery();

                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                device.createBond();
                                ConnectThread thread = new ConnectThread(device);
                                thread.start();
                                Toast.makeText(PairedDevices.this, "Connecting to: " + deviceName, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final ConnectedThread mmThread;

        private final String TAG = "PairedDevices";
        private final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
            mmThread = new ConnectedThread(mmSocket);
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            Intent intent = new Intent(PairedDevices.this, MainActivity.class);

            MainActivity.setDevice(mmDevice);
            MainActivity.setSocket(mmSocket);
            MainActivity.setThread(mmThread);


            startActivity(intent);
            finish();
            return;
            //  manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }


}
