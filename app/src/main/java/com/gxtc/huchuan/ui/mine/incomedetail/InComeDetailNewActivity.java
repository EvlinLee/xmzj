package com.gxtc.huchuan.ui.mine.incomedetail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.flyco.tablayout.SlidingTabLayout;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.MineTabAdpter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;


/**
 * Describe:
 * Created by ALing on 2017/5/19.
 * 收益明细页面
 */

public class InComeDetailNewActivity extends BaseTitleActivity implements View.OnClickListener {
    @BindView(R.id.tabLayout_main) SlidingTabLayout mTabLayoutMain;
    @BindView(R.id.vp_mine)        ViewPager        mVpMine;

    private List<Fragment> fragments;
    private String[] arrTabTitles;
    int indext = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_detail_new);
    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.titlle_income_detail));
        getBaseHeadView().showBackButton(this);
    }

    @Override
    public void initData() {
        super.initData();
        indext = getIntent().getIntExtra("indext",0);
        initViewPager();
    }

    private void initViewPager() {
        arrTabTitles = getResources().getStringArray(R.array.mine_income_detail1);
        fragments = new ArrayList<>();
        for (int i = arrTabTitles.length - 1; i >= 0 ; i --){
            InComeDetailFragment fragment = new InComeDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.INTENT_DATA,i);
            bundle.putString("added","1");
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }

        mVpMine.setAdapter(new MineTabAdpter(getSupportFragmentManager(), fragments, arrTabTitles));
        mTabLayoutMain.setViewPager(mVpMine);
        mVpMine.setCurrentItem(indext,true);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTabLayoutMain.getMsgView(
                4).getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.rightMargin = WindowUtil.dip2px(this, 10);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;

        }
    }
}
