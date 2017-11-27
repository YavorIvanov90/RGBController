package com.example.android.rgbcontroller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.jar.Attributes;

/**
 * Created by knock on 20-Nov-17.
 */

public class Relays extends AppCompatActivity {

    private Intent intent;
    private SharedPreferences sharedPref;// = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
    ;
    private LinearLayout layout;
    private int numberOfRelays;
    private boolean save_state;
    // private final SharedPreferences.Editor editor= sharedPref.edit();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relays);

        sharedPref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        // save_state = sharedPref.getBoolean("Switch2", false);
        numberOfRelays = sharedPref.getInt("Relays", numberOfRelays);
        save_state = sharedPref.getBoolean("Switch2", false);


        layout = findViewById(R.id.relays_layout);
        final LinearLayout layout1 = new LinearLayout(Relays.this);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout1.setLayoutParams(param);
        layout1.setOrientation(LinearLayout.VERTICAL);
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (10 * scale + 0.5f);
        layout.addView(layout1);
        if (numberOfRelays > 0) {
            for (int i = 1; i <= numberOfRelays; i++) {
                final LinearLayout layout2 = new LinearLayout(Relays.this);
                LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2);
                LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2);
                layout2.setLayoutParams(param);
                layout2.setOrientation(LinearLayout.HORIZONTAL);
                final Switch mSwitch = new Switch(this);
                final TextView mView = new TextView(this);
                layout2.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                mView.setLayoutParams(param2);
                mView.setText("Relay: " + i);
                mView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                mView.setTextColor(Color.BLACK);
                mSwitch.setId(i);
                mSwitch.setLayoutParams(param3);
                mSwitch.setClickable(true);
                layout2.addView(mView);
                layout2.addView(mSwitch);
                layout1.addView(layout2);
                if (save_state) {
                    mSwitch.setChecked(sharedPref.getBoolean(mView.getText().toString(), false));
                    sendData(mSwitch.getId(), sharedPref.getBoolean(mView.getText().toString(), false));
                } else {
                    mSwitch.setChecked(false);
                }
                mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        boolean status = compoundButton.isChecked();
                        if (save_state) {
                            editor.putBoolean(mView.getText().toString(), compoundButton.isChecked());
                            editor.commit();
                        }
                        sendData(mSwitch.getId(), status);
                    }
                });
            }
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
            case R.id.item_0:{
                intent = new Intent(Relays.this,MainActivity.class);
              //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
            case R.id.item_1: {
                intent = new Intent(Relays.this, Settings.class);
             //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
            case R.id.item_2: {
                intent = new Intent(Relays.this, RGBControl.class);
            //    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
            case R.id.item_3: {
                break;
            }
            case R.id.item_4: {
                intent = new Intent(Relays.this, Motor.class);
                //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);

    }

    private void sendData(int id, boolean status) {
        String value = Integer.toString(id);
        value = "Relay:" + value + ":" + status + '\n';
        if(MainActivity.getThread() != null){
            MainActivity.getThread().write(value.getBytes());
        } else {
            Switch aSwitch = findViewById(id);
            aSwitch.setChecked(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        save_state = sharedPref.getBoolean("Switch2", false);
        numberOfRelays = sharedPref.getInt("Relays", numberOfRelays);
    }
}
