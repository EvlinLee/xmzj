package com.gxtc.huchuan.ui.mine.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.FreezeAccountAdapter;
import com.gxtc.huchuan.bean.FreezeAccountBean;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by sjr on 2017/3/22.
 * 未结算账单
 */

public class FreezeAccountActivity extends BaseTitleActivity implements FreezeAccountContract.View {

    @BindView(R.id.rc_list) RecyclerView mRcList;
    @BindView(R.id.swipe_layout) SwipeRefreshLayout mSwipeLayout;

    private List<FreezeAccountBean>         mDatas;
    private FreezeAccountAdapter            mAdapter;
    private FreezeAccountContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_recyclerview);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_freeze_account));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FreezeAccountActivity.this.finish();
            }
        });
        initRecyCleView();
    }

    @Override
    public void initData() {
        new FreezeAccountPresenter(this);
        mPresenter.getData(false);
    }

    private void initRecyCleView() {
        mSwipeLayout.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        mRcList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL,false));
        mRcList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);
        mAdapter = new FreezeAccountAdapter(this, new ArrayList<FreezeAccountBean>(), R.layout.item_freeze_account_content);
        mRcList.setAdapter(mAdapter);
    }

    @Override
    public void initListener() {
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData(true);
                mRcList.reLoadFinish();
            }
        });
        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMrore();
            }
        });

        mAdapter.setOnReItemOnClickListener(new FreezeAccountAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(FreezeAccountActivity.this, FreezeAccountDetailsActivity.class);
                intent.putExtra("freeze_account", mAdapter.getList().get(position));
                startActivity(intent);
            }

        });

    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void setPresenter(FreezeAccountContract.Presenter presenter) {
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
        getBaseEmptyView().showEmptyView();
    }

    @Override
    public void showReLoad() {
        mPresenter.getData(false);
    }

    @Override
    public void showError(String info) {
        mSwipeLayout.setRefreshing(false);
        ToastUtil.showShort(this, info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showNetWorkViewReload(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getData(false);
                //记得隐藏
                getBaseEmptyView().hideEmptyView();
            }
        });
    }

    @Override
    public void showData(List<FreezeAccountBean> datas) {
        mRcList.notifyChangeData(datas,mAdapter);
    }

    @Override
    public void showLoadMore(List<FreezeAccountBean> datas) {
        mRcList.changeData(datas, mAdapter);
    }

    @Override
    public void showRefreshFinish(List<FreezeAccountBean> datas) {
        mSwipeLayout.setRefreshing(false);
        mRcList.notifyChangeData(datas,mAdapter);
    }

    @Override
    public void showNoMore() {
        mRcList.loadFinish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
