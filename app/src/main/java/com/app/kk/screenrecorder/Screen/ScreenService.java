package com.app.kk.screenrecorder.Screen;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ScreenService extends Service {
    BroadcastReceiver mReceiver = null;

    @Override
    public void onCreate() {
        super.onCreate();
// Toast.makeText(getBaseContext(), "Service on create", Toast.LENGTH_SHORT).show();

// Register receiver that handles screen on and screen off logic
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onStart(Intent intent, int startId) {

        boolean screenOn = false;

        try {
// Get ON/OFF values sent from receiver ( AEScreenOnOffReceiver.java )
            screenOn = intent.getBooleanExtra("screen_state", false);

        } catch (Exception e) {
        }

//  Toast.makeText(getBaseContext(), "Service on start :"+screenOn,
//Toast.LENGTH_SHORT).show();

        if (!screenOn) {

// your code here
// Some time required to start any service
            Toast.makeText(getBaseContext(), "Screen on, ", Toast.LENGTH_SHORT).show();

        } else {

// your code here
// Some time required to stop any service to save battery consumption
            Toast.makeText(getBaseContext(), "Screen off,", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
// TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {

        Log.i("ScreenOnOff", "Service  distroy");
        if (mReceiver != null)
            unregisterReceiver(mReceiver);
    }
}
