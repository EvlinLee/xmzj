package com.gxtc.huchuan.ui.deal.liuliang.publicAccount;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.AccountPageAdapter;
import com.gxtc.huchuan.ui.deal.liuliang.publicAccount.MsgAnalyse.MsgAnalyseFragment;
import com.gxtc.huchuan.ui.deal.liuliang.publicAccount.TextAnalyse.TextTabFragment;
import com.gxtc.huchuan.ui.deal.liuliang.publicAccount.UserAnalyse.UserTabFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 *  公众号信息
 */
public class AccountInfoActivity extends BaseTitleActivity {

    @BindView(R.id.vp_account)          ViewPager           viewPager;

    private String titles [] = {"用户分析","图文分析","消息分析"};

    private SegmentTabLayout    tabLayout;

    private MsgAnalyseFragment mFragment;
    private TextTabFragment tFragment;
    private UserTabFragment uFragment;

    private AccountPageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
    }

    @Override
    public void initView() {
        View head = LayoutInflater.from(this).inflate(R.layout.head_account, (ViewGroup) getBaseHeadView().getParentView(), false);
        tabLayout = (SegmentTabLayout)head.findViewById(R.id.stl_account);
        ((RelativeLayout) getBaseHeadView().getParentView()).addView(head);

        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initListener() {
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void initData() {
        mFragment = new MsgAnalyseFragment();
        tFragment = new TextTabFragment();
        uFragment = new UserTabFragment();

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(uFragment);
        fragments.add(tFragment);
        fragments.add(mFragment);

        adapter = new AccountPageAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setTabData(titles);
    }
}
