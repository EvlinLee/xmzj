package com.gxtc.huchuan.ui.deal.liuliang.dispatchOrder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.flyco.tablayout.SlidingTabLayout;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.OrderPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 文案派单
 */
public class DispatchOrderActivity extends BaseTitleActivity {

    @BindView(R.id.stl_order)           SlidingTabLayout    tabView;
    @BindView(R.id.vp_order)            ViewPager           viewPager;

    private String titles [] = {"全部","未设置","待推广","推广中","取消"};

    private OrderPagerAdapter   adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_order);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_dispatch_order));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getBaseHeadView().showHeadRightImageButton(R.drawable.icon_add, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GotoUtil.goToActivity(DispatchOrderActivity.this,PublishExtendActivity.class);
            }
        });
    }


    @Override
    public void initData() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new CopywritingFragment());
        fragments.add(new CopywritingFragment());
        fragments.add(new CopywritingFragment());
        fragments.add(new CopywritingFragment());
        fragments.add(new CopywritingFragment());
        adapter = new OrderPagerAdapter(getSupportFragmentManager(),fragments,titles);
        viewPager.setAdapter(adapter);
        tabView.setViewPager(viewPager);
    }


}
