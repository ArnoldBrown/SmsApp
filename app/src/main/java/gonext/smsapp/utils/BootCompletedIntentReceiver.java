package gonext.smsapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import gonext.smsapp.servers.BackgroundJob;

/**
 * Created by ram on 21/10/17.
 */

public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            context.startService(new Intent(context, BackgroundJob.class)); //start service which is BackgroundJob.java
        }
    }
}
