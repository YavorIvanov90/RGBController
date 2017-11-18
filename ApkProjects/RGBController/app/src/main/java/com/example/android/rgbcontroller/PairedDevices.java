package com.example.android.rgbcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.UUID;

/**
 * Created by knock on 14-Nov-17.
 */

public class PairedDevices extends AppCompatActivity {
    public static BluetoothAdapter mBluetoothAdapter = MainActivity.mBluetoothAdapter;
    public static Context context;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paired_devices);
        context = this;

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
                                ConnectThread thread = new ConnectThread(device,PairedDevices.this);
                                thread.start();
                                Toast.makeText(PairedDevices.this, "Connecting to: " + deviceName, Toast.LENGTH_SHORT).show();


                            }

                        }
                    });
                }
            }
        }
    }

    public  void returnToMain() {
        if (MainActivity.getDevice() != null) {
            Intent intent = new Intent(PairedDevices.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}


