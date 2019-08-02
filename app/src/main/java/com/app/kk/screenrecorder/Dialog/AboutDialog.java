package com.app.kk.screenrecorder.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.app.kk.screenrecorder.Activity.MainActivity;
import com.app.kk.screenrecorder.R;

public class AboutDialog {

    public static void aboutDialog(Context mContext) {
        final Dialog dialog = new Dialog(mContext);
        View mylayout = LayoutInflater.from(mContext).inflate(R.layout.layout_about, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(mylayout);
        Button dissmiss = (Button) dialog.findViewById(R.id.dissmiss);
        dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

    }
}
