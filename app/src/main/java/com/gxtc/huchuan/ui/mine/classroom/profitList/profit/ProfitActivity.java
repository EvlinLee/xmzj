package com.gxtc.huchuan.ui.mine.classroom.profitList.profit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.DistributionAdapter;
import com.gxtc.huchuan.bean.DistributionBean;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Describe:
 * Created by ALing on 2017/5/5 .
 */

public class ProfitActivity extends BaseTitleActivity implements View.OnClickListener ,ProfitContract.View, BaseRecyclerAdapter.OnReItemOnClickListener {
    private static final String TAG = ProfitActivity.class.getSimpleName();
    @BindView(R.id.tv_share_count)
    TextView mTvShareCount;
    @BindView(R.id.rc_list)
    RecyclerView mRcList;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeLayout;

    private ProfitContract.Presenter mPresenter;
    private List<DistributionBean> distributionList;
    private String type = "1";        //显示类型。默认1      1:课堂  2：圈子
    private DistributionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_profit_list);
    }

    @Override
    public void initView() {
        super.initView();
        mTvShareCount.setVisibility(View.GONE);

        type = getIntent().getStringExtra("type");
        Log.i(TAG, "initData: "+type);

        if ("1".equals(type)){
            getBaseHeadView().showTitle(getString(R.string.title_class_profit));
        }else {
            getBaseHeadView().showTitle(getString(R.string.title_circle_profit));
        }

        getBaseHeadView().showBackButton(this);

        mSwipeLayout.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);

        mRcList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRcList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL_LIST, 20,
                getResources().getColor(R.color.module_divide_line)));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);
    }

    private void getData() {
        //课堂收益
        if ("1".equals(type)){
            mPresenter.getProfitList("1",false);
        }else {
            //圈子收益
            mPresenter.getProfitList("2",false);
        }
    }

    @Override
    public void initData() {
        super.initData();
        new ProfitPresenter(this);

        getData();

        distributionList = new ArrayList<>();

        adapter = new DistributionAdapter(this, new ArrayList<DistributionBean>(), R.layout.item_list_income);
        adapter.setOnReItemOnClickListener(this);
        mRcList.notifyChangeData(distributionList,adapter);
        mRcList.setAdapter(adapter);

    }

    @Override
    public void initListener() {
        super.initListener();

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getProfitList(type,true);     //刷新重新获取数据
            }
        });
        mRcList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMore(type);       //加载更多
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
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
    public void showProfitList(List<DistributionBean> datas) {
        mSwipeLayout.setRefreshing(false);
        mRcList.notifyChangeData(datas,adapter);
        mRcList.setAdapter(adapter);
    }

    @Override
    public void showRefreshFinish(List<DistributionBean> datas) {
        mRcList.notifyChangeData(datas,adapter);
    }

    @Override
    public void showLoadMoreClass(List<DistributionBean> datas) {
        mRcList.changeData(datas,adapter);
    }

    @Override
    public void showLoadMoreCircle(List<DistributionBean> datas) {
        mRcList.changeData(datas,adapter);
    }

    @Override
    public void showNoMore() {
        mRcList.loadFinish();
    }

    @Override
    public void setPresenter(ProfitContract.Presenter presenter) {
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
        getBaseEmptyView().showEmptyContent();
    }

    @Override
    public void showReLoad() {
        getData();
    }

    @Override
    public void showError(String info) {
        mSwipeLayout.setRefreshing(false);
        ToastUtil.showShort(this,info);
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
    public void onItemClick(View v, int position) {

    }

    public static void startActivity(Context context, String type) {
        Intent intent = new Intent(context, ProfitActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
