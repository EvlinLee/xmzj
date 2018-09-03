package com.gxtc.huchuan.ui.mine.personalhomepage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.HisCircleAdapter;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.PersonalHomeDataBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventImgBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.circle.findCircle.CircleJoinActivity;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/5/2.
 */

public class HisCircleActivity extends BaseTitleActivity implements PersonalHomeContract.View{
    private static final String TAG = HisCircleActivity.class.getSimpleName();
    @BindView(R.id.rc_list)
    RecyclerView mRcList;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeLayout;

    private PersonalHomeContract.Presenter mPresenter;
    private String mUserCode;
    private List<CircleBean> list;
    private HisCircleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_recyclerview);
        EventBusUtil.register(this);

    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initRecyclerView();
    }

    @Override
    public void initData() {
        super.initData();
        new PersonalHomePresenter(this);
        mUserCode = getIntent().getStringExtra("userCode");
        Log.d(TAG, "initDat:userCode"+mUserCode);
        if (!(TextUtils.isEmpty(mUserCode))){
            getBaseHeadView().showTitle(getString(R.string.title_his_circle));
        }else {
            getBaseHeadView().showTitle(getString(R.string.title_mine_circle));
        }
        getCircleData();

    }

    private void getCircleData() {
        if (!(TextUtils.isEmpty(mUserCode))){
            mPresenter.getCircleListByUserCode(mUserCode,false);
        }else {
            mPresenter.getCircleListByUserCode(UserManager.getInstance().getUserCode(),false);
        }
    }

    private void initRecyclerView() {
        mSwipeLayout.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        mRcList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL,false));
        mRcList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);

        list = new ArrayList<>();
        adapter = new HisCircleAdapter(this,list, R.layout.item_his_circle);
        mRcList.setAdapter(adapter);
    }

    @Override
    public void initListener() {
        super.initListener();
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                下拉刷新
                if (!TextUtils.isEmpty(mUserCode)){
                    mPresenter.getCircleListByUserCode(mUserCode,true);
                }else {
                    mPresenter.getCircleListByUserCode(UserManager.getInstance().getUserCode(),true);
                }
                mRcList.reLoadFinish();
            }
        });

        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (!TextUtils.isEmpty(mUserCode)){
                    mPresenter.loadMrore(mUserCode);
                }else {
                    mPresenter.loadMrore(UserManager.getInstance().getUserCode());
                }
            }
        });


        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                CircleBean bean = adapter.getList().get(position);
                String     name = bean.getGroupName();
                String     joinUrl = bean.getJoinUrl();
                String     isAudit = bean.getIsAudit();
                double money = bean.getFee();
                int        id = bean.getId();
                int        isJoin = bean.getIsJoin();   //是否已经加入    0：未加入。1：已加入

                if (0 == isJoin){
                    Intent intent = new Intent(HisCircleActivity.this,CircleJoinActivity.class);
                    intent.putExtra("url",joinUrl);
                    intent.putExtra("id", id);
                    intent.putExtra("name",name);
                    intent.putExtra("isAudit", isAudit);
                    intent.putExtra(Constant.INTENT_DATA,money);
                    startActivityForResult(intent,0);
                }else {
                    GotoUtil.goToActivity(HisCircleActivity.this, CircleMainActivity.class, 0, bean);
                }
            }
        });

    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showHomePageSelfList(List<PersonalHomeDataBean> list) {

    }

    @Override
    public void showHomePageUserList(List<PersonalHomeDataBean> list) {

    }

    @Override
    public void showCircleByUserList(List<CircleBean> list) {
        mSwipeLayout.setRefreshing(false);
        adapter.notifyChangeData(list);
        mRcList.setAdapter(adapter);
    }

    @Override
    public void showCircleByUserCodeList(List<CircleBean> list) {
        mSwipeLayout.setRefreshing(false);
        adapter.notifyChangeData(list);
        mRcList.setAdapter(adapter);
    }

    @Override
    public void showRefreshFinish(List<CircleBean> datas) {
        mSwipeLayout.setRefreshing(false);
        mRcList.notifyChangeData(datas,adapter);
    }

    @Override
    public void showLoadMoreList(List<CircleBean> list) {
        if (adapter != null)
            mRcList.changeData(list,adapter);
    }

    @Override
    public void showSelfData(User user) {

    }

    @Override
    public void showMenberData(User user) {

    }

    @Override
    public void showUserFocus(Object object) {

    }

    @Override
    public void showRecommendList(List<PersonalHomeDataBean> list) {

    }

    @Override
    public void showNoMore() {
        mRcList.loadFinish();
    }

    @Override
    public void setPresenter(PersonalHomeContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyContent();
    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        if(adapter == null){
            getBaseEmptyView().showEmptyContent(info);
        }else{
            ToastUtil.showShort(this,info);
        }
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReLoad();
            }
        });
    }

    public static void startActivity(Context context,String userCode) {
        Intent intent = new Intent(context, HisCircleActivity.class);
        intent.putExtra("userCode", userCode);
        context.startActivity(intent);
    }

    //修改圈子封面图
    @Subscribe(sticky = true)
    public void onEvent(EventImgBean bean) {
        getCircleData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        EventBusUtil.unregister(this);
    }
}
