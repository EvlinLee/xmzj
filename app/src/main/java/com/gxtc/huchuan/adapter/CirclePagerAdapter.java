package com.gxtc.huchuan.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 来至  苏修伟  on 2018/5/2
 */

public class CirclePagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragments;
    private final String[] mTitles;

    public CirclePagerAdapter(FragmentManager fm, List<Fragment> fragments, String[]titles) {
        super(fm);
        mFragments = fragments;
        mTitles = titles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public Fragment getItem(int position) {

        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments == null? 0 : mFragments.size();
    }
}
