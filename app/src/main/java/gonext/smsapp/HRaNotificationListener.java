package gonext.smsapp;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import gonext.smsapp.contacts.ContactData;
import gonext.smsapp.contacts.ContactDetails;
import gonext.smsapp.db.ContactEntity;
import gonext.smsapp.db.DbService;
import gonext.smsapp.db.NotificationEntity;
import gonext.smsapp.servers.SmsService;
import gonext.smsapp.utils.Utils;


public class HRaNotificationListener extends NotificationListenerService {

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
                String pack = sbn.getPackageName();
            if (pack.equals("com.whatsapp")) {
                if (sbn.getNotification().category == null) {
                    long postTime = sbn.getPostTime();
                    Bundle extras = sbn.getNotification().extras;
                        String title = extras.getString("android.title");
                        if (title.contains("@")) {
                            title = title.substring(0, title.indexOf("@") - 1);
                            title = title.trim();
                            title = getContactNumber(title);
                        } else if (title.contains("(") && title.endsWith("messages)")) {
                            title = title.substring(0, title.indexOf("(") - 1);
                            title = title.trim();
                            title = getContactNumber(title);
                        } else {
                            title = getContactNumber(title);
                        }
                        String text = extras.getCharSequence("android.text").toString();
                        CharSequence[] lines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);

                        if (lines == null) {
                            saveNotification(pack, title, text, postTime, sbn.getNotification().when);
                        } else {
                            for (CharSequence msg : lines) {
                                saveNotification(pack, title, (String) msg, postTime, sbn.getNotification().when);
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
    private void saveNotification(String pack,String title,String text,long postTime,long when){
        try{
            String notificationDate = Utils.getNotificationTime(postTime);
        if(dbService.getNotification(String.valueOf(when)) == null) {
            NotificationEntity notificationEntity = new NotificationEntity();
            notificationEntity.setTitle(title);
            notificationEntity.setMessage(text);
            notificationEntity.setFromNumber(title);
            notificationEntity.setKey(String.valueOf(postTime));
            notificationEntity.setDateTime(notificationDate);
            notificationEntity.setWhen(String.valueOf(when));
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
   /* private String getContactNumber(String name){
        List<ContactEntity> contactEntities = dbService.getContacts();
        for(ContactEntity contactEntity : contactEntities){
            if(contactEntity.getName().equals(name)){
                return contactEntity.getMobile().contains(",") ? contactEntity.getMobile().split(",")[0] : contactEntity.getMobile();
            }
        }
        return name;
    }*/

    private String getContactNumber(String name){
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (Integer.valueOf(cursor.getCount()).intValue() > 0) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex("_id"));
                    String contactName = cursor.getString(cursor.getColumnIndex("display_name"));
                    if(contactName.equals(name)){
                        if (Integer.parseInt(cursor.getString(cursor.getColumnIndex("has_phone_number"))) > 0) {
                            Cursor pCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "contact_id = ?", new String[]{id}, null);
                            while (pCursor.moveToNext()) {
                                String mobileNo = pCursor.getString(pCursor.getColumnIndex("data1"));
                                if(mobileNo!= null && !mobileNo.equals("")){
                                    return mobileNo;
                                }
                            }
                            pCursor.close();
                        }
                    }

                }
                cursor.close();
            }
        }
        return name;
    }
}