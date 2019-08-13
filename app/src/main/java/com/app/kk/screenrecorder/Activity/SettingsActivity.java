package com.app.kk.screenrecorder.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.kk.screenrecorder.Dialog.AboutDialog;
import com.app.kk.screenrecorder.Dialog.RatingApp;
import com.app.kk.screenrecorder.R;

public class SettingsActivity extends AppCompatActivity {

    public LinearLayout quality, rSettings, controls;
    public TextView rate;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        quality = findViewById(R.id.quality);
        rSettings = findViewById(R.id.rSetting);
        controls = findViewById(R.id.controls);
        rate = findViewById(R.id.rate);

        quality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SettingsActivity.this, QualityActivity.class);
                startActivity(myIntent);
                overridePendingTransition(0, 0);

            }
        });

        rSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SettingsActivity.this, RecordingActivity.class);
                startActivity(myIntent);
                overridePendingTransition(0, 0);

            }
        });

        controls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SettingsActivity.this, ControlsActivity.class);
                startActivity(myIntent);
                overridePendingTransition(0, 0);

            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
