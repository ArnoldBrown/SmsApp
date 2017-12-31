package gonext.smsapp.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.telephony.TelephonyManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.text.SimpleDateFormat;
import java.util.Date;

import gonext.smsapp.MainActivity;
import gonext.smsapp.R;

/**
 * Created by ram on 18/08/17.
 */

public class Utils {
    public static String getNotificationTime(long time){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:s");
            return simpleDateFormat.format(new Date(time));
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
    public static void changeRecordingState(boolean state, Context context){
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name),Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("recording",state);
            editor.apply();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static boolean getRecordingState(Context context){
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name),Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean("recording", false);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public static boolean isNotificationVisible(Context context,int id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        StatusBarNotification[] statusBarNotifications = notificationManager.getActiveNotifications();
        if(statusBarNotifications != null) {
            for (StatusBarNotification statusBarNotification : statusBarNotifications) {
                if (statusBarNotification.getPackageName().equals(context.getPackageName()) && statusBarNotification.getId() == 11) {
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean isAndroid6(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    public static boolean checkPlayServices(Context context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    public static String getImeNumber(Context context){
        try {
            return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        }catch (SecurityException e){
            e.printStackTrace();
        }
        return "";
    }

    public static void saveMobileNo(String mobile, Context context){
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name),Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("mobileNo",mobile);
            editor.apply();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static String getMobileNo(Context context){
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name),Context.MODE_PRIVATE);
            return sharedPreferences.getString("mobileNo","");
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
