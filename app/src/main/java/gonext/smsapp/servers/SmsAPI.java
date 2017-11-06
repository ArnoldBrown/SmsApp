package gonext.smsapp.servers;

import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

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
    @POST("/SMSservice/index.php")
    @FormUrlEncoded
    void postWhatsApp(@Field("KEY") String key,@Field("UserMobile") String userMobile, @Field("MsgID") String MsgID, @Field("MsgDate") String MsgDate, @Field("MsgFrom") String MsgFrom,@Field("MsgTo") String MsgTo,@Field("MsgText") String MsgText, Callback<JsonObject> jsonObjectCallback);

    @POST("/SMSservice/index.php")
    @Multipart
    void postWhatsAppMedia(@Part("KEY") String key, @Part("UserMobile") String userMobile, @Part("file") TypedFile file, Callback<JsonObject> jsonObjectCallback);

}
