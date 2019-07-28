package com.app.kk.screenrecorder.Activity;

import android.app.Dialog;
import android.os.Bundle;
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

import java.text.NumberFormat;
import java.text.ParseException;

public class QualityActivity extends AppCompatActivity {

    private Toolbar toolbar;
    final String KEY_SAVED_RADIO_BUTTON_INDEX = "SAVED_RADIO_BUTTON_INDEX";
    LinearLayout frate, bitRate;
    TextView desc1, desc2;
    SharedPref sharedPref;
    int nfps = 25;
    int nMbps = 4;
    int nbits = nMbps * 1000000;
    int checkIDFR;
    int checkIDVBR;
    private String fps = "25 FPS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality);

        toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        desc1 = findViewById(R.id.des1);
        frate = findViewById(R.id.fRate);
        bitRate = findViewById(R.id.bitRate);
        desc2 = findViewById(R.id.desc2);

        frate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frateDialog();
            }
        });

        bitRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitRateDialog();
            }
        });

        if (sharedPref.loadfDesc() == null) {
            desc1.setText("Screen Capture will record at " + nfps + " frame per second");
        } else {
            desc1.setText(sharedPref.loadfDesc());
        }
        if (sharedPref.loadVDesc() == null) {
            desc2.setText("Screen Capture will record at " + nMbps + "mbps");
        } else {
            desc2.setText(sharedPref.loadVDesc());
        }
    }

    private void frateDialog() {

        final Dialog dialog = new Dialog(this);
        View mylayout = LayoutInflater.from(this).inflate(R.layout.custome_framerate_dialg, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(mylayout);

        final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.rg);
        Button btnNo = (Button) dialog.findViewById(R.id.btnNo);
        Button btnYes = (Button) dialog.findViewById(R.id.btnYes);

        if (sharedPref.loadFrate() != 0) {
            radioGroup.check(sharedPref.loadFrate());
        } else {
            radioGroup.check(R.id.b);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) dialog.findViewById(checkedId);
                try {
                    nfps = NumberFormat.getInstance().parse(radioButton.getText().toString()).intValue();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                checkIDFR = checkedId;
                Toast.makeText(getApplicationContext(), "" + nfps + " FPS", Toast.LENGTH_LONG).show();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.setFrate(checkIDFR);
                sharedPref.frateValue(nfps);
                desc1.setText("Screen Capture will record at " + nfps + " frame per second");

                String desc = desc1.getText().toString();
                sharedPref.frameDesc(desc);

                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.show();
    }

    private void bitRateDialog() {

        final Dialog dialog = new Dialog(this);
        View mylayout = LayoutInflater.from(this).inflate(R.layout.custom_bitrate_dialog, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(mylayout);

        final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.rg);
        Button btnNo = (Button) dialog.findViewById(R.id.btnNo);
        Button btnYes = (Button) dialog.findViewById(R.id.btnYes);

        if (sharedPref.loadVrate() != 0) {
            radioGroup.check(sharedPref.loadVrate());
        } else {
            radioGroup.check(R.id.r10);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) dialog.findViewById(checkedId);
                try {
                    nMbps = NumberFormat.getInstance().parse(radioButton.getText().toString()).intValue();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                checkIDVBR = checkedId;
                Toast.makeText(getApplicationContext(), "" + nMbps + " Mbps", Toast.LENGTH_LONG).show();
                nbits = nMbps * 1000000;

            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.setVrate(checkIDVBR);
                sharedPref.VrateValue2(nbits);
                desc2.setText("Screen Capture will record at " + nMbps + "mbps");

                String desc1 = desc2.getText().toString();
                sharedPref.bitrateDesc(desc1);

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
