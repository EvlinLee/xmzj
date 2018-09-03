package com.gxtc.huchuan.ui.news;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.utils.RomUtils;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ChannelPagerAdapter;
import com.gxtc.huchuan.adapter.CirclePagerAdapter;
import com.gxtc.huchuan.ui.deal.deal.DealFragment;
import com.gxtc.huchuan.ui.live.search.NewSearchActivity;
import com.gxtc.huchuan.ui.special.ArticleSpecialListFragment;
import com.gxtc.huchuan.widget.MPagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 来自 苏修伟 on 2018/5/5.
 */
public class MainNewsFragment extends BaseTitleFragment implements View.OnClickListener {


    @BindView(R.id.rl_head)
    RelativeLayout head;
    @BindView(R.id.news_tab)
    MPagerSlidingTabStrip tab;
    @BindView(R.id.iv_search)
    ImageView search;
    @BindView(R.id.vp_newpage)
    ViewPager page;

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private List<String> titledata = new ArrayList<>();
    private ChannelPagerAdapter mAdapter;

    private DealFragment dealFragment;         //交易
    private NewsItemFragment recommend;           //推荐
    private NewsFragment article;          //文章
    private VideoNewsFragment videofragment;         //视频

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_main_news, container, false);
    }

    @Override
    public void initData() {

        setActionBarTopPadding(head);

        titledata.add("推荐");
        titledata.add("文章");
        titledata.add("视频");
        titledata.add("交易");
        Bundle bundle = new Bundle();
        bundle.putString("name", "推荐");
        bundle.putInt("id", -1);
        bundle.putInt("news_type", 1);
        bundle.putString("userCode", "");
        recommend = new NewsItemFragment();
        recommend.setArguments(bundle);

        dealFragment = new DealFragment();
        Bundle dealBulde = new Bundle();
        dealBulde.putInt("showBar", 1);
        dealFragment.setArguments(dealBulde);

        article = new NewsFragment();
        videofragment = new VideoNewsFragment();

        fragments.add(recommend);
        fragments.add(article);
        fragments.add(videofragment);
        fragments.add(dealFragment);

        mAdapter = new ChannelPagerAdapter(getChildFragmentManager(), fragments, titledata);
        page.setOffscreenPageLimit(3);
        page.setAdapter(mAdapter);
        tab.setViewPager(page);

    }

    @Override
    public void initListener() {
        super.initListener();
    }

    @OnClick({R.id.iv_search})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search:
                NewSearchActivity.jumpToSearch(getContext(), "1");
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
