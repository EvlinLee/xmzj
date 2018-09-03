package com.gxtc.huchuan.ui.mine.incomedetail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.InComeAllCountAdapter;
import com.gxtc.huchuan.bean.AccountWaterBean;
import com.gxtc.huchuan.bean.FocusBean;
import com.gxtc.huchuan.bean.InComeAllCountBean;
import com.gxtc.huchuan.bean.event.EventFocusBean;
import com.gxtc.huchuan.ui.mine.incomedetail.distribute.DistributeActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Describe:收益明细
 * Created by ALing on 2017/5/19 .
 */

@SuppressLint("ValidFragment")
public class InComeDetailFragment extends BaseTitleFragment implements View.OnClickListener,
        NewInComeDetailContract.View {

    private TextView              mTvReward;
    private TextView              mTvClass;
    private TextView              mTvDeal;
    private TextView              mTvCircle;
    @BindView(R.id.rc_list)       RecyclerView       mRcList;
    @BindView(R.id.swipe_layout) SwipeRefreshLayout mSwipeLayout;

    private int dateType;
    private final String Type = "100";
    private TextView mTvAllIncome;
    private String added = "";

    private NewInComeDetailContract.Presenter mPresenter;
    private InComeAllCountAdapter mAdapter;
    private TextView mTvDistribute;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_income_detail, container, false);
        return view;
    }

    @Override
    public void initData() {
        initRecycleView();
        new NewInComeDetailPresenter(this);
        mPresenter.getTotalIncomeStatistics(dateType+"",false);
        mPresenter.getIncomeList(Type,added,dateType + "",false);
    }

    private void initRecycleView() {
        mSwipeLayout.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        mRcList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL,false));
        mRcList.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.HORIZONTAL_LIST));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.activity_income_header_layout,null);
        mRcList.addHeadView(header);
        header.findViewById(R.id.rl_reward).setOnClickListener(this);
        header.findViewById(R.id.rl_class).setOnClickListener(this);
        header.findViewById(R.id.rl_deal).setOnClickListener(this);
        header.findViewById(R.id.rl_circle).setOnClickListener(this);
        header.findViewById(R.id.rl_distribute).setOnClickListener(this);
        mTvAllIncome = (TextView) header.findViewById(R.id.tv_all_income);
        mTvReward = (TextView) header.findViewById(R.id.tv_reward);
        mTvClass = (TextView) header.findViewById(R.id.tv_class);
        mTvDeal = (TextView) header.findViewById(R.id.tv_deal);
        mTvCircle = (TextView) header.findViewById(R.id.tv_circle);
        mTvDistribute = (TextView) header.findViewById(R.id.tx_distribute);
        mAdapter = new InComeAllCountAdapter(getActivity(),new ArrayList<AccountWaterBean>(),R.layout.item_income_detail);
        mRcList.setAdapter(mAdapter);
    }

    @Override
    public void initListener() {
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getIncomeList(Type,added,dateType + "",true);
                mPresenter.getTotalIncomeStatistics(dateType+"",true);
                mRcList.reLoadFinish();
            }
        });
        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMore(Type, dateType + "");
            }
        });
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.rl_reward:
                goToDetailList(0);
                break;
            case R.id.rl_class:
                IncomeDetailActivity.jumpToIncomeDetailActivity(getActivity(),0, dateType);
                break;
            case R.id.rl_deal:
                goToDetailList(1);
                break;
            case R.id.rl_circle:
                IncomeDetailActivity.jumpToIncomeDetailActivity(getActivity(),1,dateType);
                break;
            case R.id.rl_distribute:
                DistributeActivity.jumpToDistributeActivity(getActivity(),dateType);
                break;
        }
    }

    private void goToDetailList(int detailType) {
        Intent intent = new Intent(getActivity(), InComeDetailListActivity.class);
        intent.putExtra("type", detailType);
        intent.putExtra("dateType", dateType);
        startActivity(intent);
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showTotalIncomeStatistics(InComeAllCountBean bean) {
        mTvAllIncome.setText(bean.getTotalMoney() + "元");
        mTvReward.setText(bean.getRewardMoney());
        mTvClass.setText(bean.getChatMoney());
        mTvDeal.setText(bean.getTradeMoney());
        mTvCircle.setText(bean.getGroupMoney());
        mTvDistribute.setText(bean.getDistributionMoney());
    }

    @Override
    public void showIncomeList(List<AccountWaterBean> bean) {
        mRcList.notifyChangeData(bean,mAdapter);
    }

    @Override
    public void showRefreshIncomeListFinish(List<AccountWaterBean> datas) {
        mSwipeLayout.setRefreshing(false);
        mRcList.notifyChangeData(datas,mAdapter);
    }

    @Override
    public void showRefreshTotalIncomeStatisticsFinish(InComeAllCountBean datas) {
        mTvAllIncome.setText(datas.getTotalMoney() + "元");
        mTvReward.setText(datas.getRewardMoney());
        mTvClass.setText(datas.getChatMoney());
        mTvDeal.setText(datas.getTradeMoney());
        mTvCircle.setText(datas.getGroupMoney());
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
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {}

    @Override
    public void showReLoad() {}

    @Override
    public void showError(String info) {
        getBaseEmptyView().showEmptyView(info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getIncomeList("",added,dateType + "",false);
                mPresenter.getTotalIncomeStatistics(dateType+"",false);
                getBaseEmptyView().hideEmptyView();
            }
        });
    }

    @Override
    protected void onGetBundle(Bundle bundle) {
        dateType = bundle.getInt(Constant.INTENT_DATA);
        added = bundle.getString("added");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
