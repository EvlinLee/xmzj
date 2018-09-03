package com.gxtc.huchuan.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjr on 2017/3/10.
 * 系列课数据适配器
 */

public class SeresCourseItemAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Fragment> container;
    private static List<String> data = new ArrayList<>();

    public SeresCourseItemAdapter(FragmentManager fm, ArrayList<Fragment> container, List<String> data) {
        super(fm);
        this.container = container;
        this.data = data;
    }

    @Override
    public Fragment getItem(int position) {
        return container == null ? null : container.get(position);
    }

    @Override
    public int getCount() {
        return container == null ? 0 : container.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return data.get(position);
    }
}

