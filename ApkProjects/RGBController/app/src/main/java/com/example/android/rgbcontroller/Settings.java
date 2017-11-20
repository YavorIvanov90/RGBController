package com.example.android.rgbcontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * Created by knock on 20-Nov-17.
 */

public class Settings extends AppCompatActivity {

    private Switch aSwitch;
    private SharedPreferences sharedPref;
    private boolean switch_state;
    private String deviceNameSaved;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);


        aSwitch = findViewById(R.id.switch1);
        sharedPref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        if (sharedPref != null) {
            switch_state = sharedPref.getBoolean("Switch", switch_state);
            deviceNameSaved = sharedPref.getString("Name", deviceNameSaved);
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
        } else if (!deviceNameSaved.isEmpty()){
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
        }
        else {
            aSwitch.setClickable(false);
        }

    }

    public SharedPreferences createPref() {
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        return sharedPref;
    }
}