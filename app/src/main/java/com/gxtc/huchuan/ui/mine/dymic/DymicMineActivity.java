package com.gxtc.huchuan.ui.mine.dymic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.DymicMineAdapter;
import com.gxtc.huchuan.bean.PersonalDymicBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.ui.mine.circleinfodetail.DynamicDetialActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Describe: 动态列表
 * Created by ALing on 2017/5/17 .
 */

public class DymicMineActivity extends BaseTitleActivity implements DymicMineContract.View,
        View.OnClickListener {
    @BindView(R.id.rl_layout)    RelativeLayout     mRlLayout;
    @BindView(R.id.rc_list)      RecyclerView       mRcList;
    @BindView(R.id.swipe_layout) SwipeRefreshLayout mSwipeLayout;

    private DymicMineAdapter   adapter;
    private DymicMineContract.Presenter mPresenter;
    private AlertDialog mAlertDialog;
    private List<PersonalDymicBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_recyclerview);
    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_dymnic));
        getBaseHeadView().showHeadRightButton(getString(R.string.label_clear_all), this);
        getBaseHeadView().showBackButton(this);

        mSwipeLayout.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);

        mRcList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRcList.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);

    }

    @Override
    public void initData() {
        super.initData();
        new DymicMinePresenter(this);

        mPresenter.getData(false);

    }

    @Override
    public void initListener() {
        super.initListener();
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData(true);     //刷新重新获取数据
                mRcList.reLoadFinish();
            }
        });
        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMore();       //加载更多
            }
        });

        list = new ArrayList<>();
        adapter = new DymicMineAdapter(this, list, R.layout.item_dymic_mine);
        mRcList.setAdapter(adapter);

        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                PersonalDymicBean data = adapter.getList().get(position);
                DynamicDetialActivity.startActivity(DymicMineActivity.this,data.getId()+"");
            }
        });

    }

    private void gotoWebView(String url,String title) {
        Intent intent = new Intent(this, CommonWebViewActivity.class);
        intent.putExtra("web_url", url);
        intent.putExtra("web_title", title);
        startActivity(intent);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headBackButton:
                finish();
                break;
            case R.id.headRightButton:
                if (list.size() != 0){
                    showClearDialog();
                }
                break;
        }
    }

    private void showClearDialog() {
        mAlertDialog = DialogUtil.showInputDialog(this,false,null,"确定清空所有动态？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.delDymicList(UserManager.getInstance().getToken());
                mAlertDialog.dismiss();
            }
        });
    }

    @Override
    public void tokenOverdue() {
        GotoUtil.goToActivity(this, LoginAndRegisteActivity.class);
    }

    @Override
    public void showData(List<PersonalDymicBean> datas) {
        list = datas;
        mRcList.notifyChangeData(datas, adapter);
    }

    @Override
    public void showDelResult(Object o) {
        list.clear();
        getBaseHeadView().getHeadRightButton().setVisibility(View.GONE);
        getBaseEmptyView().showEmptyContent(getString(R.string.empty_no_data));
    }


    //下拉刷新
    @Override
    public void showRefreshFinish(List<PersonalDymicBean> datas) {
        mRcList.notifyChangeData(datas, adapter);
    }


    //加载更多
    @Override
    public void showLoadMore(List<PersonalDymicBean> datas) {
        list = datas;
        mRcList.changeData(datas, adapter);
    }

    //没有加载更多数据
    @Override
    public void showNoMore() {
        mRcList.loadFinish();
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
        getBaseHeadView().getHeadRightButton().setVisibility(View.GONE);
        getBaseEmptyView().showEmptyContent(getString(R.string.empty_no_data));
    }

    @Override
    public void showReLoad() {
        mPresenter.getData(false);
    }

    @Override
    public void showError(String info) {
        getBaseEmptyView().showEmptyContent(info);
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
    public void setPresenter(DymicMineContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

}
