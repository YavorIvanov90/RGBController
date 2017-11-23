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
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
    public boolean connected;

    private RelativeLayout layout;
    private ProgressBar bar;


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paired_devices);

        layout = (RelativeLayout) findViewById(R.id.paired_devices);
        bar = new ProgressBar(PairedDevices.this, null, android.R.attr.progressBarStyleLarge);
        context = this;

        final Set<BluetoothDevice> pairedDevices = MainActivity.mBluetoothAdapter.getBondedDevices();
        //    LinearLayout layout = (LinearLayout)findViewById(R.layout.paired_devices);

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            final LinearLayout bt_layout = new LinearLayout(PairedDevices.this);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            bt_layout.setLayoutParams(param);
            bt_layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(bt_layout);

            int id =1;
            for (final BluetoothDevice device : pairedDevices) {
                final String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
               //for (int i = 1; i < pairedDevices.size(); i++) {
                    Button view = new Button(this);
                    view.setText(deviceName + "  " + deviceHardwareAddress);
                    view.setPadding(4, 4, 4, 4);
                    view.setTextSize(16);
                    view.setLayoutParams(param);
                    view.setId(id);
                    bt_layout.addView(view);
                    view.setClickable(true);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mBluetoothAdapter.cancelDiscovery();
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                createBar();
                                for (int i = 0; i < bt_layout.getChildCount(); i++) {
                                    View child = bt_layout.getChildAt(i);
                                    child.setClickable(false);
                                }
                                ConnectThread thread = new ConnectThread(device, PairedDevices.this);
                                thread.start();
                                Toast.makeText(PairedDevices.this, "Connecting to: " + deviceName, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    id++;
               // }
            }
        }
    }

    public void returnToMain() {
        // if (MainActivity.getDevice() != null) {
        Intent intent = new Intent(PairedDevices.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        //  }
    }

    public void setConnected(boolean connect) {
        connected = connect;
    }

    private void createBar() {

        bar.setIndeterminate(true);
        bar.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        bar.setLayoutParams(params);
        layout.animate().alpha(0.5f);
        layout.addView(bar);
    }

    public void msg_status() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout.removeView(bar);
                layout.animate().alpha(1f);
                if (MainActivity.getDevice() == null) {
                    Toast.makeText(PairedDevices.this, "Unable to connect!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PairedDevices.this, "Already connected!", Toast.LENGTH_SHORT).show();
                }
                LinearLayout bt_layout = (LinearLayout) layout.getChildAt(0);
                for (int i = 0; i < bt_layout.getChildCount(); i++) {
                    View child = bt_layout.getChildAt(i);
                    child.setClickable(true);
                }
            }
        });

    }
}