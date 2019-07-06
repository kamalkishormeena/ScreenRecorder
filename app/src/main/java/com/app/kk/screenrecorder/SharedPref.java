package com.app.kk.screenrecorder;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    SharedPreferences uiSharedPref, shakeState, frate;

    public SharedPref(Context context) {
        uiSharedPref = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
        shakeState = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
        frate = context.getSharedPreferences("filename", Context.MODE_PRIVATE);


    }

    // this method will save the nightMode State : True or False
    public void setNightModeState(Boolean state) {
        SharedPreferences.Editor editor = uiSharedPref.edit();
        editor.putBoolean("NightMode", state);
        editor.commit();
    }

    // this method will load the Night Mode State
    public Boolean loadNightModeState() {
        Boolean state = uiSharedPref.getBoolean("NightMode", false);
        return state;
    }

    public void setShakeState(Boolean state) {
        SharedPreferences.Editor editor = shakeState.edit();
        editor.putBoolean("switch", state);
        editor.commit();
    }

    // this method will load the Night Mode State
    public Boolean loadShakeState() {
        Boolean state = shakeState.getBoolean("switch", false);
        return state;
    }

    public void setFrate(int value) {
        SharedPreferences.Editor editor = frate.edit();
        editor.putInt("key", value);
        editor.commit();
    }

    // this method will load the Night Mode State
    public int loadFrate() {
        int value = frate.getInt("key", 0);
        return value;
    }


    public void setDriverId(int driverId) {
        SharedPreferences.Editor editor = uiSharedPref.edit();
        editor.putInt("driver_id", driverId);
        editor.apply();
    }

    // this method will load the Night Mode State
    public int getDriverID() {
        int driverId = uiSharedPref.getInt("driver_id", 0);
        return driverId;
    }
}
