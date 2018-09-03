package com.gxtc.huchuan.ui.live.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.BaseTabLayoutPagerAdapter;
import com.gxtc.huchuan.bean.LiveHeadTitleBean;
import com.gxtc.huchuan.widget.CustomViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gubr on 2017/2/27.
 */

public class LiveListActivity extends BaseTitleActivity implements View.OnClickListener {
    private final static String ID = "id";
    private final static String TITLE = "title";


    @BindView(R.id.tb_live_tab)
    TabLayout mTbLiveTab;
    @BindView(R.id.viewpager)
    CustomViewPager mViewpager;


    private LiveHeadTitleBean mBean;
    private List<LiveListFragment> mLiveListFragments;
    private BaseTabLayoutPagerAdapter<LiveListFragment, LiveHeadTitleBean.ChatTypeSonBean> mLiveListPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_list);
    }


    @Override
    public void initView() {
        mBean = (LiveHeadTitleBean) getIntent().getSerializableExtra("bean");
        String title = mBean.getTypeName();
        getBaseHeadView().showTitle(title);
        getBaseHeadView().showBackButton(this);
    }

    @Override
    public void initData() {
        mLiveListFragments = new ArrayList<>();
        for (LiveHeadTitleBean.ChatTypeSonBean bean : mBean.getChatTypeSon()) {
            mLiveListFragments.add(getLiveListFragment(bean));
        }

        mViewpager.setOffscreenPageLimit(mBean.getChatTypeSon().size()-1);
        mLiveListPagerAdapter = new BaseTabLayoutPagerAdapter<>(getSupportFragmentManager(), mLiveListFragments, mBean.getChatTypeSon());
        mViewpager.setAdapter(mLiveListPagerAdapter);
        mTbLiveTab.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTbLiveTab.setupWithViewPager(mViewpager);

    }

    @Override
    public void initListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;
        }
    }

    public static void startActivity(Context context, LiveHeadTitleBean bean) {
        Intent intent = new Intent(context, LiveListActivity.class);
        intent.putExtra("bean", bean);
        context.startActivity(intent);
    }


    public LiveListFragment getLiveListFragment(LiveHeadTitleBean.ChatTypeSonBean bean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", bean);
        LiveListFragment liveListFragment = new LiveListFragment();
        liveListFragment.setArguments(bundle);
        return liveListFragment;

    }
}
