package gonext.smsapp;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import gonext.smsapp.fragments.MobileNetworkFragment;
import gonext.smsapp.fragments.WifiFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    final int PAGE_COUNT = 2;
    // Tab Titles
    private String tabtitles[];
    private WifiFragment wifiFragment;
    private MobileNetworkFragment mobileNetworkFragment;

    public PagerAdapter(FragmentManager fm, Context context, WifiFragment wifiFragment, MobileNetworkFragment mobileNetworkFragment) {
        super(fm);
        tabtitles = new String[] { "WIFI", "MobileNetworks" };
        this.wifiFragment = wifiFragment;
        this.mobileNetworkFragment = mobileNetworkFragment;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            // Open FragmentTab1.java
            case 0:
                return wifiFragment;

            // Open FragmentTab2.java
            case 1:
                return mobileNetworkFragment;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }

}