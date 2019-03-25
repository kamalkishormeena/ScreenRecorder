package com.app.kk.screenrecorder;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.Resources;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import static android.app.Notification.GROUP_ALERT_SUMMARY;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 1000;
    private int cms;
    private MediaProjectionManager mediaPM;
    private static int width;
    private static int height;
    private MediaProjection mediaProj;
    private VirtualDisplay virtualDisplay;
    private MediaProjectionCallback mediaPCB;
    private MediaRecorder mediaRecorder;
    private static final SparseIntArray sArray = new SparseIntArray();
    private static final int crp = 10;

    String string;
    List<Item> arraylist;
    ListView listview;
    String string1 = "";
    FloatingActionButton fav;
    public static List<String> listString;
    File file;
    File file1;
    long aLong;
    private static final String YES_ACTION = "YES_ACTION";
    private NotificationManager notificationManager;
    private Toolbar toolbar;



    static {
        sArray.append(Surface.ROTATION_0, 90);
        sArray.append(Surface.ROTATION_90, 0);
        sArray.append(Surface.ROTATION_180, 270);
        sArray.append(Surface.ROTATION_270, 180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        arraylist = new ArrayList<>();
        listview = (ListView) findViewById(R.id.listView1);
        View emptyView = getLayoutInflater().inflate(R.layout.empty,null);
        ((ViewGroup)listview.getParent()).addView(emptyView);
        listview.setEmptyView(emptyView);
        fav = (FloatingActionButton) findViewById(R.id.fav);
        string1 = "s";
        //creating the adapter
        Adapter adapter = new Adapter(this, R.layout.custom_listview, arraylist);
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Screen Recording");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
            }
        }
        permissions();
        Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (string1.equals("s")){
                    checkPermission();
                } else if (string1.equals("t")){
                    notificationManager.cancel(1);
                    string1 = "s";
                    fav.setImageResource(R.drawable.ic_recode);
                    stopRecording();
                    filePath();
                }
            }
        });
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        cms = metrics.densityDpi;
        width = metrics.widthPixels;
        Resources resources = getResources();
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        if(id>0) {
            height = metrics.heightPixels + getNavigationBarHeight();
        } else {
            height = metrics.heightPixels;
        }
        mediaRecorder = new MediaRecorder();
        mediaPM = (MediaProjectionManager) getSystemService
                (Context.MEDIA_PROJECTION_SERVICE);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        StartRecording(getIntent());
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

    private void permissions(){
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)
                .check();
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
    public void format(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        Date now = new Date();
        string = formatter.format(now);
    }
    @SuppressLint("DefaultLocale")
    public static String fdfdfbg(long seconds) {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(seconds),
                TimeUnit.MILLISECONDS.toMinutes(seconds) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(seconds)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(seconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(seconds)));
    }
    public static String fileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[] { "Adapter", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
    public void filePath(){
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
            String filepath = Environment.getExternalStorageDirectory() + "/Screen Recording/"+list[i].getName();
            File file = new File(filepath);
            long length = file.length();
            if (length < 1024){
                file1 = new File(Environment.getExternalStorageDirectory() + "/Screen Recording/"+list[i].getName());
                file1.delete();
            } else {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(getApplicationContext(), Uri.parse(Environment.getExternalStorageDirectory() + "/Screen Recording/"+list[i].getName()));
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                aLong = Long.parseLong(time );
            }
            arraylist.add(new Item("video.png", list[i].getName(), ""+fdfdfbg(aLong),"Size : "+ fileSize(length)));

            Adapter adapter = new Adapter(this, R.layout.custom_listview, arraylist);
            //attaching adapter to the listview
            listview.setAdapter(adapter);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, listString);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + "/Screen Recording/"+ listString.get(position));
                intent.setDataAndType(uri, "video/*");
                startActivity(intent);
            }
        });


    }
    private Intent fflvv() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    public void notification() {
        Intent yesIntent = fflvv();
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
                    fav.setImageResource(R.drawable.ic_recode);
                    stopRecording();
                    filePath();
                    break;
            }
        }
    }
    
    public void checkPermission(){
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
            recorderFormat();
            mediaProjection();
        }
    }

    public void stopRecording(){
        mediaRecorder.stop();
        mediaRecorder.reset();
        Log.v(TAG, "Stopping Recording");
        stopCheck();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE) {
            Log.e(TAG, "Unknown request code: " + requestCode);
            return;
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "Screen Cast Permission Denied", Toast.LENGTH_SHORT).show();
            notificationManager.cancel(1);
            filePath();
            string1 = "s";
            fav.setImageResource(R.drawable.ic_recode);

            return;
        }else {
            string1 = "t";
            fav.setImageResource(R.drawable.ic_clear);
            notification();
            activityStart();

        }
        mediaPCB = new MediaProjectionCallback();
        mediaProj = mediaPM.getMediaProjection(resultCode, data);
        mediaProj.registerCallback(mediaPCB, null);
        virtualDisplay = createVirtualDisplay();
        mediaRecorder.start();
    }
    public void activityStart() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void mediaProjection() {
        if (mediaProj == null) {
            startActivityForResult(mediaPM.createScreenCaptureIntent(), REQUEST_CODE);
            return;
        }
        virtualDisplay = createVirtualDisplay();
        mediaRecorder.start();
    }

    private VirtualDisplay createVirtualDisplay() {
        return mediaProj.createVirtualDisplay("MainActivity",
                width, height, cms,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mediaRecorder.getSurface(), null /*Callbacks*/, null
                /*Handler*/);
    }

    private void recorderFormat() {

        format();

        try {
            CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(Environment.getExternalStorageDirectory()+"/Screen Recording" + "/Screen Recording "+ string +".mp4");
            mediaRecorder.setVideoSize(width, height);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
            mediaRecorder.setCaptureRate(20);
            mediaRecorder.setVideoFrameRate(20);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = sArray.get(rotation + 90);
            mediaRecorder.setOrientationHint(orientation);
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            mediaRecorder.stop();
            mediaRecorder.reset();
            Log.v(TAG, "Recording Stopped");

            mediaProj = null;
            stopCheck();
        }
    }

    private void stopCheck() {
        if (virtualDisplay == null) {
            return;
        }
        virtualDisplay.release();

        stopMediaProj();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMediaProj();
    }

    private void stopMediaProj() {
        if (mediaProj != null) {
            mediaProj.unregisterCallback(mediaPCB);
            mediaProj.stop();
            mediaProj = null;
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
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share ){

            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                String sAux = "\n "+getString(R.string.app_name)+" - Download Now\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id="+getPackageName()+" \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch(Exception e) {

            }
        }

        switch (item.getItemId())
        {
            case R.id.about:
                aboutDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void aboutDialog(){
        final Dialog dialog = new Dialog(MainActivity.this);
        View mylayout = LayoutInflater.from(this).inflate(R.layout.layout_about, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(mylayout);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        Button dissmiss = (Button) dialog.findViewById(R.id.dissmiss);
        dissmiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
}
