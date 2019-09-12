package com.app.kk.screenrecorder.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.kk.screenrecorder.R;
import com.app.kk.screenrecorder.ScreenOnOffBackgroundService;
import com.app.kk.screenrecorder.ShakeSensor.ShakeService;
import com.app.kk.screenrecorder.SharedPref;

public class ControlsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    public SwitchCompat s1, s2;
    LinearLayout shake, screen;
    SharedPref sharedPref;
    TextView s1Desc, s2Desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_controls);

        toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        shake = findViewById(R.id.shake);
        s1 = findViewById(R.id.s1);
        s1.setClickable(false);

        s1Desc = findViewById(R.id.s1Desc);

        screen = findViewById(R.id.screen);
        s2 = findViewById(R.id.s2);
        s2.setClickable(false);

        s2Desc = findViewById(R.id.s2Desc);

        if (sharedPref.loadShakeState()) {
            s1.setChecked(true);
            s1Desc.setText("Shaking your device will start/stop screen recording automatically.");
        } else {
            s1.setChecked(false);
            s1Desc.setText("Shaking your device won't do anything special");
        }

        if (sharedPref.loadScreenState()) {
            s2.setChecked(true);
        } else {
            s2.setChecked(false);
        }

//        Intent i0 = new Intent();
//        i0.setAction("com.app.kk.ScreenRecorder.ScreenService");
//        startService(i0);

        shake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shakeActivity();
            }
        });

        screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!s2.isChecked()) {
                    Intent backgroundService = new Intent(getApplicationContext(), ScreenOnOffBackgroundService.class);
                    s2.setChecked(true);
                    Toast.makeText(ControlsActivity.this, "Service On", Toast.LENGTH_SHORT).show();
                    sharedPref.setScreenState(true);
                    startService(backgroundService);

                } else {
                    sharedPref.setScreenState(false);
                    Toast.makeText(ControlsActivity.this, "Service Off", Toast.LENGTH_SHORT).show();
                    s2.setChecked(false);
                }

            }
        });
    }

    public void shakeActivity() {
        final Intent intent = new Intent(getApplicationContext(), ShakeService.class);

        if (!s1.isChecked()) {
            s1.setChecked(true);
            sharedPref.setShakeState(true);
            s1Desc.setText("Shaking your device will start/stop screen recording automatically.");
            Toast.makeText(ControlsActivity.this, "Service Started", Toast.LENGTH_SHORT).show();
            //Start Service
            startService(intent);

        } else {
            sharedPref.setShakeState(false);
            s1Desc.setText("Shaking your device won't do anything special");
            Toast.makeText(ControlsActivity.this, "Service Stopped", Toast.LENGTH_SHORT).show();
            s1.setChecked(false);
            stopService(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
