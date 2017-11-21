package com.example.android.rgbcontroller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;

/**
 * Created by knock on 20-Nov-17.
 */

public class Settings extends AppCompatActivity {

    private Switch aSwitch;
    private SharedPreferences sharedPref;
    private boolean switch_state;
    private String deviceNameSaved;
    private Intent intent;

    private int numberOfRelays;
    private NumberPicker numberOfRelaysPicker;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);


        aSwitch = findViewById(R.id.switch1);
        numberOfRelaysPicker = (NumberPicker) findViewById(R.id.number_of_relays);

        numberOfRelaysPicker.setMinValue(1);
        numberOfRelaysPicker.setMaxValue(10);
        numberOfRelaysPicker.setWrapSelectorWheel(false);

        numberOfRelaysPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("Relays",i1);
                editor.commit();
            }
        });


        sharedPref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        if (sharedPref != null) {
            switch_state = sharedPref.getBoolean("Switch", switch_state);
            deviceNameSaved = sharedPref.getString("Name", deviceNameSaved);
            numberOfRelays = sharedPref.getInt("Relays", 0);
            aSwitch.setChecked(switch_state);
        }

        if (MainActivity.getDevice() != null) {
            aSwitch.setClickable(true);
            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("Switch", aSwitch.isChecked());
                    editor.putString("Name", MainActivity.getDevice().getName());
                    editor.commit();
                }
            });
        } else if (deviceNameSaved != null) {
            aSwitch.setClickable(true);
            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("Switch", aSwitch.isChecked());
                    editor.putString("Name", deviceNameSaved);
                    editor.commit();
                }
            });
        } else {
            aSwitch.setClickable(false);
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
                break;
            }
            case R.id.item_2: {
                intent = new Intent(Settings.this, RGBControl.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.item_3: {
                intent = new Intent(Settings.this, Relays.class);
                startActivity(intent);
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);

    }
}