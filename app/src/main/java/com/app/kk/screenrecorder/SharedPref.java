package com.app.kk.screenrecorder;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    SharedPreferences uiSharedPref, shakeState, screenState, frate, fdesc, frateValue;

    public SharedPref(Context context) {
        uiSharedPref = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
        shakeState = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
        frate = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
        frateValue = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
        screenState = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
        fdesc = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
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
        editor.putBoolean("shake", state);
        editor.commit();
    }

    // this method will load the Night Mode State
    public Boolean loadShakeState() {
        Boolean state = shakeState.getBoolean("shake", false);
        return state;
    }

    public void setScreenState(Boolean state) {
        SharedPreferences.Editor editor = screenState.edit();
        editor.putBoolean("screen", state);
        editor.commit();
    }

    // this method will load the Night Mode State
    public Boolean loadScreenState() {
        Boolean state = screenState.getBoolean("screen", false);
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

    public void frameDesc(String value) {
        SharedPreferences.Editor editor = fdesc.edit();
        editor.putString("desc", value);
        editor.commit();
    }

    // this method will load the Night Mode State
    public String loadfDesc() {
        String value = fdesc.getString("desc", null);
        return value;
    }

    public void frateValue(int value) {
        SharedPreferences.Editor editor = frateValue.edit();
        editor.putInt("frate", value);
        editor.commit();
    }

    // this method will load the Night Mode State
    public int loadFrateValue() {
        int value = frateValue.getInt("frate", 25);
        return value;
    }


    //    For Shake Description
    public void shakeDesc(String value) {
        SharedPreferences.Editor editor = shakeState.edit();
        editor.putString("shakeDesc", value);
        editor.commit();
    }

    public String loadShaekDesc() {
        String value = shakeState.getString("shakeDesc", null);
        return value;
    }
}
