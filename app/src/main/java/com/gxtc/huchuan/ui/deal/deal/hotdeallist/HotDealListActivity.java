package com.gxtc.huchuan.ui.deal.deal.hotdeallist;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.base.EmptyView;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.Deal1LevelAdapter;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HotDealListActivity extends BaseTitleActivity implements HotListContract.View,
        View.OnClickListener {

    @BindView(R.id.rv_deallist) RecyclerView       listView;
    @BindView(R.id.swipe_deal)  SwipeRefreshLayout swipeLayout;
    @BindView(R.id.base_empty_area) View                      layoutEmpty;

    private                         Deal1LevelAdapter         listAdapter;
    private                         HotListContract.Presenter mPresenter;
    private                         EmptyView                 emptyView;
    private int correntPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_list);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle("人气交易");
        getBaseHeadView().showBackButton(this);
        emptyView = new EmptyView(layoutEmpty);
        swipeLayout.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);

        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        listView.setLoadMoreView(R.layout.model_footview_loadmore);
        listAdapter = new Deal1LevelAdapter(this, new ArrayList<DealListBean>(),
                R.layout.deal_list_home_page);
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
    public void initListener() {
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listView.reLoadFinish();
                mPresenter.getData(true);
            }
        });
        listView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMrore();
            }
        });
        listAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                correntPosition = position;
                Intent intent = new Intent(HotDealListActivity.this, GoodsDetailedActivity.class);
                intent.putExtra(Constant.INTENT_DATA, listAdapter.getList().get(position));
                startActivityForResult(intent, 101);
            }
        });
    }

    @Override
    public void initData() {
        new HotListPresenter(this);
        mPresenter.getData(false);
    }

    @Override
    public void showData(List<DealListBean> datas) {
        listAdapter.notifyChangeData(datas);
        listView.setAdapter(listAdapter);
    }

    @Override
    public void showLoadMore(List<DealListBean> datas) {
        listView.changeData(datas, listAdapter);
    }

    @Override
    public void showRefreshFinish(List<DealListBean> datas) {
        emptyView.hideEmptyView();
        listView.notifyChangeData(datas, listAdapter);
    }

    @Override
    public void showNoMore() {
        listView.loadFinish();
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        swipeLayout.setRefreshing(false);
        getBaseLoadingView().hideLoading();
    }

    @Override
    public void showEmpty() {
        emptyView.showEmptyContent(getString(R.string.empty_no_data));
    }

    @Override
    public void showReLoad() {

    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showEmptyContent(getString(R.string.empty_net_error));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == 101) {
            if (data != null) {
                int commentCount = data.getIntExtra("commentCount", -1);
                int readCount    = data.getIntExtra("readCount", -1);
                if (correntPosition != -1) {
                    if (commentCount != -1) {
                        listAdapter.getList().get(correntPosition).setLiuYan(commentCount + "");
                    }
                    if (readCount != -1) {
                        listAdapter.getList().get(correntPosition).setRead(readCount + "");
                    }
                    listView.notifyItemChanged(correntPosition);
                }
            }
        }
    }

    @Override
    public void setPresenter(HotListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
