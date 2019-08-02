package com.app.kk.screenrecorder.Recorder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

import com.app.kk.screenrecorder.Activity.MainActivity;
import com.app.kk.screenrecorder.R;
import com.app.kk.screenrecorder.SharedPref;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//TODO: Update icons for notifcation
public class RecorderService extends Service {
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static int WIDTH, HEIGHT, FPS, DENSITY_DPI;
    private static int BITRATE;
    private static boolean mustRecAudio;
    private static String SAVEPATH;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    SharedPref sharedPref;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message message) {
            Toast.makeText(RecorderService.this, "Recording Stopped", Toast.LENGTH_SHORT).show();
//            showShareNotification();
        }
    };
    private boolean isRecording;
    private boolean useFloatingControls;
    private boolean showTouches;
    private FloatingControlService floatingControlService;
    private boolean isBound = false;
    //Service connection to manage the connection state between this service and the bounded service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Get the service instance
            FloatingControlService.ServiceBinder binder = (FloatingControlService.ServiceBinder) service;
            floatingControlService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            floatingControlService = null;
            isBound = false;
        }
    };
    private long startTime, elapsedTime = 0;
    private SharedPreferences prefs;
    private WindowManager window;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionCallback mMediaProjectionCallback;
    private MediaRecorder mMediaRecorder;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        //Find the action to perform from intent
        switch (intent.getAction()) {
            case Const.SCREEN_RECORDING_START:
                /* Wish MediaRecorder had a method isRecording() or similar. But, we are forced to
                 * manage the state ourself. Let's hope the request is honored.
                 * Request: https://code.google.com/p/android/issues/detail?id=800 */
                if (!isRecording) {
                    //Get values from Default SharedPreferences
                    getValues();
                    Intent data = intent.getParcelableExtra(Const.RECORDER_INTENT_DATA);
                    int result = intent.getIntExtra(Const.RECORDER_INTENT_RESULT, Activity.RESULT_OK);

                    //Initialize MediaRecorder class and initialize it with preferred configuration
                    mMediaRecorder = new MediaRecorder();
                    initRecorder();

                    //Set Callback for MediaProjection
                    mMediaProjectionCallback = new MediaProjectionCallback();
                    MediaProjectionManager mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

                    //Initialize MediaProjection using data received from Intent
                    mMediaProjection = mProjectionManager.getMediaProjection(result, data);
                    mMediaProjection.registerCallback(mMediaProjectionCallback, null);

                    /* Create a new virtual display with the actual default display
                     * and pass it on to MediaRecorder to start recording */
                    mVirtualDisplay = createVirtualDisplay();
                    try {
                        mMediaRecorder.start();

                        //If floating controls is enabled, start the floating control service and bind it here
                        if (useFloatingControls) {
                            Intent floatinControlsIntent = new Intent(this, FloatingControlService.class);
                            startService(floatinControlsIntent);
                            bindService(floatinControlsIntent,
                                    serviceConnection, BIND_AUTO_CREATE);
                        }

                        //Set the state of the recording
                        if (isBound)
                            floatingControlService.setRecordingState(Const.RecordingState.RECORDING);
                        isRecording = true;

                        //Send a broadcast receiver to the plugin app to enable show touches since the recording is started
                        if (showTouches) {
                            Intent TouchIntent = new Intent();
                            TouchIntent.setAction("com.app.kk.screenrecorder.SHOWTOUCH");
                            TouchIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                            sendBroadcast(TouchIntent);
                        }
                        Toast.makeText(this, "Recording Started", Toast.LENGTH_SHORT).show();
                    } catch (IllegalStateException e) {
                        Log.d(Const.TAG, "Mediarecorder reached Illegal state exception. Did you start the recording twice?");
                        Toast.makeText(this, "Recording Failed", Toast.LENGTH_SHORT).show();
                        isRecording = false;
                    }

                    /* Add Pause action to Notification to pause screen recording if the user's android version
                     * is >= Nougat(API 24) since pause() isnt available previous to API24 else build
                     * Notification with only default stop() action */
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //startTime is to calculate elapsed recording time to update notification during pause/resume
                        startTime = System.currentTimeMillis();
                        Intent recordPauseIntent = new Intent(this, RecorderService.class);
                        recordPauseIntent.setAction(Const.SCREEN_RECORDING_PAUSE);
                        PendingIntent precordPauseIntent = PendingIntent.getService(this, 0, recordPauseIntent, 0);
                        NotificationCompat.Action action = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", precordPauseIntent);

                        //Start Notification as foreground
                        startNotificationForeGround(createNotification(action).build(), Const.SCREEN_RECORDER_NOTIFICATION_ID);
                    } else
                        startNotificationForeGround(createNotification(null).build(), Const.SCREEN_RECORDER_NOTIFICATION_ID);
                } else {
                    Toast.makeText(this, "Recording Running", Toast.LENGTH_SHORT).show();
                }
                break;
            case Const.SCREEN_RECORDING_PAUSE:
                pauseScreenRecording();
                break;
            case Const.SCREEN_RECORDING_RESUME:
                resumeScreenRecording();
                break;
            case Const.SCREEN_RECORDING_STOP:
                //Unbind the floating control service if its bound (naturally unbound if floating controls is disabled)
                if (isBound)
                    unbindService(serviceConnection);
                stopScreenSharing();

                //Send a broadcast receiver to the plugin app to disable show touches since the recording is stopped
                if (showTouches) {
                    Intent TouchIntent = new Intent();
                    TouchIntent.setAction("com.app.kk.screenrecorder.DISABLETOUCH");
                    TouchIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    sendBroadcast(TouchIntent);
                }

                //The service is started as foreground service and hence has to be stopped
                stopForeground(true);
                break;
        }
        return START_STICKY;
    }

    @TargetApi(24)
    private void pauseScreenRecording() {
        mMediaRecorder.pause();
        //calculate total elapsed time until pause
        elapsedTime += (System.currentTimeMillis() - startTime);

        //Set Resume action to Notification and update the current notification
        Intent recordResumeIntent = new Intent(this, RecorderService.class);
        recordResumeIntent.setAction(Const.SCREEN_RECORDING_RESUME);
        PendingIntent precordResumeIntent = PendingIntent.getService(this, 0, recordResumeIntent, 0);
        NotificationCompat.Action action = new NotificationCompat.Action(android.R.drawable.ic_media_play, "Resume", precordResumeIntent);
        updateNotification(createNotification(action).setUsesChronometer(false).build(), Const.SCREEN_RECORDER_NOTIFICATION_ID);
        Toast.makeText(this, "Recording Paused", Toast.LENGTH_SHORT).show();

        if (isBound)
            floatingControlService.setRecordingState(Const.RecordingState.PAUSED);

        //Send a broadcast receiver to the plugin app to disable show touches since the recording is paused
        if (showTouches) {
            Intent TouchIntent = new Intent();
            TouchIntent.setAction("com.app.kk.screenrecorder.DISABLETOUCH");
            TouchIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            sendBroadcast(TouchIntent);
        }
    }

    @TargetApi(24)
    private void resumeScreenRecording() {
        mMediaRecorder.resume();

        //Reset startTime to current time again
        startTime = System.currentTimeMillis();

        //set Pause action to Notification and update current Notification
        Intent recordPauseIntent = new Intent(this, RecorderService.class);
        recordPauseIntent.setAction(Const.SCREEN_RECORDING_PAUSE);
        PendingIntent precordPauseIntent = PendingIntent.getService(this, 0, recordPauseIntent, 0);
        NotificationCompat.Action action = new NotificationCompat.Action(android.R.drawable.ic_media_pause,
                "Pause", precordPauseIntent);
        updateNotification(createNotification(action).setUsesChronometer(true)
                .setWhen((System.currentTimeMillis() - elapsedTime)).build(), Const.SCREEN_RECORDER_NOTIFICATION_ID);
        Toast.makeText(this, "Recording Paused", Toast.LENGTH_SHORT).show();

        if (isBound)
            floatingControlService.setRecordingState(Const.RecordingState.RECORDING);


        //Send a broadcast receiver to the plugin app to enable show touches since the recording is resumed
        if (showTouches) {
            if (showTouches) {
                Intent TouchIntent = new Intent();
                TouchIntent.setAction("com.app.kk.screenrecorder.SHOWTOUCH");
                TouchIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                sendBroadcast(TouchIntent);
            }
        }
    }

    //Virtual display created by mirroring the actual physical display
    private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay("MainActivity",
                WIDTH, HEIGHT, DENSITY_DPI,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder.getSurface(), null /*Callbacks*/, null
                /*Handler*/);
    }

    /* Initialize MediaRecorder with desired default values and values set by user. Everything is
     * pretty much self explanatory */
    private void initRecorder() {
        try {
            if (mustRecAudio)
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setOutputFile(SAVEPATH);
            mMediaRecorder.setVideoSize(WIDTH, HEIGHT);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            if (mustRecAudio)
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.setVideoEncodingBitRate(BITRATE);
            mMediaRecorder.setVideoFrameRate(FPS);
            int rotation = window.getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
            mMediaRecorder.setOrientationHint(orientation);
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void showShareNotification(){
//        Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                R.mipmap.ic_launcher);
//        /*Intent Shareintent = new Intent()
//                .setAction(Intent.ACTION_SEND)
//                .putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(SAVEPATH)))
//                .setType("video/mp4");*/
//        Intent videoListIntent = new Intent();
//        Intent editIntent = new Intent(this, EditVideoActivity.class);
//        editIntent.putExtra(Const.VIDEO_EDIT_URI_KEY, SAVEPATH);
//        PendingIntent editPendingIntent = PendingIntent.getActivity(this, 0, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent sharePendingIntent = PendingIntent.getActivity(this, 0, Intent.createChooser(
//                videoListIntent, "Share"), PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Builder shareNotification = new NotificationCompat.Builder(this)
//                .setContentTitle("Share")
//                .setContentText("Sharing")
//                .setSmallIcon(R.drawable.ic_notification)
//                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
//                .setAutoCancel(true)
//                .setContentIntent(sharePendingIntent)
//                .addAction(android.R.drawable.ic_menu_share, "Share"
//                        , sharePendingIntent)
//                .addAction(android.R.drawable.ic_menu_edit, "Edit"
//                        , editPendingIntent);
//        updateNotification(shareNotification.build(), Const.SCREEN_RECORDER_SHARE_NOTIFICATION_ID);
//    }

    /* Create Notification.Builder with action passed in case user's android version is greater than
     * API24 */
    private NotificationCompat.Builder createNotification(NotificationCompat.Action action) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);

        Intent recordStopIntent = new Intent(this, RecorderService.class);
        recordStopIntent.setAction(Const.SCREEN_RECORDING_STOP);
        PendingIntent precordStopIntent = PendingIntent.getService(this, 0, recordStopIntent, 0);

        Intent UIIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationContentIntent = PendingIntent.getActivity(this, 0, UIIntent, 0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setContentTitle("Recording")
                .setTicker("Screen Recorder")
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(
                        Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setUsesChronometer(true)
                .setOngoing(true)
                .setContentIntent(notificationContentIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .addAction(R.drawable.ic_notification_stop, "Stop",
                        precordStopIntent);
        if (action != null)
            notification.addAction(action);
        return notification;
    }

    //Start service as a foreground service. We dont want the service to be killed in case of low memory
    private void startNotificationForeGround(Notification notification, int ID) {
        startForeground(ID, notification);
    }

    //Update existing notification with its ID and new Notification data
    private void updateNotification(Notification notification, int ID) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Get user's choices for user choosable settings
    public void getValues() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String res = getResolution();
        setWidthHeight(res);
        FPS = 30;
        BITRATE = 7130317;
        mustRecAudio = false;
        String saveLocation =
                Environment.getExternalStorageDirectory() + File.separator + Const.APPDIR;
        File saveDir = new File(saveLocation);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && !saveDir.isDirectory()) {
            saveDir.mkdirs();
        }
        useFloatingControls = true;
        showTouches = true;
        String saveFileName = getFileSaveName();
        SAVEPATH = saveLocation + File.separator + saveFileName + ".mp4";
    }

    /* The PreferenceScreen save values as string and we save the user selected video resolution as
     * WIDTH x HEIGHT. Lets split the string on 'x' and retrieve width and height */
    private void setWidthHeight(String res) {
        String[] widthHeight = res.split("x");
        WIDTH = Integer.parseInt(widthHeight[0]);
        HEIGHT = Integer.parseInt(widthHeight[1]);
    }

    //Get the device resolution in pixels
    private String getResolution() {
        DisplayMetrics metrics = new DisplayMetrics();
        window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        window.getDefaultDisplay().getMetrics(metrics);
        DENSITY_DPI = metrics.densityDpi;
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        return width + "x" + height;
    }

    //Return filename of the video to be saved formatted as chosen by the user
    private String getFileSaveName() {
        String filename = "yyyyMMdd_hhmmss";
        String prefix = "recording";
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat(filename);
        return prefix + "_" + formatter.format(today);
    }

    //Stop and destroy all the objects used for screen recording
    private void destroyMediaProjection() {
        try {
            mMediaRecorder.stop();
            indexFile();
            Log.i(Const.TAG, "MediaProjection Stopped");
        } catch (RuntimeException e) {
            Log.e(Const.TAG, "Fatal exception! Destroying media projection failed." + "\n" + e.getMessage());
            if (new File(SAVEPATH).delete())
                Log.d(Const.TAG, "Corrupted file delete successful");
            Toast.makeText(this, "Delete Successfully", Toast.LENGTH_SHORT).show();
        } finally {
            mMediaRecorder.reset();
            mVirtualDisplay.release();
            mMediaRecorder.release();
            if (mMediaProjection != null) {
                mMediaProjection.unregisterCallback(mMediaProjectionCallback);
                mMediaProjection.stop();
                mMediaProjection = null;
            }
        }
        isRecording = false;
    }

    /* Its weird that android does not index the files immediately once its created and that causes
     * trouble for user in finding the video in gallery. Let's explicitly announce the file creation
     * to android and index it */
    private void indexFile() {
        //Create a new ArrayList and add the newly created video file path to it
        ArrayList<String> toBeScanned = new ArrayList<>();
        toBeScanned.add(SAVEPATH);
        String[] toBeScannedStr = new String[toBeScanned.size()];
        toBeScannedStr = toBeScanned.toArray(toBeScannedStr);

        //Request MediaScannerConnection to scan the new file and index it
        MediaScannerConnection.scanFile(this, toBeScannedStr, null, new MediaScannerConnection.OnScanCompletedListener() {

            @Override
            public void onScanCompleted(String path, Uri uri) {
                Log.i(Const.TAG, "SCAN COMPLETED: " + path);
                //Show toast on main thread
                Message message = mHandler.obtainMessage();
                message.sendToTarget();
                stopSelf();
            }
        });
    }

    private void stopScreenSharing() {
        if (mVirtualDisplay == null) {
            return;
        }
        destroyMediaProjection();
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Log.v(Const.TAG, "Recording Stopped");
            stopScreenSharing();
        }
    }
}
