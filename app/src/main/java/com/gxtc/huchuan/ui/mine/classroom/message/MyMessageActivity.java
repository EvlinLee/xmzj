package com.gxtc.huchuan.ui.mine.classroom.message;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.gxtc.huchuan.adapter.ClassRoomMeassageAdapter;
import com.gxtc.huchuan.bean.ClassMyMessageBean;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 我的 >  系统消息
 * Created by ALing on 2017/3/9.
 */

public class MyMessageActivity extends BaseTitleActivity implements View.OnClickListener,
        MyMessageContract.View {
    @BindView(R.id.rl_layout)    RelativeLayout     mRlLayout;
    @BindView(R.id.rc_list)      RecyclerView       mRcList;
    @BindView(R.id.swipe_layout) SwipeRefreshLayout mSwipeLayout;
    private ClassRoomMeassageAdapter adapter;

    private MyMessageContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_recyclerview);
        mRlLayout.setBackgroundColor(getResources().getColor(R.color.module_divide_line));
    }

    @Override
    public void initView() {
        super.initView();
        getBaseHeadView().showTitle(getString(R.string.title_personal_mymessage));
        getBaseHeadView().showBackButton(this);

        mSwipeLayout.setColorSchemeResources(R.color.refresh_color1, R.color.refresh_color2,
                R.color.refresh_color3, R.color.refresh_color4);

        mRcList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
//        mRcList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        mRcList.setLoadMoreView(R.layout.model_footview_loadmore);

    }

    @Override
    public void initData() {
        super.initData();
        new MyMessagePresenter(this);
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
        adapter = new ClassRoomMeassageAdapter(this, new ArrayList<ClassMyMessageBean>(), R.layout.item_classroom_mymessage);
        mRcList.setAdapter(adapter);
        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });

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
    public void showData(List<ClassMyMessageBean> datas) {
        mRcList.notifyChangeData(datas, adapter);
    }


    //下拉刷新
    @Override
    public void showRefreshFinish(List<ClassMyMessageBean> datas) {
        mRcList.notifyChangeData(datas, adapter);
    }


    //加载更多
    @Override
    public void showLoadMore(List<ClassMyMessageBean> datas) {
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
                getBaseEmptyView().hideEmptyView();
            }
        });
    }

    @Override
    public void setPresenter(MyMessageContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

}
