package com.gxtc.huchuan.ui.mine.classroom.purchaserecord;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.SeriesAndTopicRecordAdapter;
import com.gxtc.huchuan.bean.AllPurchaseListBean;
import com.gxtc.huchuan.bean.PurchaseCircleRecordBean;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.bean.PurchaseSeriesAndTopicBean;
import com.gxtc.huchuan.ui.live.intro.LiveIntroActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mine.classroom.classorderdetail.ClassOrderDetail;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Describe:
 * Created by ALing on 2017/6/2 .
 */

public class SeriesAndTopicFragment extends BaseTitleFragment implements PurchaseRecordContract.View{
    @BindView(R.id.rc_list) RecyclerView mRcList;
    @BindView(R.id.swipe_layout) SwipeRefreshLayout mSwipeLayout;

    private PurchaseRecordContract.Presenter mPresenter;
    private SeriesAndTopicRecordAdapter      adapter;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.common_recyclerview, container, false);
        return view;
    }
    @Override
    public void initData() {
        super.initData();
        new PurchaseRecordPresenter(this);
        mPresenter.getSeriesAndTopic(false);
        initRecyclerView();
    }

    @Override
    public void initListener() {
        super.initListener();
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                下拉刷新
                mPresenter.getSeriesAndTopic(true);
                mRcList.reLoadFinish();
            }
        });

        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMoreSeriesAndTopic();
            }
        });

    }

    private void initRecyclerView() {
        mSwipeLayout.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);
        mRcList.setBackgroundColor(getResources().getColor(R.color.module_divide_line));
        mRcList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL,false));
//        mRcList.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.HORIZONTAL_LIST, WindowUtil.dip2px(getActivity(),10), Color.parseColor("#ECEDEE")));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);
        adapter = new SeriesAndTopicRecordAdapter(getActivity(),new ArrayList<PurchaseSeriesAndTopicBean>(),R.layout.item_topic_record);

    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(getActivity(), LoginAndRegisteActivity.class);
    }


    @Override
    public void showSeriesAndTopic(List<PurchaseSeriesAndTopicBean> datas) {
        mRcList.setAdapter(adapter);
        mSwipeLayout.setRefreshing(false);
        adapter.notifyChangeData(datas);

        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                GotoUtil.goToActivity(getActivity(),ClassOrderDetail.class,100,adapter.getList().get(position));
            }
        });
    }


    @Override
    public void showCircleData(List<PurchaseCircleRecordBean> datas) {

    }

    @Override
    public void showDealData(List<PurchaseListBean> datas) {

    }

    @Override
    public void showAllOrderData(List<AllPurchaseListBean> datas) {

    }



    @Override
    public void showLoadMoreSeriesAndTopic(List<PurchaseSeriesAndTopicBean> datas) {
        mSwipeLayout.setRefreshing(false);
        mRcList.changeData(datas,adapter);
    }

    @Override
    public void showLoadMoreCircle(List<PurchaseCircleRecordBean> datas) {

    }

    @Override
    public void showLoadMoreDeal(List<PurchaseListBean> datas) {

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
        mPresenter.getSeriesAndTopic(false);
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
