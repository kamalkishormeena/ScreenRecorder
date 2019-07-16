package com.app.kk.screenrecorder.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.kk.screenrecorder.R;

import java.io.File;

import static com.app.kk.screenrecorder.Activity.MainActivity.listString;

public class RecordingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    LinearLayout folder;
    File file;
    TextView address;
    SwitchCompat audioSwitch;
    LinearLayout audio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_settings);

        toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        address = findViewById(R.id.address);
        audioSwitch = findViewById(R.id.sAudio);
        audio = findViewById(R.id.Audio);
        folder = findViewById(R.id.folder);

        file = new File(Environment.getExternalStorageDirectory() + "/Screen Recording/");
        String add = file.getAbsolutePath();
        address.setText("" + add);

        audioSwitch.setClickable(false);
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioPermission();
            }
        });

        folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFolder();

            }
        });
    }

    private void audioPermission() {
        if (!audioSwitch.isChecked())
            audioSwitch.setChecked(true);
        else
            audioSwitch.setChecked(false);
    }

    public void openFolder() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(Intent.createChooser(i, "Choose directory"), 9999);
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
