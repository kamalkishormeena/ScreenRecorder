package com.app.kk.screenrecorder.Activity;

import android.app.ActivityOptions;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;

import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.kk.screenrecorder.R;

public class SettingsActivity extends AppCompatActivity {

    public LinearLayout quality, rSettings, controls;
    public TextView rate, qua, con;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        con = findViewById(R.id.con);
        qua = findViewById(R.id.qua);
        quality = findViewById(R.id.quality);
        rSettings = findViewById(R.id.rSetting);
        controls = findViewById(R.id.controls);
        rate = findViewById(R.id.rate);

        quality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**Shared Element Code*/
//                Intent in = new Intent(SettingsActivity.this, QualityActivity.class);
////                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(SettingsActivity.this, quality,"qua");
//
//                Pair[] pairs = new Pair[1];
//                pairs[0] = new Pair<View, String>(quality, "quality");
//                pairs[0] = new Pair<View, String>(qua, "qua");
//                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(SettingsActivity.this, pairs);
//                startActivity(in,activityOptions.toBundle());
//
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
