package com.app.kk.screenrecorder.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.app.kk.screenrecorder.R;

public class RatingApp {
    private final static String APP_PNAME = "com.app.kk.screenrecorder";// Package Name
    private final static int DAYS_UNTIL_PROMPT = 1;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 1;//Min number of launches
    private static String APP_TITLE = "App Name";// App Name
    private static long EXTRA_DAYS;
    private static long EXTRA_LAUCHES;
    private static SharedPreferences.Editor editor;
    private static SharedPreferences prefs;

    public static void app_launched(Context mContext) {
        prefs = mContext.getSharedPreferences("apprater", Context.MODE_PRIVATE);
        if (prefs.getBoolean("dontshowagain", false)) {
            return;
        }

        editor = prefs.edit();

        EXTRA_DAYS = prefs.getLong("extra_days", 0);
        EXTRA_LAUCHES = prefs.getLong("extra_launches", 0);

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= (LAUNCHES_UNTIL_PROMPT + EXTRA_LAUCHES)) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000) + EXTRA_DAYS) {
                showRateDialog(mContext, editor);
            }
        }

        editor.commit();
    }

    private static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        final Dialog dialog = new Dialog(mContext);
        View mylayout = LayoutInflater.from(mContext).inflate(R.layout.custom_dialog_rating, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(mylayout);
        APP_TITLE = mContext.getResources().getString(R.string.app_name);
        dialog.setTitle("Rate " + APP_TITLE);

        TextView title = dialog.findViewById(R.id.Rtitle);
        TextView desc = dialog.findViewById(R.id.Rdesc);
        title.setText("Rate this App");
        desc.setText("If you enjoy using " + APP_TITLE + ", please take a minute to rate it. Thanks for your support.;)");

        Button rate = dialog.findViewById(R.id.rateNow);
        Button remind = dialog.findViewById(R.id.remindMe);
        Button thanks = dialog.findViewById(R.id.thanks);

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                delayDays(2);
                delayLaunches(2);
                dialog.dismiss();
            }
        });

        remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delayDays(1);
                delayLaunches(2);
                dialog.dismiss();
            }
        });

        thanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
//                    editor.putLong("launch_count", 0);

                }
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation3;
        dialog.show();
    }

    private static void delayLaunches(int numberOfLaunches) {
        long extra_launches = prefs.getLong("extra_launches", 0) + numberOfLaunches;
        editor.putLong("extra_launches", extra_launches);
        editor.commit();
    }

    private static void delayDays(int numberOfDays) {
        Long extra_days = prefs.getLong("extra_days", 0) + (numberOfDays * 1000 * 60 * 60 * 24);
        editor.putLong("extra_days", extra_days);
        editor.commit();
    }
}