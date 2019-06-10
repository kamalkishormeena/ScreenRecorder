package com.app.kk.screenrecorder.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.app.kk.screenrecorder.R;

public class SettingsActivity extends AppCompatActivity {

    public TextView quality, rSettings, controls;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        quality = findViewById(R.id.quality);
        rSettings = findViewById(R.id.rSetting);
        controls = findViewById(R.id.controls);


    }
}
