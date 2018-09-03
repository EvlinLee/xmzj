package com.gxtc.huchuan.ui.live.list;

import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gxtc.commlibrary.base.BaseLazyFragment;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.LiveListAdapter;
import com.gxtc.huchuan.bean.LiveHeadTitleBean;
import com.gxtc.huchuan.bean.LiveListBean;
import com.gxtc.huchuan.widget.DividerItemDecoration;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Gubr on 2017/3/7.
 */

public class LiveListFragment extends BaseLazyFragment implements LiveListContract.View,
        BaseRecyclerAdapter.OnReItemOnClickListener {

    private static final String TAG = "LiveListFragment";

    @BindView(R.id.recyclerview) RecyclerView mRcLiveList;

    private LiveListAdapter                   mLiveListAdapter;
    private LiveListContract.Presenter        mPresenter;
    private LiveHeadTitleBean.ChatTypeSonBean mBean;
    private boolean isLoad = true;
    private String mId;


    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.layout_recyclerview, container, false);
        Log.d(TAG, "initView: ");
        return view;
    }


    @Override
    public void initData() {
        mBean = (LiveHeadTitleBean.ChatTypeSonBean) getArguments().getSerializable("bean");
        mId = mBean.getId();
        new LiveListPresenter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        mRcLiveList.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL_LIST));
        mRcLiveList.setLayoutManager(linearLayoutManager);
        mRcLiveList.setLoadMoreView(R.layout.model_footview_loadmore);
    }

    @Override
    public void initListener() {
        mRcLiveList.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.getLoadMore(Integer.valueOf(mBean.getId()),
                        mLiveListAdapter.getItemCount());
            }
        });

    }

    @Override
    public void tokenOverdue() {

    }

    @Override
    public void showLiveListDatas(List<LiveListBean> datas) {
        isLoad = false;
        if (mLiveListAdapter == null) {
            mLiveListAdapter = new LiveListAdapter(getContext(), datas, R.layout.item_live_list);
            mRcLiveList.setAdapter(mLiveListAdapter);
            mLiveListAdapter.setOnReItemOnClickListener(this);

        } else {
//            mLiveListAdapter.changeDatas(datas);
            mRcLiveList.notifyChangeData(datas, mLiveListAdapter);
        }
    }

    @Override
    public void showLoadMore(List<LiveListBean> datas) {
        Log.d(TAG, "datas.size():" + datas.size());
        if (mLiveListAdapter != null) {
            mRcLiveList.changeData(datas, mLiveListAdapter);

        }
    }


    @Override
    public void canLoadMore(boolean flag) {
        if (mRcLiveList == null) return;
        if (flag) {
            mRcLiveList.reLoadFinish();
        } else {
            mRcLiveList.loadFinish();
        }
    }

    @Override
    public void setPresenter(LiveListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {

    }

    @Override
    public void showLoadFinish() {
        mRcLiveList.loadFinish();
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyContent(getString(R.string.empty_no_data));

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

    @Override
    protected void lazyLoad() {
        if (isLoad) {
            mPresenter.getLiveListDatas(Integer.valueOf(mBean.getId()));
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        LiveListBean liveListBean = mLiveListAdapter.getList().get(position);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
