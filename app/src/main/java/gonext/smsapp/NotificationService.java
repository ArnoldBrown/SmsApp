package gonext.smsapp;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import gonext.smsapp.db.DbService;
import gonext.smsapp.db.NotificationEntity;
import gonext.smsapp.utils.Utils;


public class NotificationService extends NotificationListenerService {

    Context context;
    private DbService dbService;

    @Override

    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();
        dbService = new DbService(context);

    }
    @Override

    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            List<String> messages = new ArrayList<>();
                String pack = sbn.getPackageName();
                long postTime = sbn.getPostTime();
                Bundle extras = sbn.getNotification().extras;
                String title = extras.getString("android.title");
                if (pack.equals("com.whatsapp")){
                    title = "WhatsApp";
                }
                String text = extras.getCharSequence("android.text").toString();
            CharSequence[] lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
            if(lines == null){
                messages.add(pack+title+text);
            }else {
                for (CharSequence msg : lines) {
                    messages.add(pack+title+(String) msg);
                }
            }

            if(lines == null){
                if(messages.contains(pack+title+text)){
                    messages = removeFromList(messages,pack+title+text);
                    saveNotification(pack, title, text, postTime);
                }
            }else {
                for (CharSequence msg : lines) {
                    if(messages.contains(pack+title+(String) msg)) {
                        messages = removeFromList(messages,pack+title+(String) msg);
                        saveNotification(pack, title, (String) msg, postTime);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");

    }
    private void saveNotification(String pack,String title,String text,long postTime){
        try{
            String notificationDate = Utils.getNotificationTime(postTime);
        if(dbService.getNotification(pack,title,text) == null) {
            NotificationEntity notificationEntity = new NotificationEntity();
            notificationEntity.setTitle(title);
            notificationEntity.setMessage(text);
            notificationEntity.setPackageName(pack);
            notificationEntity.setDateTime(notificationDate);
            dbService.saveNotification(notificationEntity);

            Intent msgrcv = new Intent("Msg");
            msgrcv.putExtra("package", pack);
            msgrcv.putExtra("title", title);
            msgrcv.putExtra("text", text);
            LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private List<String> removeFromList(List<String> list,String txt){
        List<String> result = new ArrayList<>();
        for(String item : list){
            if(!item.equals(txt)){
                result.add(item);
            }
        }
        return result;
    }
}