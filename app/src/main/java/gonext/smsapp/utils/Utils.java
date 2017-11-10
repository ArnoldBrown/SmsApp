package gonext.smsapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;

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
}
