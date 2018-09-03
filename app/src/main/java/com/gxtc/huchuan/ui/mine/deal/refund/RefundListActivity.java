package com.gxtc.huchuan.ui.mine.deal.refund;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.RefundListAdapter;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 退款申请列表
 */

public class RefundListActivity extends BaseTitleActivity implements RefundListContract.View{

    @BindView(R.id.swipelayout)  SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView) RecyclerView       listView;

    private RefundListAdapter            mAdapter;
    private RefundListContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_list);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("申请退款");
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        refreshLayout.setColorSchemeResources(
                R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL,false));
        listView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL_LIST, WindowUtil.dip2px(this,10), Color.parseColor("#ECEDEE")));
        listView.setLoadMoreView(R.layout.model_footview_loadmore);
        mAdapter = new RefundListAdapter(this,new ArrayList<PurchaseListBean>(),R.layout.item_list_refund);
        listView.setAdapter(mAdapter);
    }

    @Override
    public void initListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listView.reLoadFinish();
                mPresenter.refreshData();
            }
        });

        listView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMoreData();
            }
        });
    }

    @Override
    public void initData() {
        new RefundListPresenter(this);
        mPresenter.getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void showRefreshData(List<PurchaseListBean> datas) {
        refreshLayout.setRefreshing(false);
        listView.notifyChangeData(datas,mAdapter);
    }

    @Override
    public void showLoadMoreData(List<PurchaseListBean> datas) {
        listView.changeData(datas,mAdapter);
    }

    @Override
    public void showNoLoadMore() {
        listView.loadFinish();
    }

    @Override
    public void tokenOverdue() {
        UserManager.getInstance().isLogin(this);
    }

    @Override
    public void showData(List<PurchaseListBean> datas) {
        listView.notifyChangeData(datas,mAdapter);
    }

    @Override
    public void setPresenter(RefundListContract.Presenter presenter) {
        mPresenter = presenter;
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
        if(mAdapter.getItemCount() == 0){
            getBaseEmptyView().showEmptyView(R.drawable.load_error,info,null);
        }else{
            ToastUtil.showShort(this,info);
        }
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseEmptyView().hideEmptyView();
                mPresenter.getData();
            }
        });
    }
}
