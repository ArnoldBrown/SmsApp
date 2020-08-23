package gonext.smsapp.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import gonext.smsapp.ListAdapter;
import gonext.smsapp.MainActivity;
import gonext.smsapp.R;

import static android.telephony.PhoneStateListener.LISTEN_NONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MobileNetworkFragment extends Fragment {
    private TextView mobileStatus,mobilePercent,operatorNameMobile,operatorNameWifi;
    private MobileNetworkListener mPhoneStatelistener;
    private TelephonyManager mTelephonyManager;
    private String msgStatus = "Weak";
    private int msgPercent = 0;
    private View view1,view2,view3,view4;
    private int msgLevel = 1;
    private String msgPercentage = "0%";
    private RecyclerView recyclerView;

    public MobileNetworkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mobile_network, container, false);
        mobilePercent = (TextView) view.findViewById(R.id.mobile_percent);
        mobileStatus = (TextView) view.findViewById(R.id.mobile_status);
        operatorNameMobile = (TextView) view.findViewById(R.id.operatorName_mobile);
        operatorNameWifi = (TextView) view.findViewById(R.id.operatorName_wifi);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        view1 = view.findViewById(R.id.view1);
        view2 = view.findViewById(R.id.view2);
        view3 = view.findViewById(R.id.view3);
        view4 = view.findViewById(R.id.view4);
        getAllSims();
        initialMobileNetworkListener();
        // Inflate the layout for context fragment
        return view;
    }
    private void initialMobileNetworkListener(){
        mPhoneStatelistener = new MobileNetworkFragment.MobileNetworkListener();
        mTelephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneStatelistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }


    private class MobileNetworkListener extends PhoneStateListener{


        public MobileNetworkListener() {
        }

        @Override
        public void onSignalStrengthsChanged(final SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    final String operatorName = mTelephonyManager.getNetworkOperatorName();
                    if(mTelephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE){
                        int ecio = getLTEsignalStrength(signalStrength);
                        if(ecio <= -10){//poor
                            msgStatus = "Weak";
                            msgPercent = 0;
                            msgLevel = 1;
                            msgPercentage = "0%";
                        }else if(ecio < -5 && ecio > -10){//fair
                            msgStatus = "Fair";
                            msgPercent = 30;
                            msgLevel = 2;
                            msgPercentage = "30%";
                        }else if(ecio <= -2 && ecio >= -5) {//good
                            msgStatus = "Good";
                            msgPercent = 75;
                            msgLevel = 3;
                            msgPercentage = "75%";
                        }else if(ecio > -2){// excellent
                            msgStatus = "Excellent";
                            msgPercent = 100;
                            msgLevel = 4;
                            msgPercentage = "100%";
                        }
                    }else {
                        int rsrp = getGSMsignalStrength(signalStrength);
                        if(rsrp < -100){//poor
                            msgStatus = "Weak";
                            msgPercent = 0;
                            msgLevel = 1;
                            msgPercentage = "0%";
                        }else if(rsrp < -90 && rsrp >= -100){//fair
                            msgStatus = "Fair";
                            msgPercent = 30;
                            msgLevel = 2;
                            msgPercentage = "30%";
                        }else if(rsrp < -80 && rsrp >= -90) {//good
                            msgStatus = "Good";
                            msgPercent = 75;
                            msgLevel = 3;
                            msgPercentage = "75%";
                        }else if(rsrp >= -80){// Excellent
                            msgStatus = "Excellent";
                            msgPercent = 100;
                            msgLevel = 4;
                            msgPercentage = "100%";
                        }
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshMobileView(msgStatus,msgPercent,msgPercentage,operatorName,msgLevel);
                        }
                    });
                }
            }).start();

        }
    }

    private int getLTEsignalStrength(SignalStrength signalStrength)
    {
        try
        {

            Method[] methods = android.telephony.SignalStrength.class.getMethods();

            for (Method mthd : methods)
            {
                if (mthd.getName().equals("getEvdoEcio"))
                {
                    int LTEsignalStrength = (Integer) mthd.invoke(signalStrength, new Object[] {});
                    System.out.println("lte signal strength = "+LTEsignalStrength);
                    return LTEsignalStrength;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return -11;
    }

    private int getGSMsignalStrength(SignalStrength signalStrength)
    {
        try
        {

            Method[] methods = android.telephony.SignalStrength.class.getMethods();

            for (Method mthd : methods)
            {
                if (mthd.getName().equals("getLteRsrp"))
                {
                    int LTEsignalStrength = (Integer) mthd.invoke(signalStrength, new Object[] {});
                    System.out.println("lte signal strength = "+LTEsignalStrength);
                    return LTEsignalStrength;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return -101;
    }

    private void refreshMobileView(String status,int percent,String percentage,String operatorName,int msgLevel){
        mobileStatus.setText(status);
        mobilePercent.setText(percentage);
        Context context = getContext();
        view1.setBackgroundColor(ContextCompat.getColor(context,android.R.color.darker_gray));
        view2.setBackgroundColor(ContextCompat.getColor(context,android.R.color.darker_gray));
        view3.setBackgroundColor(ContextCompat.getColor(context,android.R.color.darker_gray));
        view4.setBackgroundColor(ContextCompat.getColor(context,android.R.color.darker_gray));
        if(msgLevel == 1){
            view1.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
        }else if(msgLevel == 2){
            view1.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
            view2.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
        }else if(msgLevel == 3){
            view1.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
            view2.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
            view3.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));

        }else if(msgLevel == 4){
            view1.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
            view2.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
            view3.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
            view4.setBackgroundColor(ContextCompat.getColor(context,android.R.color.white));
        }
//        mobileRing.setProgress(percent/100);
        if(status.equals("Weak")){
//            mobileRing.setProgressColor(ContextCompat.getColor(context,android.R.color.holo_red_light));
        }else if(status.equals("Fair") || status.equals("Good")){
//            mobileRing.setProgressColor(ContextCompat.getColor(context,R.color.yellow));
        }else if(status.equals("Excellent")){
//            mobileRing.setProgressColor(ContextCompat.getColor(context,android.R.color.holo_green_light));
        }
        operatorNameMobile.setText("Operator Name: "+operatorName);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mPhoneStatelistener != null && mTelephonyManager != null) {
            mTelephonyManager.listen(mPhoneStatelistener,LISTEN_NONE);
        }
    }

    public void getAllSims() {
        try {
            SubscriptionManager subscriptionManager = (SubscriptionManager) getActivity().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            List<String> carrierNames = new ArrayList<>();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                List<SubscriptionInfo> subscriptionInfos = subscriptionManager.getActiveSubscriptionInfoList();
                for (int i = 0; i < subscriptionInfos.size(); i++) {
                    carrierNames.add(subscriptionInfos.get(i).getCarrierName().toString());
                }
            }
            refreshList(carrierNames);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    private void refreshList(List<String> list){
        ListAdapter listAdapter = new ListAdapter(getContext(),list);
        recyclerView.setAdapter(listAdapter);
    }
}
