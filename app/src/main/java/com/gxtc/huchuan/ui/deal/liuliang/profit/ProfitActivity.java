package com.gxtc.huchuan.ui.deal.liuliang.profit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.flyco.tablayout.SlidingTabLayout;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ProfitPageAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 接单盈利
 */
public class ProfitActivity extends BaseTitleActivity {

    @BindView(R.id.stl_profit)          SlidingTabLayout    tabView;
    @BindView(R.id.vp_profit)           ViewPager           viewPager;

    private String titles [] = {"全部","未设置","接单中"};

    private ProfitPageAdapter   adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profit);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_profit));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getBaseHeadView().showHeadRightImageButton(R.drawable.icon_add, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void initData() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new ProfitFragment());
        fragments.add(new ProfitFragment());
        fragments.add(new ProfitFragment());
        adapter = new ProfitPageAdapter(getSupportFragmentManager(),fragments,titles);
        viewPager.setAdapter(adapter);
        tabView.setViewPager(viewPager);
    }
}
