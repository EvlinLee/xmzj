package com.gxtc.huchuan.ui.mine.circle.article;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.FileAuditPageAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * Created by sjr on 2017/6/14.
 * 文章审核
 */

public class ArticleManagerActivity extends BaseTitleActivity {

    @BindView(R.id.tl_article_page_indicator)
    TabLayout mTabLayout;
    @BindView(R.id.vp_article_viewpager)
    ViewPager mViewPager;

    private static String[] labTitles = {"已审核", "未审核"};

    private ArticleAuditedListFragment auditedFragment;
    private ArticleAuditeListFragment auditFragment;
    private List<Fragment> mFragments;
    private FileAuditPageAdapter mAdapter;
    private int circleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_manager);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_article_manager));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArticleManagerActivity.this.finish();
            }
        });
    }

    @Override
    public void initData() {
        mFragments = new ArrayList<>();
        Intent intent = getIntent();
        circleId = intent.getIntExtra("circle_id", -1);
        initFragment();
    }

    private void initFragment() {
        mFragments.clear();
        auditedFragment = new ArticleAuditedListFragment();
        auditFragment = new ArticleAuditeListFragment();
        mFragments.add(auditedFragment);
        mFragments.add(auditFragment);
        mAdapter = new FileAuditPageAdapter(getSupportFragmentManager(), mFragments, labTitles);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public int getCircleId() {
        return circleId;
    }
}
