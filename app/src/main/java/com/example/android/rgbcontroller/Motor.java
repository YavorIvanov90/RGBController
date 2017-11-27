package com.example.android.rgbcontroller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by yavor on 11/27/17.
 */

public class Motor extends AppCompatActivity {

    private SharedPreferences sharedPref;// = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
    ;
    private LinearLayout layout;
    private int numberOfMotors;
    private boolean save_state;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.motor);

        sharedPref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();


        numberOfMotors = sharedPref.getInt("Motors", numberOfMotors);
        save_state = sharedPref.getBoolean("Switch3", false);


        layout = findViewById(R.id.motor_layout);
        final LinearLayout layout1 = new LinearLayout(Motor.this);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout1.setLayoutParams(param);
        layout1.setOrientation(LinearLayout.VERTICAL);
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (10 * scale + 0.5f);
        layout.addView(layout1);
        if (numberOfMotors > 0) {
            for (int i = 1; i <= numberOfMotors; i++) {
                final LinearLayout layout2 = new LinearLayout(Motor.this);
                LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2);
                LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2);
                layout2.setLayoutParams(param);
                layout2.setOrientation(LinearLayout.VERTICAL);
                final SeekBar mBar = new SeekBar(this);
                final TextView mView = new TextView(this);
                layout2.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                mView.setLayoutParams(param2);
                mView.setText("Motor: " + i);
                mView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                mView.setTextColor(Color.BLACK);
                mBar.setMax(100);
                mBar.setProgress(50);
                mBar.setId(i);
                mBar.setLayoutParams(param3);
                mBar.setClickable(true);
                layout2.addView(mView);
                layout2.addView(mBar);
                layout1.addView(layout2);

                mBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        sendData(seekBar.getId(), progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

            }
        }

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
                intent = new Intent(Motor.this, Settings.class);
                //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
            case R.id.item_2: {
                intent = new Intent(Motor.this, RGBControl.class);
                //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
            case R.id.item_3: {
                intent = new Intent(Motor.this, Relays.class);
                //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
            case R.id.item_4: {
                intent = new Intent(Motor.this, Motor.class);
                //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);

    }

    private void sendData(int id, int status) {
        String value = Integer.toString(id);
        value = "Motor:" + value + ":" + status + '\n';
        if (MainActivity.getThread() != null) {
            MainActivity.getThread().write(value.getBytes());
        }
    }
}
