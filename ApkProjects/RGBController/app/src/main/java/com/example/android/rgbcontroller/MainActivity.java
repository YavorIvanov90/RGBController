package com.example.android.rgbcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    public static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static BluetoothDevice mmDevice;
    private static BluetoothSocket mmSocket;
    public Button buttonEnable;

    public SeekBar redBar;
    public SeekBar greenBar;
    public SeekBar blueBar;

    public int red_value = 0;
    public int green_value = 0;
    public int blue_value = 0;

    private Button buttonList;

    Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        redBar = findViewById(R.id.redBar);
        greenBar = findViewById(R.id.greenBar);
        blueBar = findViewById(R.id.blueBar);
        buttonEnable = findViewById(R.id.enableDisableButton);

        buttonList = findViewById(R.id.listButton);

        // START of loop
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Your device does not support Bluetooth.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                buttonEnable.setText("Enable BT");
                buttonList.setClickable(false);
                mmDevice = null;
            } else {
                buttonEnable.setText("Disable BT");
                buttonList.setClickable(true);
            }
        }
        if (mmDevice != null) {
            if (mmDevice.getBondState() == 12) {
                redBarChange();
                greenBarChange();
                blueBarChange();
            }
        } else {
            Toast.makeText(this, "No Bluetooth connection!", Toast.LENGTH_LONG).show();
        }
        status_msg();
        // END of loop
    }
/*
    @Override
    protected void onResume() {
        TextView dot = findViewById(R.id.dot);
        TextView status = findViewById(R.id.status);

        super.onResume();

        if (!mBluetoothAdapter.isEnabled()) {
            buttonEnable.setText("Enable BT");
            dot.setTextColor(Color.RED);
            buttonList.setClickable(false);
            status.setText("Disconnected");
        }
    }
*/
    public void btEnable(View view) {
        final int REQUEST_ENABLE_BT = 1;

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            buttonList.setClickable(true);
            buttonEnable.setText("Disable BT");
        } else {
            mBluetoothAdapter.disable();
            buttonEnable.setText("Enable BT");
            buttonList.setClickable(false);
            mmDevice=null;
        }
        status_msg();
    }

    public void btList(View view) {
        Intent intent = new Intent(MainActivity.this, PairedDevices.class);
        startActivity(intent);
    }

    public static void setDevice(BluetoothDevice device) {
        mmDevice = device;
    }

    public static void setSocket(BluetoothSocket socket) {
        mmSocket = socket;
    }

    private void status_msg() {
        TextView dot = findViewById(R.id.dot);
        TextView status = findViewById(R.id.status);
        if (mmDevice != null) {
            if (mmDevice.getBondState() == 12) {
                Toast.makeText(this, "Connected to: " + mmDevice.getName(), Toast.LENGTH_LONG).show();
                dot.setTextColor(Color.GREEN);
                status.setText("Connected");

            }
        } else {
            Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show();
            dot.setTextColor(Color.RED);
            status.setText("Disconnected");

        }
    }

    private void redBarChange() {
        final TextView redBarValue = findViewById(R.id.redBarValue);
        redBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                red_value = seekBar.getProgress();
                updateColorDot();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // red_value = seekBar.getProgress();
                Log.d("OnProgressChanges", Integer.toString(red_value));
                redBarValue.setText(Integer.toString(red_value));
                updateValue();
            }
        });
    }

    private void greenBarChange() {

        final TextView greenBarValue = findViewById(R.id.greenBarValue);

        greenBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                green_value = seekBar.getProgress();
                updateColorDot();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // green_value = seekBar.getProgress();
                Log.d("OnProgressChanges", Integer.toString(green_value));
                greenBarValue.setText(Integer.toString(green_value));
                updateValue();
            }
        });
    }

    private void blueBarChange() {
        final TextView blueBarValue = findViewById(R.id.blueBarValue);

        blueBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                blue_value = seekBar.getProgress();
                updateColorDot();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // blue_value = seekBar.getProgress();
                Log.d("OnProgressChanges", Integer.toString(blue_value));
                blueBarValue.setText(Integer.toString(blue_value));
                updateValue();
            }
        });
    }

    private void updateValue() {
        ConnectedThread thread = new ConnectedThread(mmSocket);

        String string = Integer.toString(red_value) + "." + Integer.toString(green_value) + "." + Integer.toString(blue_value) + ")";

        thread.write(string.getBytes());
    }

    private void updateColorDot() {
        TextView colorDot = findViewById(R.id.color_dot);
        colorDot.setTextColor(Color.rgb(red_value, green_value, blue_value));
    }

    public void exit(View view) {
        mBluetoothAdapter.disable();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
    //  finish();
    //  System.exit(0);
}