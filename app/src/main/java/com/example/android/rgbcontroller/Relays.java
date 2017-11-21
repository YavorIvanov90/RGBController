package com.example.android.rgbcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by knock on 20-Nov-17.
 */

public class Relays extends AppCompatActivity {

    private  Intent intent;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relays);
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
                intent = new Intent(Relays.this, Settings.class);
                startActivity(intent);
                break;
            }
            case R.id.item_2: {
                intent = new Intent(Relays.this, RGBControl.class);
                startActivity(intent);
                break;
            }
            case R.id.item_3: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);

    }
}
