package com.app.kk.screenrecorder.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.kk.screenrecorder.Activity.MainActivity;
import com.app.kk.screenrecorder.R;
import com.app.kk.screenrecorder.SharedPref;

public class CountDown {

    static SharedPref sharedPref;
    private static int sec = 60 * 2;  // this is for 2 for min TODO replace your sec

    public static void startTimer(Context context, final MainActivity mainActivity) {
        sharedPref = new SharedPref(context);
        final Dialog dialog = new Dialog(context);
        View mylayout = LayoutInflater.from(context).inflate(R.layout.test, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(mylayout);

        int i = sharedPref.loadTimerText();
        final TextView tim = dialog.findViewById(R.id.tim);
//        Button cancel = dialog.findViewById(R.id.cancel);

        final CountDownTimer downTimer = new CountDownTimer(1000 * i, 1000) {

            public void onTick(long millisUntilFinished) {
                String v = String.format("%02d", millisUntilFinished / 60000);
                int va = (int) ((millisUntilFinished % 60000) / 1000);
                tim.setText(v + ":" + String.format("%02d", va));
            }

            public void onFinish() {
                mainActivity.notification();
                mainActivity.activityStart();
                dialog.dismiss();
            }
        };
        downTimer.start();

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog);
        dialog.show();

    }

}
