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

import gonext.smsapp.db.ContactEntity;
import gonext.smsapp.db.DbService;
import gonext.smsapp.db.NotificationEntity;
import gonext.smsapp.servers.SmsService;
import gonext.smsapp.utils.Utils;


public class GooNotificationListener extends NotificationListenerService {

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
            if (pack.equals("com.whatsapp")) {
                long postTime = sbn.getPostTime();
                Bundle extras = sbn.getNotification().extras;
                String title = extras.getString("android.title");
                String key = sbn.getKey();
                if(title.contains("@")){
                    title = title.substring(0,title.indexOf("@")-1);
                    title = title.trim();
                    title = getContactNumber(title);
                }else if(title.contains("(") && title.endsWith("messages)")){
                    title = title.substring(0,title.indexOf("(")-1);
                    title = title.trim();
                    title = getContactNumber(title);
                }
                else{
                    title = getContactNumber(title);
                }
                String text = extras.getCharSequence("android.text").toString();
                CharSequence[] lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
                if (lines == null) {
                    messages.add(key + text + postTime);
                } else {
                    for (CharSequence msg : lines) {
                        messages.add(key + (String) msg + postTime);
                    }
                }

                if (lines == null) {
                    if (messages.contains(key + text + postTime)) {
                        messages = removeFromList(messages, pack + title + text);
                        saveNotification(pack, title, text, postTime,key);
                    }
                } else {
                    for (CharSequence msg : lines) {
                        if (messages.contains(key + (String) msg + postTime)) {
                            messages = removeFromList(messages, pack + title + (String) msg);
                            saveNotification(pack, title, (String) msg, postTime,key);
                        }
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
    private void saveNotification(String pack,String title,String text,long postTime,String key){
        try{
            String notificationDate = Utils.getNotificationTime(postTime);
        if(dbService.getNotification(title,text,String.valueOf(postTime)) == null) {
            NotificationEntity notificationEntity = new NotificationEntity();
            notificationEntity.setTitle(title);
            notificationEntity.setMessage(text);
            notificationEntity.setFromNumber(title);
            notificationEntity.setKey(String.valueOf(postTime));
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
    private String getContactNumber(String name){
        List<ContactEntity> contactEntities = dbService.getContacts();
        for(ContactEntity contactEntity : contactEntities){
            if(contactEntity.getName().equals(name)){
                return contactEntity.getMobile().contains(",") ? contactEntity.getMobile().split(",")[0] : contactEntity.getMobile();
            }
        }
        return name;
    }
}