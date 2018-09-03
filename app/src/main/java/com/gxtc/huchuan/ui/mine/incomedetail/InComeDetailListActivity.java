package com.gxtc.huchuan.ui.mine.incomedetail;

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
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.AccountWaterAdapter;
import com.gxtc.huchuan.adapter.InComeAllCountAdapter;
import com.gxtc.huchuan.bean.AccountWaterBean;
import com.gxtc.huchuan.bean.InComeAllCountBean;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Describe:收益明细  二级页面 之打赏、交易
 * Created by ALing on 2017/5/19 .
 */

public class InComeDetailListActivity extends BaseTitleActivity implements View.OnClickListener,
        NewInComeDetailContract.View {
    @BindView(R.id.rc_list)        RecyclerView       mRcList;
    @BindView(R.id.swipe_layout)   SwipeRefreshLayout mSwipeLayout;
//    @BindView(R.id.base_empty_area) View               emptyView;

    private int mType;      // 0  打赏   1  交易
    private int dateType;      // 0  打赏   1  交易
    private String streamType;      //流水类型
    private NewInComeDetailContract.Presenter mPresenter;
    private InComeAllCountAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_recyclerview);
    }

    @Override
    public void initView() {
        mType = getIntent().getIntExtra("type", 0);
        dateType = getIntent().getIntExtra("dateType", 0);
        if (mType == 0) {
            getBaseHeadView().showTitle(getString(R.string.title_reward));
            streamType = "8";
        } else {
            getBaseHeadView().showTitle(getString(R.string.title_deal));
            streamType = "0";
        }
        getBaseHeadView().showBackButton(this);

        initRecycler();

    }

    private void initRecycler() {
        mSwipeLayout.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        mRcList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL,false));
        mRcList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getIncomeList(streamType,"",dateType+"",true);     //刷新重新获取数据   日期类型。    0：本日，1：本周，2：本月，3:本年，4：全部
                mRcList.reLoadFinish();
            }
        });
        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMore(streamType,dateType+"");       //加载更多
            }
        });

//        mAdapter = new AccountWaterAdapter(this,new ArrayList<AccountWaterBean>(),R.layout.item_income_detail_list);
        mAdapter = new InComeAllCountAdapter(this,new ArrayList<AccountWaterBean>(),R.layout.item_income_detail);
        mRcList.setAdapter(mAdapter);

    }

    @Override
    public void initData() {
        super.initData();
//        mEmptyView = new EmptyView(emptyView);
        new NewInComeDetailPresenter(this);

        mPresenter.getIncomeList(streamType,"",dateType+"",false);
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
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showTotalIncomeStatistics(InComeAllCountBean bean) {

    }

    @Override
    public void showIncomeList(List<AccountWaterBean> datas) {
        mRcList.notifyChangeData(datas,mAdapter);
    }

    @Override
    public void showRefreshIncomeListFinish(List<AccountWaterBean> datas) {
        mRcList.notifyChangeData(datas,mAdapter);
    }

    @Override
    public void showRefreshTotalIncomeStatisticsFinish(InComeAllCountBean datas) {

    }

    @Override
    public void showLoadMore(List<AccountWaterBean> datas) {
        mRcList.changeData(datas,mAdapter);
    }

    @Override
    public void showNoMore() {
        mRcList.loadFinish();
    }

    @Override
    public void setPresenter(NewInComeDetailContract.Presenter presenter) {
        this.mPresenter = presenter;
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
//        mPresenter.getIncomeList(false);
    }

    @Override
    public void showError(String info) {
        getBaseEmptyView().showEmptyContent(info);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
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
}
