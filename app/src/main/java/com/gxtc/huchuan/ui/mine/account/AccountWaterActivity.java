package com.gxtc.huchuan.ui.mine.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.AccountWaterAdapter;
import com.gxtc.huchuan.bean.AccountWaterBean;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;

import java.util.List;

import butterknife.BindView;

/**
 * Created by sjr on 2017/4/20.
 * 账户流水
 */
public class AccountWaterActivity extends BaseTitleActivity implements AccountWaterContract.View {

    @BindView(R.id.rl_account_water) RecyclerView mRecyclerView;

    private AccountWaterContract.Presenter mPresenter;
    private AccountWaterAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_water);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_account_water));
        getBaseHeadView().showBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountWaterActivity.this.finish();
            }
        });
    }

    @Override
    public void initListener() {
        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMrore();
            }
        });
    }

    @Override
    public void initData() {
        initRecyCleView();
        new AccountWaterPresenter(this);
        mPresenter.getData();
    }

    private void initRecyCleView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void setPresenter(AccountWaterContract.Presenter presenter) {
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

    }

    @Override
    public void showError(String info) {
        ToastUtil.showShort(this, getString(R.string.empty_net_error));
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
    public void showData(final List<AccountWaterBean> datas) {
        LogUtil.printD("datas_size:" + datas.size());
        mAdapter = new AccountWaterAdapter(this, datas,
                R.layout.item_account_water);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(AccountWaterActivity.this, AccountWaterDetailsActivity.class);
                intent.putExtra("AccountWaterBean", datas.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void showLoadMore(List<AccountWaterBean> datas) {
        mRecyclerView.changeData(datas, mAdapter);
    }

    @Override
    public void showNoMore() {
        mRecyclerView.loadFinish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
