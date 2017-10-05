package gonext.smsapp.servers;

import android.telecom.Call;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ram on 14/09/17.
 */

public class SmsCallback implements Callback<JsonObject>{
    private int req = 0;

    public SmsCallback(int req) {
        this.req = req;
    }

    @Override
    public void success(JsonObject o, Response response) {
        System.out.println(response);
    }

    @Override
    public void failure(RetrofitError error) {
        System.out.println(error);
        error.printStackTrace();
    }
}
