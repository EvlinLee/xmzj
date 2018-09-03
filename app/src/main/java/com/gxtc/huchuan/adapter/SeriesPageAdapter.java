package com.gxtc.huchuan.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Gubr on 2017/3/1.
 */

public class SeriesPageAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragments;
    private final String[] mTitles;

    public SeriesPageAdapter(FragmentManager fm, List<Fragment> fragments, String[]titles) {
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
        return mFragments==null?0:mFragments.size();
    }
}
