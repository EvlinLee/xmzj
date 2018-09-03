package com.gxtc.huchuan.ui.live.participation;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleActivity;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ParicipationAdaspter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.ui.live.conversation.LiveConversationActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Gubr on 2017/3/29.
 * 我参与的课堂列表
 */

public class ParticipationActivity extends BaseTitleActivity implements ParticipationContract.View, View.OnClickListener {

    private static final String TAG = "ParticipationActivity";

    @BindView(R.id.recyclerview)        RecyclerView       mRecyclerview;
    @BindView(R.id.swipe_participation) SwipeRefreshLayout mSwipeParticipation;

    private ParticipationContract.Presenter mPresenter;
    private ParicipationAdaspter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participation);
    }

    @Override
    public void initView() {
        getBaseHeadView().showTitle(getString(R.string.title_participation));
        getBaseHeadView().showBackButton(this);

        new ParticipationPresenter(this);

        mSwipeParticipation.setColorSchemeResources(Constant.REFRESH_COLOR);

        adapter = new ParicipationAdaspter(this, new ArrayList<ChatInfosBean>(), R.layout.item_myparticipation);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        mRecyclerview.setLoadMoreView(R.layout.model_footview_loadmore);
        mRecyclerview.setAdapter(adapter);
    }

    @Override
    public void initListener() {
        mSwipeParticipation.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeParticipation.setRefreshing(false);
            }
        });
        adapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                ChatInfosBean chatInfosBean = adapter.getList().get(position);
                mPresenter.getChatInfos(Integer.valueOf(chatInfosBean.getId()));
            }
        });
        mRecyclerview.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.getData(false, adapter.getItemCount());
            }
        });

    }

    @Override
    public void initData() {
        mPresenter.getData(false, 0);
    }

    @Override
    public void showData(List<ChatInfosBean> datas) {
        if (adapter != null && datas != null && datas.size() > 0) {
            mRecyclerview.notifyChangeData(datas, adapter);
        }
    }

    @Override
    public void showRefreshFinish(List<ChatInfosBean> datas) {
        if (adapter != null && datas != null && datas.size() > 0) {
            mRecyclerview.notifyChangeData(datas, adapter);
        }
        mSwipeParticipation.setRefreshing(false);
    }

    @Override
    public void showLoadMore(List<ChatInfosBean> datas) {
        if (datas.size() == 0) {
            showNoMore();
        }
        if (adapter != null) {
            mRecyclerview.changeData(datas, adapter);
        }
    }

    @Override
    public void showNoMore() {
        mRecyclerview.loadFinish();
    }

    @Override
    public void showChatInfoSuccess(ChatInfosBean infosBean) {
        LiveConversationActivity.startActivity(this, infosBean);
    }


    @Override
    public void setPresenter(ParticipationContract.Presenter presenter) {
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
        getBaseEmptyView().showEmptyContent(info);
    }

    @Override
    public void showNetError() {
        getBaseEmptyView().showEmptyContent(getString(R.string.empty_net_error));

    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.headBackButton:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.destroy();
        super.onDestroy();
    }
}
