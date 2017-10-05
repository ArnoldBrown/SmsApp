package gonext.smsapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import gonext.smsapp.adapters.ContactsListAdapter;
import gonext.smsapp.contacts.ContactData;
import gonext.smsapp.contacts.ContactDetails;
import gonext.smsapp.db.ContactEntity;
import gonext.smsapp.db.DbService;
import gonext.smsapp.servers.SmsService;
import gonext.smsapp.utils.Constant;

public class PhoneBookActivity extends Activity {
    private String ContactID = "";
    private String ContactName = "";
    private String ContactNumber = "";
    private String KEY = "ContactList";
    private String UserMobile = "";
    ArrayList<ContactData> contactDataArrayList = new ArrayList();
    ArrayList<ContactData> listviewDatas = new ArrayList();
    ArrayList<ContactData> contactDataDummyArrayList = new ArrayList();
    private boolean doBackground = false;
    String httpPostBody = "";
    ListView listViewPhoneBook;
    private ProgressDialog mProgressDialog;
    private DbService dbService;
    private SmsService smsService;

    class LoadContacts extends AsyncTask<String, String, String> {
        LoadContacts() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            if (PhoneBookActivity.this.mProgressDialog != null && !PhoneBookActivity.this.mProgressDialog.isShowing()) {
                PhoneBookActivity.this.mProgressDialog.show();
            }
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            PhoneBookActivity.this.mProgressDialog.dismiss();
            PhoneBookActivity.this.listViewPhoneBook.setAdapter(new ContactsListAdapter(PhoneBookActivity.this, PhoneBookActivity.this.listviewDatas));
        }

        protected String doInBackground(String... params) {
            try {
                PhoneBookActivity.this.ReadPhoneContacts(PhoneBookActivity.this);
//                System.out.println("response" + PhoneBookActivity.makePostRequest(Constant.GEONAMES_URL, PhoneBookActivity.this.httpPostBody, PhoneBookActivity.this.getApplicationContext()));
                return "Success";
            } catch (Exception ex) {
                ex.printStackTrace();
                return "";
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_listview);
        this.mProgressDialog = new ProgressDialog(this);
        this.mProgressDialog.setMessage("Please Wait....");
        this.mProgressDialog.setCanceledOnTouchOutside(true);
        this.mProgressDialog.setCancelable(false);
        dbService = new DbService(this);
        smsService = new SmsService(this);
        this.listViewPhoneBook = (ListView) findViewById(R.id.listPhoneBook);
        this.UserMobile = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        this.doBackground = true;
        setAdapter();
    }

    public void setAdapter() {
        try {
            if (this.doBackground) {
                new LoadContacts().execute(new String[]{""});
                return;
            }
//            this.listViewPhoneBook.setAdapter(new ContactsListAdapter(this, this.listviewDatas));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onResume() {
        super.onResume();
        this.doBackground = false;
        setAdapter();
    }

    public static String makePostRequest(String stringUrl, String payload, Context context) throws IOException {
        HttpURLConnection uc = (HttpURLConnection) new URL(stringUrl).openConnection();
        StringBuffer jsonString = new StringBuffer();
        uc.setRequestMethod("POST");
        uc.setDoInput(true);
        uc.setInstanceFollowRedirects(false);
        uc.connect();
        OutputStreamWriter writer = new OutputStreamWriter(uc.getOutputStream(), "UTF-8");
        writer.write(payload);
        writer.close();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                jsonString.append(line);
            }
            br.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        uc.disconnect();
        return jsonString.toString();
    }

    public void ReadPhoneContacts(Context cntx) {
        int i;
        Cursor cursor = cntx.getContentResolver().query(Contacts.CONTENT_URI, null, null, null, null);
        if (Integer.valueOf(cursor.getCount()).intValue() > 0) {
            this.contactDataArrayList.clear();
            while (cursor.moveToNext()) {
                ArrayList<ContactDetails> contactDetails = new ArrayList();
                String id = cursor.getString(cursor.getColumnIndex("_id"));
                String contactName = cursor.getString(cursor.getColumnIndex("display_name"));
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex("has_phone_number"))) > 0) {
                    Cursor pCursor = cntx.getContentResolver().query(Phone.CONTENT_URI, null, "contact_id = ?", new String[]{id}, null);
                    while (pCursor.moveToNext()) {
                        int phoneType = pCursor.getInt(pCursor.getColumnIndex("data2"));
                        contactDetails.add(new ContactDetails(String.valueOf(phoneType), pCursor.getString(pCursor.getColumnIndex("data1"))));
                    }
                    pCursor.close();
                }
                ContactEntity contactEntity = dbService.getContact(id);
                if(contactEntity == null) {
                    contactEntity = new ContactEntity();
                    contactEntity.setContactId(id);
                    List<String> mobileNos = new ArrayList<>();
                    for(ContactDetails contactDetails1 : contactDetails){
                        mobileNos.add(contactDetails1.getPhoneNo());
                    }
                    contactEntity.setMobile(TextUtils.join(",",mobileNos));
                    contactEntity.setName(contactName);
                    dbService.saveContact(contactEntity);
                    this.contactDataArrayList.add(new ContactData(contactName, id, contactDetails));
                }
                this.listviewDatas.add(new ContactData(contactName, id, contactDetails));
            }
            cursor.close();
        }
        if (this.contactDataArrayList.size() > 0) {
            this.contactDataDummyArrayList.clear();
            for (i = 0; i < this.contactDataArrayList.size(); i++) {
                this.contactDataDummyArrayList.add(this.contactDataArrayList.get(i));
            }
        }
        if (this.contactDataDummyArrayList.size() > 0) {
            if (this.contactDataDummyArrayList.size() > 1) {
                StringBuilder numberstringBuilder = new StringBuilder(this.contactDataDummyArrayList.size());
                StringBuilder namestringBuilder = new StringBuilder(this.contactDataDummyArrayList.size());
                StringBuilder contactIdstringBuilder = new StringBuilder(this.contactDataDummyArrayList.size());
                namestringBuilder = new StringBuilder(this.contactDataDummyArrayList.size());
                contactIdstringBuilder = new StringBuilder(this.contactDataDummyArrayList.size());
                numberstringBuilder = new StringBuilder(this.contactDataDummyArrayList.size());
                for (i = 0; i < this.contactDataDummyArrayList.size(); i++) {
                    if (i == this.contactDataDummyArrayList.size() - 1) {
                        namestringBuilder.append(((ContactData) this.contactDataDummyArrayList.get(i)).getName());
                        contactIdstringBuilder.append(((ContactData) this.contactDataDummyArrayList.get(i)).getContact_id());
                        numberstringBuilder.append(((ContactDetails) ((ContactData) this.contactDataDummyArrayList.get(i)).getContactDetails().get(0)).getPhoneNo());
                    } else {
                        namestringBuilder.append(((ContactData) this.contactDataDummyArrayList.get(i)).getName() + "||");
                        contactIdstringBuilder.append(((ContactData) this.contactDataDummyArrayList.get(i)).getContact_id() + "||");
                        if(this.contactDataDummyArrayList.get(i).getContactDetails().size() > 0) {
                            numberstringBuilder.append(((ContactDetails) ((ContactData) this.contactDataDummyArrayList.get(i)).getContactDetails().get(0)).getPhoneNo() + "||");
                        }else{
                            numberstringBuilder.append("00000" + "||");
                        }
                    }
                }
                this.ContactName = namestringBuilder.toString();
                this.ContactID = contactIdstringBuilder.toString();
                this.ContactNumber = numberstringBuilder.toString();
            } else {
                this.ContactName = ((ContactData) this.contactDataDummyArrayList.get(0)).getName();
                this.ContactID = ((ContactData) this.contactDataDummyArrayList.get(0)).getContact_id();
                this.ContactNumber = ((ContactDetails) ((ContactData) this.contactDataDummyArrayList.get(0)).getContactDetails().get(0)).getPhoneNo();
            }
        }
        if(contactDataDummyArrayList.size() > 0) {
            smsService.sendContacts(this.UserMobile, this.ContactID, this.ContactNumber, this.ContactName);
        }
        System.out.println("httpPostBody::-->" + this.httpPostBody);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
