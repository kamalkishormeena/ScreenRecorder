package com.app.kk.screenrecorder.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.kk.screenrecorder.R;
import com.app.kk.screenrecorder.SharedPref;

import java.io.File;

import static com.app.kk.screenrecorder.Activity.MainActivity.listString;

public class RecordingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    LinearLayout folder;
    File file;
    final int currentMax = 10;
    SwitchCompat audioSwitch;
    TextView address, desc3, timertxt;
    LinearLayout audio, count;
    SeekBar seekBar;
    SharedPref sharedPref;
    int currentProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_settings);

        sharedPref = new SharedPref(this);

        toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        address = findViewById(R.id.address);
        audioSwitch = findViewById(R.id.sAudio);
        audio = findViewById(R.id.Audio);
        folder = findViewById(R.id.folder);
        count = findViewById(R.id.count);
        desc3 = findViewById(R.id.desc3);

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

        count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDialog();
            }
        });
    }

    private void countDialog() {
        final Dialog dialog = new Dialog(this);
        View mylayout = LayoutInflater.from(this).inflate(R.layout.custom_countdown_dialog, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(mylayout);

        Button btnNo = (Button) dialog.findViewById(R.id.btnNo);
        Button btnYes = (Button) dialog.findViewById(R.id.btnYes);

        timertxt = dialog.findViewById(R.id.timertxt);
        seekBar = dialog.findViewById(R.id.seekBar);

        seekBar.setMax(currentMax / 1);
        if (sharedPref.loadTimerText() != 0) {
            seekBar.setProgress(sharedPref.loadTimerText());
            timertxt.setText("" + sharedPref.loadTimerText());
        } else {
            seekBar.setProgress(0);
            timertxt.setText("0");
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentProgress = progress * 1;
                timertxt.setText("" + currentProgress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.setTimerText(currentProgress);
                Toast.makeText(getApplicationContext(), "" + currentProgress, Toast.LENGTH_LONG).show();

                dialog.dismiss();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
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
