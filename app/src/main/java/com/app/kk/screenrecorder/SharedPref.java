package com.app.kk.screenrecorder;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    SharedPreferences uiSharedPref, shakeState, screenState, frate, fdesc, frateValue, view, vbrate, vbValue, vdesc;

    public SharedPref(Context context) {
        uiSharedPref = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
        shakeState = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
        frate = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
        frateValue = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
        fdesc = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
        screenState = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
        vbrate = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
        vbValue = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
        vdesc = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
        view = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    /**
     * Night Mode
     */
    public void setNightModeState(Boolean state) {
        SharedPreferences.Editor editor = uiSharedPref.edit();
        editor.putBoolean("NightMode", state);
        editor.commit();
    }

    public Boolean loadNightModeState() {
        Boolean state = uiSharedPref.getBoolean("NightMode", false);
        return state;
    }

    /**
     * Shake State
     */
    public void setShakeState(Boolean state) {
        SharedPreferences.Editor editor = shakeState.edit();
        editor.putBoolean("shake", state);
        editor.commit();
    }

    public Boolean loadShakeState() {
        Boolean state = shakeState.getBoolean("shake", false);
        return state;
    }

    /**
     * RecyclerView Layout
     */
    public void setView(int value) {
        SharedPreferences.Editor editor = view.edit();
        editor.putInt("layout", value);
        editor.commit();
    }

    public int loadView() {
        int value = view.getInt("layout", 1);
        return value;
    }

    /**
     * Screen State
     */
    public void setScreenState(Boolean state) {
        SharedPreferences.Editor editor = screenState.edit();
        editor.putBoolean("screen", state);
        editor.commit();
    }

    public Boolean loadScreenState() {
        Boolean state = screenState.getBoolean("screen", false);
        return state;
    }

    /**
     * Frame Rate
     */
    public void setFrate(int value) {
        SharedPreferences.Editor editor = frate.edit();
        editor.putInt("key1", value);
        editor.commit();
    }

    public int loadFrate() {
        int value = frate.getInt("key1", 0);
        return value;
    }

    /**
     * Frame Rate Description
     */
    public void frameDesc(String value) {
        SharedPreferences.Editor editor = fdesc.edit();
        editor.putString("desc1", value);
        editor.commit();
    }

    public String loadfDesc() {
        String value = fdesc.getString("desc1", null);
        return value;
    }

    /**
     * Frame Rate Value
     */
    public void frateValue(int value) {
        SharedPreferences.Editor editor = frateValue.edit();
        editor.putInt("frate", value);
        editor.commit();
    }

    public int loadFrateValue() {
        int value = frateValue.getInt("frate", 25);
        return value;
    }

    /**
     * Video BitRate
     */
    public void setVrate(int value) {
        SharedPreferences.Editor editor = vbrate.edit();
        editor.putInt("key2", value);
        editor.commit();
    }

    public int loadVrate() {
        int value = vbrate.getInt("key2", 0);
        return value;
    }

    /**
     * Video Bitrate Description
     */
    public void bitrateDesc(String value) {
        SharedPreferences.Editor editor = vdesc.edit();
        editor.putString("desc2", value);
        editor.commit();
    }

    public String loadVDesc() {
        String value = vdesc.getString("desc2", null);
        return value;
    }

    /**
     * Video Bitrate Value
     */
    public void VrateValue(int value) {
        SharedPreferences.Editor editor = vbValue.edit();
        editor.putInt("frate", value);
        editor.commit();
    }

    public int loadVrateValue() {
        int value = vbValue.getInt("frate", 25);
        return value;
    }

    /**
     * Video Bitrate Value 2
     */
    public void VrateValue2(int value) {
        SharedPreferences.Editor editor = vbValue.edit();
        editor.putInt("frate", value);
        editor.commit();
    }

    public int loadVrateValue2() {
        int value = vbValue.getInt("frate", 25);
        return value;
    }


    /**
     * Shake Description
     */
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
