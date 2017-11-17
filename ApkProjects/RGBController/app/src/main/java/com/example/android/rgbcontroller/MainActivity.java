package com.example.android.rgbcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    private static ConnectedThread mmThread;


    private Button buttonEnable;
    private Button buttonList;
    private Button ledControl;

    private SeekBar redBar;
    private SeekBar greenBar;
    private SeekBar blueBar;

    private TextView redBarValue;
    private TextView greenBarValue;
    private TextView blueBarValue;

    private int red_value = 0;
    private int green_value = 0;
    private int blue_value = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        redBarValue = findViewById(R.id.redBarValue);
        greenBarValue = findViewById(R.id.greenBarValue);
        blueBarValue = findViewById(R.id.blueBarValue);

        redBar = findViewById(R.id.redBar);
        greenBar = findViewById(R.id.greenBar);
        blueBar = findViewById(R.id.blueBar);

        buttonEnable = findViewById(R.id.enableDisableButton);
        buttonList = findViewById(R.id.listButton);
        ledControl = findViewById(R.id.onOff);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Your device does not support Bluetooth.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                buttonEnable.setText("Enable BT");
                buttonList.setClickable(false);
                ledControl.setClickable(false);
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
            ledControl.setClickable(false);
            Toast.makeText(this, "No Bluetooth connection!", Toast.LENGTH_LONG).show();
        }
        status_msg();
    }


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
                        ledControl.setText("OFF");
                        ledControl.setClickable(false);
                        mmDevice = null;
                        status_msg();
                        if (mmThread != null) {
                            mmThread.cancel();
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        buttonList.setClickable(true);
                        buttonEnable.setText("Disable BT");
                        status_msg();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }

        }

    };

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
        Intent intent = new Intent(MainActivity.this, PairedDevices.class);
        startActivity(intent);
    }

    public void ledStateControl(View view) {
        if (red_value > 0 || green_value > 0 || blue_value > 0) {
            ledControl.setText("ON");
            red_value = 0;
            green_value = 0;
            blue_value = 0;

        } else {
            ledControl.setText("OFF");
            red_value = 1;
            green_value = 1;
            blue_value = 1;
        }
        redBar.setProgress(red_value);
        greenBar.setProgress(green_value);
        blueBar.setProgress(blue_value);
        redBarValue.setText(Integer.toString(red_value));
        greenBarValue.setText(Integer.toString(green_value));
        blueBarValue.setText(Integer.toString(blue_value));
        updateValue();

    }

    public static void setDevice(BluetoothDevice device) {
        mmDevice = device;
    }

    public static void setSocket(BluetoothSocket socket) {
        mmSocket = socket;
    }

    public static void setThread(ConnectedThread thread) {
        mmThread = thread;
    }

    private void status_msg() {
        ImageView dot = findViewById(R.id.dot);
        TextView status = findViewById(R.id.status);
        if (mmDevice != null) {
            if (mmDevice.getBondState() == 12) {
                //  Toast.makeText(this, "Connected to: " + mmDevice.getName(), Toast.LENGTH_LONG).show();
                dot.setImageResource(R.drawable.green_dot);
                status.setText("Connected to: " + mmDevice.getName());
                enableBars();

            }
        } else {
            //   Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show();
            dot.setImageResource(R.drawable.red_dot);
            status.setText("Disconnected");
            disableBars();

        }
    }

    private void redBarChange() {

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
                greenBarValue.setText((Integer.toString(green_value)));
                updateValue();
            }
        });
    }

    private void blueBarChange() {


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
        if (red_value == 0 && green_value == 0 && blue_value == 0) {
            ledControl.setText("ON");
        } else {
            ledControl.setText("OFF");
        }
        String string = Integer.toString(red_value) + "." + Integer.toString(green_value) + "." + Integer.toString(blue_value) + ")";
        mmThread.write(string.getBytes());
    }

    private void updateColorDot() {
        TextView colorDot = findViewById(R.id.color_dot);
        colorDot.setTextColor(Color.rgb(red_value, green_value, blue_value));
    }

    private void enableBars() {
        redBar.setEnabled(true);
        greenBar.setEnabled(true);
        blueBar.setEnabled(true);
    }

    private void disableBars() {
        redBar.setEnabled(false);
        greenBar.setEnabled(false);
        blueBar.setEnabled(false);
    }

    public void exit(View view) {
        mBluetoothAdapter.disable();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}