package com.gxtc.huchuan.ui.circle.classroom.classroomaudit;

import android.content.Context;
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
 * Created by Gubr on 2017/6/13.
 * 圈子课堂审核
 */

public class ClassRoomAuditActivity extends BaseTitleActivity implements View.OnClickListener {
    private static final String TAG = "ClassRoomAuditActivity";
    @BindView(R.id.tl_fileaudit_page_indicator) TabLayout mTlFileauditPageIndicator;
    @BindView(R.id.vp_fileaudit_viewpager)      ViewPager mVpFileauditViewpager;

    private static String[]       labTitles  = {"已审核", "未审核"};
    private        List<Fragment> mFragments = new ArrayList<>();
    private int                      mCircleid;
    private String                   mUserCode;
    private ClassRoomAuditedFragment mClassRoomAuditedFragment;
    private ClassRoomAuditFragment   mClassRoomAuditFragment;
    private FileAuditPageAdapter     mPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroomaudit);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("课程管理");
        getBaseHeadView().showBackButton(this);
    }

    @Override
    public void initData() {
        mCircleid = getIntent().getIntExtra("circleid", 0);
        mUserCode = getIntent().getStringExtra("userCode");
        initFragment();
    }

    private void initFragment() {
        mFragments.clear();
        mClassRoomAuditedFragment = new ClassRoomAuditedFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("circleid", mCircleid);
        bundle.putString("userCode", mUserCode);
        mClassRoomAuditedFragment.setArguments(bundle);


        mClassRoomAuditFragment = new ClassRoomAuditFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt("circleid", mCircleid);
        bundle1.putString("userCode", mUserCode);
        mClassRoomAuditFragment.setArguments(bundle1);

        mFragments.add(mClassRoomAuditedFragment);
        mFragments.add(mClassRoomAuditFragment);
        mPageAdapter = new FileAuditPageAdapter(getSupportFragmentManager(), mFragments, labTitles);
        mVpFileauditViewpager.setAdapter(mPageAdapter);
        mTlFileauditPageIndicator.setupWithViewPager(mVpFileauditViewpager);
    }


    public void upDateAuditedClassRoom() {
        if (mClassRoomAuditedFragment != null) {
            mClassRoomAuditedFragment.getDatas();
        }
    }

    public static void startActivity(Context context, int circcleId, String userCode) {
        Intent intent = new Intent(context, ClassRoomAuditActivity.class);
        intent.putExtra("circleid", circcleId);
        intent.putExtra("userCode", userCode);
        context.startActivity(intent);
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
