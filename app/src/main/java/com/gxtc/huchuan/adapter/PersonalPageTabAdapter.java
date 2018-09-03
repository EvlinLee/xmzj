package com.gxtc.huchuan.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.gxtc.huchuan.Constant;

import java.util.List;

/**
 * Describe:个人主页适配器
 * Created by ALing on 2017/4/5 .
 */

public class PersonalPageTabAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;
    private String[] tabTitle;

    private String userCode;

    public PersonalPageTabAdapter(FragmentManager fm, List<Fragment> fragments, String[] tabTitle,String userCode) {
        super(fm);
        this.fragments = fragments;
        this.tabTitle = tabTitle;
        this.userCode = userCode;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragments.get(position);
        Bundle bundle = new Bundle();
        bundle.putString(Constant.INTENT_DATA,userCode);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitle[position];
    }
}
