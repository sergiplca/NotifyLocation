package com.sergi.notifylocation.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sergi.notifylocation.Fragments.EventsFragment;
import com.sergi.notifylocation.Fragments.LocationFragment;
import com.sergi.notifylocation.Fragments.LocationInfoFragment;

/**
 * Created by Sergi on 14/03/2017.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private int NUM_ITEMS = 3;
    private double latitude;
    private double longitude;
    private String name;
    private String address;
    private String phone;

    public PagerAdapter(FragmentManager fragmentManager, double latitude, double longitude, String name,
                                                         String address, String phone) {
        super(fragmentManager);
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return LocationFragment.newInstance(latitude, longitude, name,
                                                    address, phone);
            case 1:
                return EventsFragment.newInstance();
            case 2:
                return LocationInfoFragment.newInstance(latitude, longitude, name,
                                                        address, phone);
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Map";
            case 1:
                return "Events";
            case 2:
                return "Social";
            default:
                return null;
        }
    }


}
