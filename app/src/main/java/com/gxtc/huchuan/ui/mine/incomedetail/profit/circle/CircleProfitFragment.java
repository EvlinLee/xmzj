package com.gxtc.huchuan.ui.mine.incomedetail.profit.circle;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.flyco.tablayout.SlidingTabLayout;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.MineTabAdpter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Describe:
 * Created by ALing on 2017/5/16 .
 */

public class CircleProfitFragment extends BaseTitleFragment {
    @BindView(R.id.tabLayout_main)
    SlidingTabLayout mTabLayoutMain;
    @BindView(R.id.vp_mian)
    ViewPager mVpMian;

    private List<Fragment> fragments;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_profit_list, container, false);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        initViewPager();
    }

    private void initViewPager() {
        String[] arrTabTitles = getResources().getStringArray(R.array.mine_profit_tab_circle);
        fragments = new ArrayList<>();
        CircleDistributionFragment circleDistributionFragment   =   new CircleDistributionFragment();
        CircleProfitListFragment circleProfitListFragment       =    new CircleProfitListFragment();

        fragments.add(circleProfitListFragment);
        fragments.add(circleDistributionFragment);

        mVpMian.setAdapter(new MineTabAdpter(getChildFragmentManager(), fragments, arrTabTitles));
        mTabLayoutMain.setViewPager(mVpMian);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTabLayoutMain.getMsgView(
                4).getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.rightMargin = WindowUtil.dip2px(getContext(), 10);
    }
}
