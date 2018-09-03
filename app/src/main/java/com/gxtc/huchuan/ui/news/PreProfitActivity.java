package com.gxtc.huchuan.ui.news;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.PreProfitAdapter;
import com.gxtc.huchuan.bean.PreProfitBean;
import com.gxtc.huchuan.bean.ShareMakeMoneyBean;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.ui.circle.homePage.CircleMainActivity;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Describe:分享赚钱 > 分销列表
 * Created by ALing on 2017/5/25 .
 */

public class PreProfitActivity extends BaseTitleActivity implements View.OnClickListener,ShareMakeMoneyContract.View {
    @BindView(R.id.rc_list)      RecyclerView       mRcList;
    @BindView(R.id.swipe_layout) SwipeRefreshLayout mSwipeLayout;

    private ShareMakeMoneyContract.Presenter mPresenter;
    private List<PreProfitBean> mDatas;
    private PreProfitAdapter mAdapter;

    private CircleShareHandler mShareHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_recyclerview);
    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.label_distriibution_list));
        getBaseHeadView().showBackButton(this);

        mSwipeLayout.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        mRcList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL,false));
        mRcList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL_LIST, WindowUtil.dip2px(this,10), Color.parseColor("#ECEDEE")));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);
    }

    @Override
    public void initListener() {
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getMMoneyList(true);     //刷新重新获取数据
                mRcList.reLoadFinish();
            }
        });
        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMoreMoneyList();       //加载更多
            }
        });
        mAdapter = new PreProfitAdapter(this,new ArrayList<PreProfitBean>(),R.layout.item_pre_profit);
        mRcList.setAdapter(mAdapter);
        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String type = mAdapter.getList().get(position).getType();
                if ("chatInfo".equals(type)){
                    //跳转课程主页
                    mShareHandler.getLiveInfo( mAdapter.getList().get(position).getId(),null);
                    //LiveIntroActivity.startActivity(PreProfitActivity.this, mAdapter.getList().get(position).getId());
                }else {
                    //圈子主页
                    Integer groupId = Integer.valueOf(mAdapter.getList().get(position).getId());
                    gotoCircleMain(groupId);
                }
            }
        });
    }

    //跳转订单详情页
    private void gotoCircleMain(int groupId) {
        Intent intent = new Intent(this, CircleMainActivity.class);
        intent.putExtra("groupId", groupId);
        startActivity(intent);
    }

    @Override
    public void initData() {
        super.initData();
        new ShareMakeMoneyPresenter(this);
        mDatas = new ArrayList<>();
        mPresenter.getMMoneyList(false);
        mShareHandler = new CircleShareHandler(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;
        }
    }
    @Override
    public void showData(List<ShareMakeMoneyBean> datas) {

    }

    @Override
    public void showMoneyList(List<PreProfitBean> datas) {
        mRcList.notifyChangeData(datas,mAdapter);
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void setPresenter(ShareMakeMoneyContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        mSwipeLayout.setRefreshing(false);
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyContent(getString(R.string.empty_no_data));
    }

    @Override
    public void showReLoad() {
        mPresenter.getMMoneyList(false);
    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkViewReload(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
                //记得隐藏
                getBaseEmptyView().hideEmptyView();
            }
        });
    }


    @Override
    public void showRefreshFinish(List<ShareMakeMoneyBean> datas) {
    }

    @Override
    public void showMoneyListRefreshFinish(List<PreProfitBean> datas) {
        mRcList.notifyChangeData(datas,mAdapter);

    }

    @Override
    public void showLoadMore(List<ShareMakeMoneyBean> datas) {}

    @Override
    public void showLoadMoreMoneyList(List<PreProfitBean> datas) {
        mRcList.changeData(datas,mAdapter);
    }

    @Override
    public void showNoMore() {
        mRcList.loadFinish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        mShareHandler.destroy();
    }
}
