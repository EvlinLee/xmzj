package com.gxtc.huchuan.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.flyco.tablayout.listener.CustomTabEntity;

import java.util.List;

/**
 * Created by Steven on 17/2/21.
 */

public class TabPagerAdapter extends FragmentPagerAdapter{

    private List<CustomTabEntity> tabs;
    private List<Fragment>        fragments;

    public TabPagerAdapter(FragmentManager fm,List<CustomTabEntity> tabs,List<Fragment> fragments) {
        super(fm);
        this.tabs = tabs;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

}
