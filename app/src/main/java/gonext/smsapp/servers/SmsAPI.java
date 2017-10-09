package gonext.smsapp.servers;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by ram on 14/09/17.
 */

public interface SmsAPI {
    @POST("/SMSservice/index.php")
    @FormUrlEncoded
    void postContacts(@Field("KEY") String key,@Field("UserMobile") String userMobile, @Field("ContactID") String contactId, @Field("ContactNumber") String contactNumber, @Field("ContactName") String contactName, Callback<JsonObject> jsonObjectCallback);
    @POST("/SMSservice/index.php")
    @FormUrlEncoded
    void postMessages(@Field("KEY") String key,@Field("UserMobile") String userMobile, @Field("MsgID") String MsgID, @Field("MsgDate") String MsgDate, @Field("MsgFrom") String MsgFrom,@Field("MsgTo") String MsgTo,@Field("MsgText") String MsgText, Callback<JsonObject> jsonObjectCallback);
    @FormUrlEncoded
    void postWhatsApp(@Field("KEY") String key,@Field("UserMobile") String userMobile, @Field("MsgID") String MsgID, @Field("MsgDate") String MsgDate, @Field("MsgFrom") String MsgFrom,@Field("MsgTo") String MsgTo,@Field("MsgText") String MsgText, Callback<JsonObject> jsonObjectCallback);

}