package com.example.android.rgbcontroller;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by knock on 20-Nov-17.
 */

public class RGBControl extends AppCompatActivity {

    private static int red_value = 0;
    private static int green_value = 0;
    private static int blue_value = 0;
    private Button ledControl;
    private Button buttonEnterHexColor;
    private SeekBar redBar;
    private SeekBar greenBar;
    private SeekBar blueBar;
    private TextView redBarValue;
    private TextView greenBarValue;
    private TextView blueBarValue;
    private EditText hexColor;
    private Intent intent;
    private String valueToSend;

    private static BluetoothDevice mmDevice;
    private static BluetoothSocket mmSocket;
    private static ConnectedThread mmThread;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rgb_control);


        redBarValue = findViewById(R.id.redBarValue);
        greenBarValue = findViewById(R.id.greenBarValue);
        blueBarValue = findViewById(R.id.blueBarValue);


        redBar = findViewById(R.id.redBar);
        greenBar = findViewById(R.id.greenBar);
        blueBar = findViewById(R.id.blueBar);

        ledControl = findViewById(R.id.onOff);
        buttonEnterHexColor = findViewById(R.id.hexColorButton);

        hexColor = findViewById(R.id.hexColor);


        if (MainActivity.getDevice() != null) {
            mmDevice = MainActivity.getDevice();
            if (mmDevice.getBondState() == 12) {
                mmThread = MainActivity.getThread();
                enableBars();
                ledControl.setEnabled(true);
                buttonEnterHexColor.setEnabled(true);
                hexColor.setEnabled(true);
                redBarChange();
                greenBarChange();
                blueBarChange();
                updateValue();
            }
        } else {
            disableBars();
            ledControl.setEnabled(false);
            buttonEnterHexColor.setEnabled(false);
            hexColor.setEnabled(false);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_1: {
                intent = new Intent(RGBControl.this, Settings.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.item_2: {
                break;
            }
            case R.id.item_3: {
                intent = new Intent(RGBControl.this, Relays.class);
                startActivity(intent);
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);

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
        updateValue();

    }

    public void enterHexColor(View view) {
        String hexColorString = hexColor.getText().toString();


        if (!hexColorString.isEmpty()) {
            Long color = Long.parseLong(hexColorString, 16);

            red_value = (int) (color >> 16) & 0xFF;
            green_value = (int) (color >> 8) & 0xFF;
            blue_value = (int) (color & 0xFF);
            updateValue();
        } else {
            Toast.makeText(this, "Please enter a valid value", Toast.LENGTH_LONG).show();
        }
    }

    private void redBarChange() {
        redBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                red_value = seekBar.getProgress();
                valueToSend = Integer.toString(red_value) + "R!"+ '\n';
                Log.d("OnProgressChanges", Integer.toString(red_value));
                updateValue();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // red_value = seekBar.getProgress();
            }
        });
    }

    private void greenBarChange() {
        greenBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                green_value = seekBar.getProgress();
                valueToSend = Integer.toString(green_value) + "G!"+ '\n';
                //Log.i("OnProgressChanges", Integer.toString(green_value));
                greenBarValue.setText((Integer.toString(green_value)));
                updateValue();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // green_value = seekBar.getProgress();
            }
        });
    }

    private void blueBarChange() {
        blueBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                blue_value = seekBar.getProgress();
                valueToSend = Integer.toString(blue_value) + "B!"+ '\n';
              //  Log.i("OnProgressChanges", Integer.toString(blue_value));
                blueBarValue.setText(Integer.toString(blue_value));
                updateValue();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // blue_value = seekBar.getProgress();
            }
        });
    }

    private void updateValue() {
        String string;
        if (red_value == 0 && green_value == 0 && blue_value == 0) {
            ledControl.setText("ON");
        } else {
            ledControl.setText("OFF");
        }
        redBar.setProgress(red_value);
        greenBar.setProgress(green_value);
        blueBar.setProgress(blue_value);
        redBarValue.setText(Integer.toString(red_value));
        greenBarValue.setText(Integer.toString(green_value));
        blueBarValue.setText(Integer.toString(blue_value));

        string = Integer.toString(red_value) + "." + Integer.toString(green_value) + "." + Integer.toString(blue_value) + ")";
        // mmThread.write(string.getBytes());
        if (valueToSend!=null) {
            mmThread.write(valueToSend.getBytes());
            Log.i("RGBController",valueToSend);
            updateColorDot();
        }
    }

    private void updateColorDot() {
        View colorDot = findViewById(R.id.color_dot);
        colorDot.setBackgroundColor(Color.rgb(red_value, green_value, blue_value));
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
}
