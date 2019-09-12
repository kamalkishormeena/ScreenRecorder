package com.app.kk.screenrecorder.Activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Animatable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.PowerManager;

import androidx.annotation.RequiresApi;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import androidx.core.app.NotificationCompat.Action;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.widget.Toolbar;

import com.app.kk.screenrecorder.Adapter.CustomAdapter;
import com.app.kk.screenrecorder.Dialog.AboutDialog;
import com.app.kk.screenrecorder.Dialog.CountDown;
import com.app.kk.screenrecorder.Dialog.RatingApp;
import com.app.kk.screenrecorder.Interface.RecyclerClickListener;
import com.app.kk.screenrecorder.Utils.RecyclerView.RecyclerTouchListener;
import com.app.kk.screenrecorder.Utils.ToolbarActionModeCallback;
import com.app.kk.screenrecorder.Utils.RecyclerView.EmptyRecyclerView;
import com.app.kk.screenrecorder.Model.Item;
import com.app.kk.screenrecorder.R;
import com.app.kk.screenrecorder.ShakeSensor.ScreenReceiver;
import com.app.kk.screenrecorder.ShakeSensor.ShakeDetector;
import com.app.kk.screenrecorder.SharedPref;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import static android.app.Notification.GROUP_ALERT_SUMMARY;

import static com.app.kk.screenrecorder.Adapter.CustomAdapter.SPAN_COUNT_ONE;
import static com.app.kk.screenrecorder.Adapter.CustomAdapter.SPAN_COUNT_TWO;


public class MainActivity extends AppCompatActivity {

    public ActionMode mActionMode;

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1000;
    private int densityDPI;
    public TextView timer;
    private static int width;
    private static int height;
    public LinearLayout timL1;
    private MediaProjectionManager mProjectionManager;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private static final SparseIntArray sArray = new SparseIntArray();
    private static final int crp = 10;
    //    private RecyclerView recyclerView;
    private EmptyRecyclerView recyclerView;

    String string;
    List<Item> arraylist;
    String string1 = "";
    FloatingActionButton fav;
    public static List<String> listString;
    File file;
    File file1;
    long aLong;
    private static final String YES_ACTION = "YES_ACTION";
    private NotificationManager notificationManager;
    private Toolbar toolbar;

    LinearLayout emptyView;

    CustomAdapter adapter1;
    SharedPref sharedPref;
    private BroadcastReceiver mReceiver = null;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    private GridLayoutManager gridLayoutManager;
    private MediaProjectionCallback mMediaProjectionCallback;
    private MediaRecorder mMediaRecorder;

    static {
        sArray.append(Surface.ROTATION_0, 90);
        sArray.append(Surface.ROTATION_90, 0);
        sArray.append(Surface.ROTATION_180, 270);
        sArray.append(Surface.ROTATION_270, 180);
    }

    public static String fileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[]{"Adapter", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private int getNavigationBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = new SharedPref(this);

        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        RatingApp.app_launched(this);

        recyclerView = findViewById(R.id.listView2);
        emptyView = findViewById(R.id.emptyView);

        if (sharedPref.loadView() == 1)
            gridLayoutManager = new GridLayoutManager(this, SPAN_COUNT_ONE);
        else
            gridLayoutManager = new GridLayoutManager(this, SPAN_COUNT_TWO);
        recyclerView.getItemAnimator().setChangeDuration(100);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setEmptyView(emptyView);

        timL1 = findViewById(R.id.timL1);
        timer = (TextView) findViewById(R.id.timer);

        arraylist = new ArrayList<>();

        /**Creating Adapter*/
        adapter1 = new CustomAdapter(this, gridLayoutManager, R.layout.custom_layout_list, arraylist);
        recyclerView.setAdapter(adapter1);
        adapter1.notifyDataSetChanged();

        fav = (FloatingActionButton) findViewById(R.id.fav);
        string1 = "s";

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Screen Recording");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
            }
        }
        permissions();
        configureDisplay();
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (string1.equals("s")) {
                    checkPermission();
                } else if (string1.equals("t")) {
                    notificationManager.cancel(1);
                    string1 = "s";
                    fav.setImageResource(R.drawable.ic_record);
                    stopRecording();
                    filePath();
                }
            }
        });

        /**initialize Media Recorder*/
        mMediaRecorder = new MediaRecorder();
        mProjectionManager = (MediaProjectionManager) getSystemService
                (Context.MEDIA_PROJECTION_SERVICE);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        StartRecording(getIntent());
        /**Power Manager*/
        PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean wakeUpFlag = false;
        PowerManager.WakeLock wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "screenrecorder::MyWakelockTag");
//        wakeLock.acquire();
        if (!mgr.isInteractive()) {
            if (sharedPref.loadScreenState()) {
                wakeLock.release();
                onStop();
                Toast.makeText(MainActivity.this, "true", Toast.LENGTH_SHORT).show();

            }
        }
        /**ShakeDetector initialization*/
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                /*
                 * The following method, "handleShakeEvent(count):" is a stub //
                 * method you would use to setup whatever you want done once the
                 * device has been shook.
                 */
                if (sharedPref.loadShakeState()) {
                    fav.performClick();
                    //Toast.makeText(MainActivity.this, "Shaked", Toast.LENGTH_SHORT).show();
                    //toStart
//                    mediaProjection();
//                    initRecorder();
                } else
                    Toast.makeText(MainActivity.this, "Please Activate This feature from Control Settings", Toast.LENGTH_SHORT).show();
            }
        });
        implementRecyclerViewClickListeners();
    }

    public void checkIfRecyclerViewIsEmpty() {
        if (adapter1.getItemCount() <= 0) {
            recyclerView.setEmptyView(emptyView);
        }
    }

    private void configureDisplay() {
        /**Display Manager*/
        final int version = android.os.Build.VERSION.SDK_INT;
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (version >= 13) {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;
        } else {
            width = display.getWidth();
            height = display.getHeight();
        }

        /**Display Metrics*/
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        densityDPI = metrics.densityDpi;
        width = metrics.widthPixels;
        Resources resources = getResources();
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            height = metrics.heightPixels + getNavigationBarHeight();
        } else {
            height = metrics.heightPixels;
        }
    }


    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            filePath();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    private void permissions() {
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .check();
    }

    @SuppressLint("DefaultLocale")
    public static String timeFormat(long seconds) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(seconds),
                TimeUnit.MILLISECONDS.toMinutes(seconds) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(seconds)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(seconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(seconds)));
    }

    public void format() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        Date now = new Date();
        string = formatter.format(now);
    }

    public void filePath() {
        arraylist.clear();
        listString = new ArrayList<String>();
        File directory = Environment.getExternalStorageDirectory();
        file = new File(directory + "/Screen Recording");
        File list[] = file.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String cf) {
                // TODO Auto-generated method stub
                if (cf.contains(".mp4")) {
                    return true;
                }
                return false;
            }
        });
        for (int i = 0; i < list.length; i++) {
            listString.add(list[i].getName());
            String filepath = Environment.getExternalStorageDirectory() + "/Screen Recording/" + list[i].getName();
            File file = new File(filepath);
            long length = file.length();
            if (length < 1024) {
                file1 = new File(Environment.getExternalStorageDirectory() + "/Screen Recording/" + list[i].getName());
                file1.delete();
            } else {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(getApplicationContext(), Uri.parse(Environment.getExternalStorageDirectory() + "/Screen Recording/" + list[i].getName()));
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                aLong = Long.parseLong(time);
            }
            arraylist.add(new Item("Video.png", list[i].getName(), "" + timeFormat(aLong), "Size : " + fileSize(length)));
            adapter1.notifyDataSetChanged();
        }

    }

    private Intent intentFlag() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    public void notification() {
        Intent yesIntent = intentFlag();
        yesIntent.setAction(YES_ACTION);
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Screen Recorder")
                .setContentText("Recoding")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setOngoing(true)
                .setUsesChronometer(true)
                .setVibrate(null)
                .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
                .setGroup("My group")
                .setGroupSummary(false)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .addAction(new Action(
                        R.drawable.ic_close,
                        "Stop Recording",
                        PendingIntent.getActivity(this, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT)));
        notificationManager.notify(notificationId, mBuilder.build());
        //startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        StartRecording(intent);
        super.onNewIntent(intent);
    }

    private void StartRecording(Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case YES_ACTION:
                    notificationManager.cancel(1);
                    string1 = "s";
                    fav.setImageResource(R.drawable.ic_record);
                    stopRecording();
                    filePath();
                    break;
            }
        }
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (MainActivity.this, Manifest.permission.RECORD_AUDIO)) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission
                                .WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                        crp);
            }
        } else {
            initRecorder();
            mediaProjection();
        }
    }

    public void stopRecording() {
        final MediaPlayer notify2 = MediaPlayer.create(this, R.raw.end);
        notify2.start();
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        Toast.makeText(this, "File save in Your phone storage" + file + string + ".mp4",
                Toast.LENGTH_LONG).show();
        Log.v(TAG, "Stopping Recording");
        stopScreenSharing();
        adapter1.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE) {
            Log.e(TAG, "Unknown request code: " + requestCode);
            return;
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
            notificationManager.cancel(1);
            filePath();
            string1 = "s";
            fav.setImageResource(R.drawable.ic_record);

            return;
        } else {
            string1 = "t";
            fav.setImageResource(R.drawable.ic_stop);
            if (sharedPref.loadTimerText() == 0) {
                notification();
                activityStart();
            } else {
                CountDown.startTimer(this, MainActivity.this);
            }
        }
        if (sharedPref.loadTimerText() == 0) {
            mMediaProjectionCallback = new MediaProjectionCallback();
            mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
            mMediaProjection.registerCallback(mMediaProjectionCallback, null);
            mVirtualDisplay = createVirtualDisplay();
            mMediaRecorder.start();
        } else {
            mMediaProjectionCallback = new MediaProjectionCallback();
            mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
        }
    }

    public void activityStart() {
        final MediaPlayer notify1 = MediaPlayer.create(this, R.raw.start);
        notify1.start();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        /*Extra*/
        if (sharedPref.loadTimerText() != 0) {
            mMediaProjection.registerCallback(mMediaProjectionCallback, null);
            mVirtualDisplay = createVirtualDisplay();
            mMediaRecorder.start();
        }
    }

    private void mediaProjection() {
        if (mMediaProjection == null) {
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
            return;
        }
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();
    }

    private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay("MainActivity",
                width, height, densityDPI,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder.getSurface(), null /*Callbacks*/, null
                /*Handler*/);
    }

    private void initRecorder() {

        format();

        try {
            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);

            if (sharedPref.loadMic()) {
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            }
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mMediaRecorder.setOutputFile(Environment.getExternalStorageDirectory() + "/Screen Recording" + "/Screen Recording " + string + ".mp4");
            mMediaRecorder.setVideoSize(width, height);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            if (sharedPref.loadMic()) {
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            }
            mMediaRecorder.setVideoEncodingBitRate(sharedPref.loadVrateValue() * 1024 * 1024);
            mMediaRecorder.setCaptureRate(sharedPref.loadFrateValue());
            mMediaRecorder.setVideoFrameRate(sharedPref.loadFrateValue());
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = sArray.get(rotation + 90);
            mMediaRecorder.setOrientationHint(orientation);
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopScreenSharing() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();

        destroyMediaProjection();
    }

    private void destroyMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.unregisterCallback(mMediaProjectionCallback);
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        Log.i(TAG, "MediaProjection Stopped");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case crp: {
                if ((grantResults.length > 0) && (grantResults[0] +
                        grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
                } else {

                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_toolbar, menu);
        if (sharedPref.loadView() == 1) {
            menu.findItem(R.id.Change).setIcon(AnimatedVectorDrawableCompat.create(MainActivity.this, R.drawable.avd_grid_to_list));
        } else {
            menu.findItem(R.id.Change).setIcon(AnimatedVectorDrawableCompat.create(MainActivity.this, R.drawable.avd_list_to_grid));
        }
        if (adapter1.getItemCount() == 0) {
            menu.findItem(R.id.Change).setVisible(false);
        } else {
            menu.findItem(R.id.Change).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                String sAux = "\n " + getString(R.string.app_name) + " - Download Now\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=" + getPackageName() + " \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch (Exception e) {
            }
        }

        switch (item.getItemId()) {
            case R.id.about:
                AboutDialog.aboutDialog(this);
                return true;

            case R.id.Change:
                if (!((Animatable) item.getIcon()).isRunning()) {
                    if (sharedPref.loadView() == 1) {
                        item.setIcon(AnimatedVectorDrawableCompat.create(MainActivity.this, R.drawable.avd_grid_to_list));
                    } else {
                        item.setIcon(AnimatedVectorDrawableCompat.create(MainActivity.this, R.drawable.avd_list_to_grid));
                    }
                    switchLayout();
                }
                ((Animatable) item.getIcon()).start();
                adapter1.notifyItemRangeChanged(0, adapter1.getItemCount());
                return true;

            case R.id.settings:
                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                MainActivity.this.startActivity(myIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void switchLayout() {
        if (gridLayoutManager.getSpanCount() == SPAN_COUNT_ONE) {
            gridLayoutManager.setSpanCount(SPAN_COUNT_TWO);
            sharedPref.setView(SPAN_COUNT_TWO);
        } else {
            gridLayoutManager.setSpanCount(SPAN_COUNT_ONE);
            sharedPref.setView(SPAN_COUNT_ONE);
        }
        adapter1.notifyItemRangeChanged(0, adapter1.getItemCount());
    }

    public void RecyclerAdapter() {
        checkIfRecyclerViewIsEmpty();
        adapter1.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                checkIfRecyclerViewIsEmpty();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkIfRecyclerViewIsEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkIfRecyclerViewIsEmpty();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                checkIfRecyclerViewIsEmpty();
            }

            @Override
            public void onChanged() {
                super.onChanged();
                checkIfRecyclerViewIsEmpty();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPause() {
        // when the shake is about to turn off
        if (ScreenReceiver.wasScreenOn) {

            Log.e("MYAPP", "SCREEN TURNED OFF");
        } else {
            // this is when onPause() is called when the shake state has not changed
        }
        // Add the following line to unregister the Sensor Manager onPause
//        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();
        // only when shake turns on
        if (!ScreenReceiver.wasScreenOn) {
            // this is when onResume() is called due to a shake state change
            Log.e("MYAPP", "SCREEN TURNED ON");
        } else {
            // this is when onResume() is called when the shake state has not changed
        }
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyMediaProjection();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        mSensorManager.unregisterListener(mShakeDetector);
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            Log.v(TAG, "Recording Stopped");

            mMediaProjection = null;
            stopScreenSharing();
        }
    }


    /**
     * NEW Method to handle recycler view item click
     */

    //Implement item click and long click over recycler view
    private void implementRecyclerViewClickListeners() {
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerClickListener() {
            @Override
            public void onClick(View view, int position) {
                //If ActionMode not null select item
                if (mActionMode != null)
                    onListItemSelect(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                //Select item on long click
                onListItemSelect(position);
            }
        }));
    }

    //List item select method
    private void onListItemSelect(int position) {
        adapter1.toggleSelection(position);//Toggle the selection

        boolean hasCheckedItems = adapter1.getSelectedCount() > 0;//Check if any items are already selected or not


        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = ((AppCompatActivity) this).startSupportActionMode(new ToolbarActionModeCallback(this, adapter1, MainActivity.this, arraylist));
        else if (!hasCheckedItems && mActionMode != null)
            // there no selected items, finish the actionMode
            mActionMode.finish();

        if (mActionMode != null) {
            //set action mode title on item selection
            View myview = LayoutInflater.from(getApplicationContext()).inflate(R.layout.action_mode_bar, null);
            mActionMode.setCustomView(myview);
            TextView actionTitle = findViewById(R.id.actiontitle);
            actionTitle.setText("Screen Recorder(" + String.valueOf(adapter1
                    .getSelectedCount()) + ")");
//            mActionMode.setTitle(String.valueOf(adapter1
//                    .getSelectedCount()) + " selected");
        }

    }

    //Set action mode null after use
    public void setNullToActionMode() {
        if (mActionMode != null)
            mActionMode = null;
    }

    //Delete selected rows
    public void deleteRows() {
        SparseBooleanArray selected = adapter1
                .getSelectedIds();//Get selected ids

        //Loop all selected ids
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                //If current id is selected remove the item via key
                File fdelete = new File(Environment.getExternalStorageDirectory() + "/Screen Recording/" + listString.get(selected.keyAt(i)));
                fdelete.delete();
                arraylist.remove(selected.keyAt(i));
                adapter1.notifyDataSetChanged();//notify adapter
                RecyclerAdapter();
                //Snackbar.make(view, selected.size() + " item deleted.", Snackbar.LENGTH_LONG).show();
                Toast.makeText(this, selected.size() + " item deleted.", Toast.LENGTH_SHORT).show();//Show Toast
                mActionMode.finish();//Finish action mode after use
            }
        }
    }

    public void shareRows() {
        SparseBooleanArray selected = adapter1
                .getSelectedIds();//Get selected ids

        //Loop all selected ids
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                File sFile = new File(Environment.getExternalStorageDirectory() + "/Screen Recording/" + listString.get(selected.keyAt(i)));
                Uri uri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", sFile);
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                shareIntent.setType("video/*");

//                ArrayList<Uri> shareFiles = new ArrayList<Uri>();
//                for(String path : selected.keyAt(i)) {
//                    File file = new File(path);
//                    Uri sUri = Uri.fromFile(file);
//                    shareFiles.add(sUri);
//                }
//                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, shareFiles);

                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, listString.get(selected.keyAt(i)));
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                this.startActivity(Intent.createChooser(shareIntent, "Share with"));

                //Snackbar.make(view, selected.size() + " item deleted.", Snackbar.LENGTH_LONG).show();
//                Toast.makeText(this, selected.size() + " item deleted.", Toast.LENGTH_SHORT).show();//Show Toast
            }
        }
        mActionMode.finish();//Finish action mode after use
    }
}
