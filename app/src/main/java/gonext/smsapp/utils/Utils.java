package gonext.smsapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

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
}
