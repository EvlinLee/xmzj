package com.gxtc.huchuan.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gxtc.huchuan.base.ITabTitle;

import java.util.List;

/**
 * Created by Gubr on 2017/3/7.
 */

public class BaseTabLayoutPagerAdapter<F extends Fragment, T extends ITabTitle> extends FragmentPagerAdapter {
    private List<T> titls;
    private List<F> fragments;

    public BaseTabLayoutPagerAdapter(FragmentManager fm, List<F> fragments, List<T> titls) {
        super(fm);
        this.fragments = fragments;
        this.titls = titls;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments==null?0:fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titls!=null) return titls.get(position).getTitle();
        return super.getPageTitle(position);
    }
}
