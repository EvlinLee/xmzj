package com.gxtc.huchuan.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.huchuan.Constant;

import java.util.List;

public class CircleMainPageAdapter extends FragmentPagerAdapter {

    private int id;
    private String title [] ;
    private List<BaseTitleFragment> fragments;

    public CircleMainPageAdapter(FragmentManager fm,List<BaseTitleFragment> fragments , String title [],int id) {
        super(fm);
        this.fragments = fragments;
        this.title = title;
        this.id = id;
    }

    @Override
    public Fragment getItem(int position) {
        BaseTitleFragment fragment = fragments.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.INTENT_DATA,id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
