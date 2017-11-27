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
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by knock on 20-Nov-17.
 */

public class Settings extends AppCompatActivity {

    private SharedPreferences sharedPref;

    private Switch aSwitch1;
    private Switch aSwitch2;
    private Switch aSwitch3;

    private boolean switch1_state = false;
    private boolean switch2_state = false;
    private boolean Switch3_state = false;

    private String deviceNameSaved;
    private Intent intent;

    private int numberOfRelays;
    private int numberOfMotors;
    private TextView numberOfRelaysText;
    private TextView numberOfMotorsText;
    private Button buttonPlus2;
    private Button buttonMinus2;

    private Button buttonPlus3;
    private Button buttonMinus3;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        sharedPref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        aSwitch1 = findViewById(R.id.switch1);
        aSwitch2 = findViewById(R.id.switch2);
        //aSwitch3 = findViewById(R.id.switch3);


        numberOfRelaysText = findViewById(R.id.numberOfRelays);
        numberOfMotorsText = findViewById(R.id.numberOfMotors);
        buttonPlus2 = findViewById(R.id.buttonPlus2);
        buttonMinus2 = findViewById(R.id.buttonMinus2);

        buttonPlus3 = findViewById(R.id.buttonPlus3);
        buttonMinus3 = findViewById(R.id.buttonMinus3);

        if (sharedPref != null) {
            switch1_state = sharedPref.getBoolean("Switch1", switch1_state);
            switch2_state = sharedPref.getBoolean("Switch2", switch2_state);
            aSwitch1.setChecked(switch1_state);
            aSwitch2.setChecked(switch2_state);
            deviceNameSaved = sharedPref.getString("Name", deviceNameSaved);
            numberOfRelays = sharedPref.getInt("Relays", 0);
            numberOfMotors = sharedPref.getInt("Motors",0);
            numberOfRelaysText.setText(String.valueOf(numberOfRelays));
            numberOfMotorsText.setText(String.valueOf(numberOfMotors));
        }

        buttonPlus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfRelays == 50) {
                    Toast.makeText(Settings.this, "Max 50 relays", Toast.LENGTH_SHORT).show();
                } else {
                    numberOfRelays += 1;
                    numberOfRelaysText.setText(String.valueOf(numberOfRelays));
                    editor.putInt("Relays",numberOfRelays);
                    editor.commit();
                }
            }
        });
        buttonMinus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfRelays == 0) {
                    Toast.makeText(Settings.this, "Min 0 relays", Toast.LENGTH_SHORT).show();
                } else {
                    numberOfRelays -= 1;
                    numberOfRelaysText.setText(String.valueOf(numberOfRelays));
                    editor.putInt("Relays",numberOfRelays);
                    editor.commit();
                }
            }
        });

        buttonPlus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfMotors == 50) {
                    Toast.makeText(Settings.this, "Max 50 motors", Toast.LENGTH_SHORT).show();
                } else {
                    numberOfMotors += 1;
                    numberOfMotorsText.setText(String.valueOf(numberOfMotors));
                    editor.putInt("Motors",numberOfMotors);
                    editor.commit();
                }
            }
        });
        buttonMinus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfMotors == 0) {
                    Toast.makeText(Settings.this, "Min 0 motors", Toast.LENGTH_SHORT).show();
                } else {
                    numberOfMotors-= 1;
                    numberOfMotorsText.setText(String.valueOf(numberOfMotors));
                    editor.putInt("Motors",numberOfMotors);
                    editor.commit();
                }
            }
        });

        aSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("Switch2", aSwitch2.isChecked());
                editor.commit();
            }
        });

        if (MainActivity.getDevice() != null) {
            aSwitch1.setClickable(true);
            aSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    editor.putBoolean("Switch1", aSwitch1.isChecked());
                    editor.putString("Name", MainActivity.getDevice().getName());
                    editor.commit();
                }
            });
        } else if (deviceNameSaved != null) {
            aSwitch1.setClickable(true);
            aSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("Switch1", aSwitch1.isChecked());
                    editor.putString("Name", deviceNameSaved);
                    editor.commit();
                }
            });
        } else {
            aSwitch1.setClickable(false);
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
                intent = new Intent(Settings.this,MainActivity.class);
              //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.item_1: {
                break;
            }
            case R.id.item_2: {
                intent = new Intent(Settings.this, RGBControl.class);
              //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.item_3: {
                intent = new Intent(Settings.this, Relays.class);
             //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.item_4: {
                intent = new Intent(Settings.this, Motor.class);
                //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);

    }
}