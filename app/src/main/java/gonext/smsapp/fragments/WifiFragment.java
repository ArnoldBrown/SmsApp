package gonext.smsapp.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gonext.smsapp.ListAdapter;
import gonext.smsapp.MainActivity;
import gonext.smsapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WifiFragment extends Fragment {
    private TextView wifiStatus,wifiPercent,operatorNameWifi;
    private BroadcastReceiver wifiReceiver;
    private View wview1,wview2,wview3,wview4;
    private int msgLevel = 1;
    private int wifiLevel = 1;
    private RecyclerView recyclerView;
    private WifiManager wifi;

    public WifiFragment() {
        // Required empty public constructor
    }

    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi, container, false);
        // Inflate the layout for context fragment
        wifiPercent = (TextView) view.findViewById(R.id.wifi_percent);
        wifiStatus = (TextView) view.findViewById(R.id.wifi_status);
        operatorNameWifi = (TextView) view.findViewById(R.id.operatorName_wifi);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        wview1 = view.findViewById(R.id.wview1);
        wview2 = view.findViewById(R.id.wview2);
        wview3 = view.findViewById(R.id.wview3);
        wview4 = view.findViewById(R.id.wview4);

        initializeWiFiListener();
        return view;
    }
    private void initializeWiFiListener(){
        String connectivity_context = Context.WIFI_SERVICE;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        wifi = (WifiManager) getActivity().getSystemService(connectivity_context);
        wifiReceiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                WifiInfo info = wifi.getConnectionInfo();
                String operatorName = info.getSSID();
                int rssi = info.getRssi();
                int level = WifiManager.calculateSignalLevel(rssi, 3);
//                int percentage = (int) ((level/10.0)*100);
                String status = "Weak";
                int percent = 0;
                wifiLevel = 1;
                String percentage = "0%";
                if(level == 0){//weak
                    if(rssi > -100) {
                        status = "Weak";
                        percent = 33;
                        percentage = "0-33%";
                        wifiLevel = 2;
                    }
                }else if(level == 1){//avg
                    status = "Average";
                    percent = 66;
                    wifiLevel = 3;
                    percentage = "33-66%";
                }else if(level == 2){//good
                    status = "Excellent";
                    percent = 100;
                    wifiLevel = 4;
                    percentage = "100%";
                }
                refreshWifiView(status,percent,percentage,operatorName,wifiLevel);
            }
            //TODO: implement methods for action handling

        };
        getContext().registerReceiver(wifiReceiver, intentFilter);
        refreshList();
    }
    public void refreshList(){
        List<String> wifis = new ArrayList<>();
            List<ScanResult> scanResults = wifi.getScanResults();
            for (ScanResult scanResult : scanResults) {
                wifis.add(scanResult.SSID);
            }
        ListAdapter listAdapter = new ListAdapter(getContext(),wifis);
        recyclerView.setAdapter(listAdapter);
    }
    private void refreshWifiView(String status,int percent,String percentage,String operatorName, int wifiLevel){
        wifiStatus.setText(status);
        wifiPercent.setText(percentage);
        Context context = getContext();
        wview1.setBackgroundColor(ContextCompat.getColor(context,android.R.color.darker_gray));
        wview2.setBackgroundColor(ContextCompat.getColor(context,android.R.color.darker_gray));
        wview3.setBackgroundColor(ContextCompat.getColor(context,android.R.color.darker_gray));
        wview4.setBackgroundColor(ContextCompat.getColor(context,android.R.color.darker_gray));
        if(msgLevel == 1){
            wview1.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
        }else if(msgLevel == 2){
            wview1.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
            wview2.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
        }else if(msgLevel == 3){
            wview1.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
            wview2.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
            wview3.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));

        }else if(msgLevel == 4){
            wview1.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
            wview2.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
            wview3.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
            wview4.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
        }

//        wifiRing.setProgress(percent/100);
        if(status.equals("Weak")){
//            wifiRing.setProgressColor(ContextCompat.getColor(context,android.R.color.holo_red_light));
        }else if(status.equals("Average")){
//            wifiRing.setProgressColor(ContextCompat.getColor(context,R.color.yellow));
        }else if(status.equals("Excellent")){
//            wifiRing.setProgressColor(ContextCompat.getColor(context,android.R.color.holo_green_light));
        }
        operatorNameWifi.setText("Provider Name: "+operatorName);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(wifiReceiver != null) {
            getActivity().unregisterReceiver(wifiReceiver);
        }
    }
}
