package com.app.kk.screenrecorder.Dialog;

import android.os.AsyncTask;
import android.util.Log;

import com.app.kk.screenrecorder.Interface.ICountdownView;

public class CountdownTask extends AsyncTask<Integer, Integer, Integer> {

    private static final String TAG = "CountdownTask";
    private ICountdownView mCountdownView;

    public CountdownTask(ICountdownView countdownView) {
        this.mCountdownView = countdownView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Code to set up the timer ...
        mCountdownView.initCountdownView();

    }

    @Override
    protected Integer doInBackground(Integer... params) {
        // setting the default number of steps ..
        int countdownSteps = 5;
        // reading the number of steps if mentioned in the parameters
        if (params.length >= 1 && params[0] != null) {
            countdownSteps = params[0];
        }

        // setting the default step duration in milliseconds ..
        int stepDur = 1000;
        // reading the step duration if mentioned in the parameters
        if (params.length >= 2 && params[1] != null) {
            stepDur = params[1];
        }

        for (int i = 0; i < countdownSteps; i++) {
            publishProgress(countdownSteps - i);
            sleep(stepDur);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mCountdownView.updateCountdownView(values[0]);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mCountdownView.destroyCountDownView();
    }

    private void sleep(int mDelay) {
        try {
            Thread.sleep(mDelay);
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
        }
    }
}