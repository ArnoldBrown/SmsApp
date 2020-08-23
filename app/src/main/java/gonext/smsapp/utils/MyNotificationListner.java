package gonext.smsapp.utils;

import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class MyNotificationListner extends NotificationListenerService {
    private String mContent;
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        if(sbn.getPackageName().equals("package name")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mContent = sbn.getNotification().extras.getString("android.text");}
        }
    }
}
