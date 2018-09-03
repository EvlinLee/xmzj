package com.gxtc.huchuan.ui.mine.classroom.purchaserecord;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseRecyclerTypeAdapter;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.DealRecordAdapter;
import com.gxtc.huchuan.bean.AllPurchaseListBean;
import com.gxtc.huchuan.bean.PurchaseCircleRecordBean;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.bean.PurchaseSeriesAndTopicBean;
import com.gxtc.huchuan.ui.deal.deal.orderDetailed.OrderDetailedActivity;
import com.gxtc.huchuan.ui.deal.deal.orderDetailed.OrderDetailedBuyerActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Describe:
 * Created by ALing on 2017/5/22.
 */

public class DealRecordFragment extends BaseTitleFragment implements PurchaseRecordContract.View{
    @BindView(R.id.rc_list) RecyclerView mRcList;
    @BindView(R.id.swipe_layout) SwipeRefreshLayout mSwipeLayout;

    private PurchaseRecordContract.Presenter mPresenter;
    private DealRecordAdapter                adapter;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.common_recyclerview, container, false);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        new PurchaseRecordPresenter(this);
        mPresenter.getDealData(false);

        initRecyclerView();
    }

    @Override
    public void initListener() {
        super.initListener();
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                下拉刷新
                mPresenter.getDealData(true);
                mRcList.reLoadFinish();
            }
        });

        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMoreDeal();
            }
        });

    }

    private void initRecyclerView() {
        mSwipeLayout.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);
        mRcList.setBackgroundColor(getResources().getColor(R.color.module_divide_line));
        mRcList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL,false));
//        mRcList.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.HORIZONTAL_LIST, WindowUtil.dip2px(getActivity(),10), Color.parseColor("#ECEDEE")));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);
        adapter = new DealRecordAdapter(getActivity(),new ArrayList<PurchaseListBean>(),R.layout.item_list_purchase3);

    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(getActivity(), LoginAndRegisteActivity.class);
    }

    @Override
    public void showSeriesAndTopic(List<PurchaseSeriesAndTopicBean> datas) {

    }

    @Override
    public void showCircleData(List<PurchaseCircleRecordBean> datas) {

    }

    @Override
    public void showDealData(List<PurchaseListBean> datas) {
        mRcList.setAdapter(adapter);
        mSwipeLayout.setRefreshing(false);
        adapter.notifyChangeData(datas);

        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                gotoOrderDetailed(adapter.getList().get(position));
            }
        });
    }

    @Override
    public void showAllOrderData(List<AllPurchaseListBean> datas) {

    }


    @Override
    public void showLoadMoreSeriesAndTopic(List<PurchaseSeriesAndTopicBean> datas) {

    }

    //跳转订单详情页
    private void gotoOrderDetailed(PurchaseListBean bean){
        Intent intent ;
        //是否是买家。0：买家；1：卖家
        if(bean.getBuyer() == 0){
            intent = new Intent(getActivity(), OrderDetailedBuyerActivity.class);
        }else {
            intent = new Intent(getActivity(), OrderDetailedActivity.class);
        }
        intent.putExtra(Constant.INTENT_DATA,bean.getId() + "");
        startActivityForResult(intent,110);
    }

    @Override
    public void showLoadMoreCircle(List<PurchaseCircleRecordBean> datas) {

    }

    @Override
    public void showLoadMoreDeal(List<PurchaseListBean> datas) {
        mSwipeLayout.setRefreshing(false);
        mRcList.changeData(datas,adapter);
    }

    @Override
    public void showNoMore() {
        mSwipeLayout.setRefreshing(false);
        mRcList.loadFinish();
    }

    @Override
    public void setPresenter(PurchaseRecordContract.Presenter presenter) {
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
        mPresenter.getDealData(false);
    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(getActivity(),info);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
