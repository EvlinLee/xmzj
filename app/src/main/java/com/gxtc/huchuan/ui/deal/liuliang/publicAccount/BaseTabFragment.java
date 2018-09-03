package com.gxtc.huchuan.ui.deal.liuliang.publicAccount;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.TabPagerAdapter;
import com.gxtc.huchuan.widget.CustomViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 封装好的tab + fragment + viewpager布局
 * Created by Steven on 17/2/21.
 */

public abstract class BaseTabFragment extends BaseTitleFragment{

    @BindView(R.id.vp_account)      CustomViewPager viewPager;

    private CommonTabLayout tabLayout;

    private TabPagerAdapter adapter;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.base_tab_layout,container,false);
        tabLayout = (CommonTabLayout) view.findViewById(R.id.common_tab_layout);
        configTabLayout(tabLayout);
        return view;
    }

    @Override
    public void initListener() {
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {}
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                tabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override
    public void initData() {
        adapter = new TabPagerAdapter(getChildFragmentManager(),getTabBeans(),getFragments());
        viewPager.setAdapter(adapter);

        tabLayout.setTabData(getTabBeans());

    }


    /**
     * 在这个方法设置tabLayou 的一些配置
     * @param tabLayout
     */
    public void configTabLayout(CommonTabLayout tabLayout){}


    /**
     * @return 在这个方法返回要显示的tab标题
     */
    public abstract ArrayList<CustomTabEntity> getTabBeans();


    /**
     * @return 在这个方法返回要显示在viewpager的fragment
     */
    public abstract List<Fragment> getFragments();
}
