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
        layout1.setPadding(dpAsPixels,dpAsPixels,dpAsPixels,dpAsPixels);
        layout.addView(layout1);
        if (numberOfMotors > 0) {
            for (int i = 1; i <= numberOfMotors; i++) {
                final LinearLayout motorAllLayout = new LinearLayout(Motor.this);
                final LinearLayout nameAndValueLayout = new LinearLayout(Motor.this);
                final LinearLayout.LayoutParams mViewParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2);
                final LinearLayout.LayoutParams mValueParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
                final LinearLayout.LayoutParams barParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                final SeekBar mBar = new SeekBar(this);
                final TextView mView = new TextView(this);
                final TextView mValue = new TextView(this);
                nameAndValueLayout.setLayoutParams(barParam);
                motorAllLayout.setLayoutParams(barParam);
                motorAllLayout.setOrientation(LinearLayout.VERTICAL);
                nameAndValueLayout.setOrientation(LinearLayout.HORIZONTAL);
                mView.setLayoutParams(mViewParam);
                mValue.setLayoutParams(mValueParam);
                mView.setText("Motor: " + i);
                mValue.setText("Value: " + mBar.getProgress());
                mView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                mView.setTextColor(Color.BLACK);
                mValue.setTextColor(Color.BLACK);
                mBar.setMax(100);
                mBar.setProgress(50);
                mValue.setText("Value: " + mBar.getProgress());
                mBar.setId(i);
                mBar.setLayoutParams(barParam);
                mBar.setClickable(true);
                nameAndValueLayout.addView(mView);
                nameAndValueLayout.addView(mValue);
                motorAllLayout.addView(mBar);
                layout1.addView(nameAndValueLayout);
                layout1.addView(motorAllLayout);

                mBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        mValue.setText("Value: " + progress);
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
