package com.gxtc.huchuan.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/16 .
 */

public class ImcomeTopAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;
    private String[] mTitle;
    public ImcomeTopAdapter(FragmentManager fm,List<Fragment> mFragments,String[] mTitle) {
        super(fm);
        this.mFragments = mFragments;
        this.mTitle = mTitle;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle[position];
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }
}
