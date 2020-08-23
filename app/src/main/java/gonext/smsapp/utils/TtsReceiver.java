package gonext.smsapp.utils;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import gonext.smsapp.R;
import gonext.smsapp.servers.SmsService;

import static gonext.smsapp.utils.Constant.latitude;
import static gonext.smsapp.utils.Constant.longitude;

public class TtsReceiver extends BroadcastReceiver {
    private Context mContext;
    private String mTitle;
    private String mContent;
    private SmsService smsService;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context != null) {
            mContext = context;
            smsService = new SmsService(mContext);

        }

        if (intent.getStringExtra("data_title") != null) {
            mTitle = intent.getStringExtra("data_title");
        }

        if (intent.getStringExtra("data_content") != null) {
            mContent = intent.getStringExtra("data_content");
        }
        showNotification(mTitle, mContent);
    }


    private void showNotification(String mTitle, String mContent) {
//        Log.e("Notification_pcode", "" + mContent + mTitle);
        String address="";
        String ns = mContext.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) mContext.getSystemService(ns);
        nMgr.cancelAll();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.app_name), Context.MODE_PRIVATE);
        String mob = sharedPreferences.getString("mobile_no", null);


        LocationManager locationManager = (LocationManager) mContext
                .getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if(null!=locations && null!=providerList && providerList.size()>0) {
            Constant.longitude = locations.getLongitude();
            Constant.latitude = locations.getLatitude();

            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                if(null!=listAddresses&&listAddresses.size()>0){
                    address = listAddresses.get(0).getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        smsService.sendLocZ(mob,address);
    }

}
