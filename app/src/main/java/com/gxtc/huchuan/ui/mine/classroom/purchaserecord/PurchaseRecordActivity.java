package com.gxtc.huchuan.ui.mine.classroom.purchaserecord;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.flyco.tablayout.SlidingTabLayout;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.PurchaseRecordAdapter;
import com.gxtc.huchuan.ui.mall.MallOrderListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * Describe:我的 > 课堂 > 订单列表
 * Created by ALing on 2017/4/24 .
 */

public class PurchaseRecordActivity extends BaseTitleActivity {

    @BindView(R.id.tabLayout_main) SlidingTabLayout mTabLayoutMain;
    @BindView(R.id.vp_record)      ViewPager        mVpRecord;

    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_record);
    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_order_record));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initData() {
        initViewPager();
    }


    private void initViewPager() {
        String[] arrTabTitles = getResources().getStringArray(R.array.array_purchase_record);
        fragments = new ArrayList<>();
        AllOrderFragment seriesAndTopicFragment = new AllOrderFragment();
        Bundle SeriesAndTopic = new Bundle();
        SeriesAndTopic.putString("type", "6");
        seriesAndTopicFragment.setArguments(SeriesAndTopic);

        AllOrderFragment   circleRecordFragment   = new AllOrderFragment();
        Bundle Circle = new Bundle();
        Circle.putString("type", "3");
        circleRecordFragment.setArguments(Circle);

        AllOrderFragment     dealRecordFragment1    = new AllOrderFragment();
        Bundle deal = new Bundle();
        deal.putString("type", "4");
        dealRecordFragment1.setArguments(deal);

        AllOrderFragment  myCenterMallFragment   = new AllOrderFragment();
        Bundle Mall = new Bundle();
        Mall.putString("type", "5");
        myCenterMallFragment.setArguments(Mall);

        AllOrderFragment       allOrderFragment       = new AllOrderFragment();         //全部订单
        Bundle allOrder = new Bundle();
        allOrder.putString("type", "0");
        allOrderFragment.setArguments(allOrder);

        fragments.add(allOrderFragment);
        fragments.add(seriesAndTopicFragment);
        fragments.add(circleRecordFragment);
        fragments.add(dealRecordFragment1);
        fragments.add(myCenterMallFragment);

        PurchaseRecordAdapter recordAdapter = new PurchaseRecordAdapter(getSupportFragmentManager(), fragments, arrTabTitles);
        mVpRecord.setOffscreenPageLimit(4);
        mVpRecord.setAdapter(recordAdapter);
        mTabLayoutMain.setViewPager(mVpRecord);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTabLayoutMain.getMsgView(2).getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.rightMargin = WindowUtil.dip2px(this, 10);
    }

}
