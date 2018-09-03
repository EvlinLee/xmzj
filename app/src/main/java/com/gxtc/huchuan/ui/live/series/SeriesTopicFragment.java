package com.gxtc.huchuan.ui.live.series;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.LiveTopicAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Gubr on 2017/3/1.
 * 课程
 */

public class SeriesTopicFragment extends BaseTitleFragment {

    @BindView(R.id.rl_topic)
    RecyclerView mRecyclerView;
    @BindView(R.id.sw_topic)
    SwipeRefreshLayout swTopic;

    private List<ChatInfosBean> mDatas;
    private LiveTopicAdapter mLiveTopicAdapter;
    private LinearLayoutManager mLinearLayoutManager;


    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_topic, container, false);
        return view;
    }

    @Override
    protected void onGetBundle(Bundle bundle) {
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
    }

    @Override
    public void initListener() {
        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
            }
        });
    }

    @Override
    public void initData() {
        mDatas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mDatas.add(new ChatInfosBean());
        }
        mRecyclerView.setAdapter(mLiveTopicAdapter);
    }

}
