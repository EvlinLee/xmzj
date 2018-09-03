package com.gxtc.huchuan.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Steven on 17/3/21.
 */

public class ProfitPageAdapter extends FragmentPagerAdapter{

    private List<Fragment> fragments ;
    private String []       titles;

    public ProfitPageAdapter(FragmentManager fm, List<Fragment> fragments, String [] titles) {
        super(fm);
        this.fragments = fragments ;
        this.titles = titles ;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles [position];
    }

}
