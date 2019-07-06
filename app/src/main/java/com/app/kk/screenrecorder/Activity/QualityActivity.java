package com.app.kk.screenrecorder.Activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.kk.screenrecorder.R;
import com.app.kk.screenrecorder.SharedPref;

import java.io.File;

public class QualityActivity extends AppCompatActivity {

    private Toolbar toolbar;
    final String KEY_SAVED_RADIO_BUTTON_INDEX = "SAVED_RADIO_BUTTON_INDEX";
    LinearLayout frate;
    TextView desc1;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality);

        toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        desc1 = findViewById(R.id.des1);
        frate = findViewById(R.id.frate);
        frate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frateDialog();
            }
        });


    }

    private void frateDialog() {

        sharedPref = new SharedPref(this);
        final Dialog dialog = new Dialog(this);
        View mylayout = LayoutInflater.from(this).inflate(R.layout.custome_framerate_dialg, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(mylayout);

        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.rg);
        Button btnNo = (Button) dialog.findViewById(R.id.btnNo);
        Button btnYes = (Button) dialog.findViewById(R.id.btnYes);

        final int selectedId = radioGroup.getCheckedRadioButtonId();
        final RadioButton radioButton = (RadioButton) dialog.findViewById(selectedId);
        final String fps = (String) radioButton.getText().toString();

        radioGroup.check(sharedPref.loadFrate());


        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.setFrate(selectedId);
                desc1.setText("Screen Capture will record upto " + fps);

                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.show();
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
