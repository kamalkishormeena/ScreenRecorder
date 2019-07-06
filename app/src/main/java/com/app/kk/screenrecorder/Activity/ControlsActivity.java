package com.app.kk.screenrecorder.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.app.kk.screenrecorder.R;
import com.app.kk.screenrecorder.ShakeSensor.ShakeService;
import com.app.kk.screenrecorder.SharedPref;

public class ControlsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    public Switch s1;
    LinearLayout shake;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controls);

        toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        shake = findViewById(R.id.shake);
        s1 = findViewById(R.id.s1);


        if (sharedPref.loadShakeState()) {
            s1.setChecked(true);
        } else {
            s1.setChecked(false);
        }

        final Intent intent = new Intent(getApplicationContext(), ShakeService.class);


        s1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!s1.isChecked()) {
                    s1.setChecked(true);
                    sharedPref.setShakeState(true);
                    Toast.makeText(ControlsActivity.this, "Service Started", Toast.LENGTH_SHORT).show();
                    //Start Service
                    startService(intent);

                } else {
                    sharedPref.setShakeState(false);
                    Toast.makeText(ControlsActivity.this, "Service Stopped", Toast.LENGTH_SHORT).show();
                    s1.setChecked(false);
                }
            }
        });

        shake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!s1.isChecked()) {
                    s1.setChecked(true);
                    sharedPref.setShakeState(true);
                    Toast.makeText(ControlsActivity.this, "Service Started", Toast.LENGTH_SHORT).show();
                    //Start Service
                    startService(intent);

                } else {
                    sharedPref.setShakeState(false);
                    Toast.makeText(ControlsActivity.this, "Service Stopped", Toast.LENGTH_SHORT).show();
                    s1.setChecked(false);
                }
            }
        });
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
}
