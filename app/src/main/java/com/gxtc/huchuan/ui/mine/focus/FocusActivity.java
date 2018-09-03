package com.gxtc.huchuan.ui.mine.focus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.MineTabAdpter;
import com.gxtc.huchuan.bean.event.EventSelectFriendBean;
import com.gxtc.huchuan.im.ui.ConversationActivity;
import com.gxtc.huchuan.ui.circle.SearchConversationActivity;
import com.gxtc.huchuan.ui.live.search.NewSearchActivity;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.rong.imlib.model.Conversation;

/**
 * Describe: 关注列表
 * Created by ALing on 2017/3/13 0013.
 * 2017/4/6 需求更改，把推荐跟关注的抽出来 我的好友也抽出来 粉丝也单独抽出来
 * 暂定一个flag 1是推荐跟我订阅的自媒体 2是我的好友 3是我的粉丝  4,推荐   5,添加朋友
 */

public class FocusActivity extends BaseTitleActivity implements View.OnClickListener {

    @BindView(R.id.tablayout_focus) TabLayout mTabLayout;
    @BindView(R.id.vp_focus)        ViewPager mViewPager;

    private List<Fragment> fragments;

    private int isSelectFriends = -1;            //是否是选择好友模式
    private String focusFlag;
    private String targetId;
    private Conversation.ConversationType mConversationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);
        EventBusUtil.register(this);
    }

    @Override
    public void initView() {
        getBaseHeadView().showHeadRightImageButton(R.drawable.news_icon_search,this);
    }

    Bundle        bundle;
    FocusFragment mFocusFragment;

    @Override
    public void initData() {
        focusFlag = getIntent().getStringExtra("focus_flag");
        targetId = getIntent().getStringExtra(Constant.INTENT_DATA);
        mConversationType = (Conversation.ConversationType) getIntent().getSerializableExtra("type");
        getBaseHeadView().showBackButton(this);
        fragments = new ArrayList<>();
        //推荐和我的订阅
        if ("1".equals(focusFlag)) {
            getBaseHeadView().showTitle(getString(R.string.title_personal_recommed));
            mTabLayout.setupWithViewPager(mViewPager);
            String[] arrTabTitles = getResources().getStringArray(R.array.mine_recommend);

            for (int i = 0; i < 2; i++) {
                FocusFragment fragment = new FocusFragment();
                bundle = new Bundle();
                if (i == 0) bundle.putInt("type_id", 1);
                else bundle.putInt("type_id", 4);
                fragment.setArguments(bundle);
                fragments.add(fragment);
            }

            mViewPager.setAdapter(new MineTabAdpter(getSupportFragmentManager(), fragments, arrTabTitles));

        //我的好友
        } else if ("2".equals(focusFlag)) {
            getBaseHeadView().showTitle(getString(R.string.title_personal_focus));
            isSelectFriends = getIntent().getIntExtra("select_type_card",-1);
            mTabLayout.setVisibility(View.GONE);
            mFocusFragment = new FocusFragment();
            bundle = new Bundle();
            bundle.putInt("type_id", 3);
            if(mConversationType != null)
                bundle.putSerializable("type",mConversationType);
            if(targetId != null)
                bundle.putString("targetId", targetId);
            bundle.putInt("select_type_card", isSelectFriends);
            mFocusFragment.setArguments(bundle);
            fragments.add(mFocusFragment);
            mViewPager.setAdapter(new MineTabAdpter(getSupportFragmentManager(), fragments, new String[]{}));

        //我的粉丝
        } else if ("3".equals(focusFlag)) {
            getBaseHeadView().showTitle(getString(R.string.title_personal_fans));
            mTabLayout.setVisibility(View.GONE);
            FocusFragment fragment = new FocusFragment();
            bundle = new Bundle();
            bundle.putInt("type_id", 2);
            fragment.setArguments(bundle);
            fragments.add(fragment);
            mViewPager.setAdapter(new MineTabAdpter(getSupportFragmentManager(), fragments, new String[]{}));

        //添加朋友
        } else if ("5".equals(focusFlag)) {
            getBaseHeadView().showTitle("添加朋友");
            mTabLayout.setVisibility(View.GONE);
            FocusFragment fragment = new FocusFragment();
            bundle = new Bundle();
            bundle.putInt("type_id", 4);
            bundle.putBoolean("isFollow", false);
            fragment.setArguments(bundle);
            fragments.add(fragment);
            mViewPager.setAdapter(new MineTabAdpter(getSupportFragmentManager(), fragments, new String[]{}));
        }


    }

    @Override
    public void initListener() {

    }

    public String getFocusFlag() {
        return focusFlag;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.headBackButton:
                finish();
                break;

            case R.id.HeadRightImageButton:
                //如果分享聊天的东西给好友 应该跳转到搜索自己的好友列表里面
                if (isSelectFriends != -1) {
                    SearchConversationActivity.startActivity(this, isSelectFriends,1, ConversationActivity.REQUEST_SHARE_CONTENT);
                } else {
                    NewSearchActivity.jumpToSearch(this,"5");
                    //SearchConversationActivity.startActivity(this, isSelectFriends,1,0);
                }
                break;
        }
    }

    @Subscribe
    public void onEvent(EventSelectFriendBean bean){
        finish();
    }

    public static void startActivity(Activity activity, String type){
        Intent intent = new Intent(activity,FocusActivity.class);
        intent.putExtra("focus_flag", type);
        activity.startActivity(intent);
    }

    public static void startSelect(Activity activity, int type, String targetId, Conversation.ConversationType conversitionType, int requestCode){
        Intent intent = new Intent(activity,FocusActivity.class);
        intent.putExtra("focus_flag","2");
        intent.putExtra("select_type_card",type);
        if(!TextUtils.isEmpty(targetId))
            intent.putExtra("targetId",targetId);
        if(conversitionType != null)
            intent.putExtra("type",conversitionType);
        activity.startActivityForResult(intent,requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mFocusFragment != null){
            mFocusFragment.onActivityResult(requestCode,resultCode,data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
    }
}
