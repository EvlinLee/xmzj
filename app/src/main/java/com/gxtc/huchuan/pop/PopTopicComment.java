package com.gxtc.huchuan.pop;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BasePopupWindow;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.TopicCommentAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imlib.model.Message;

/**
 * Created by Gubr on 2017/3/15.
 */

public class PopTopicComment extends BasePopupWindow implements LoadMoreWrapper.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {


    private final ChatInfosBean mBean;
    @BindView(R.id.ll_back)
    LinearLayout mLlBack;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_topic_comment)
    SwipeRefreshLayout mSwipeTopicComment;
    @BindView(R.id.line)
    View mLine;
    @BindView(R.id.et_comment)
    TextView mEditComment;
    private TopicCommentAdapter mAdapter;

    public PopTopicComment(Activity activity, int resId, ChatInfosBean bean) {
        super(activity, resId);
        mBean = bean;
    }

    @Override
    public void init(View layoutView) {
        ButterKnife.bind(this, layoutView);
        mSwipeTopicComment.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2,
                R.color.refresh_color3,
                R.color.refresh_color4);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);
        ArrayList<Message> list = new ArrayList<>();
        mAdapter = new TopicCommentAdapter(getActivity(), list, R.layout.item_topic_comment);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initListener() {
        mRecyclerView.setOnLoadMoreListener(this);
        mSwipeTopicComment.setOnRefreshListener(this);
    }


    @OnClick({R.id.ll_back, R.id.et_comment})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ll_back:
                //回到直播
                closePop();
                break;

            case R.id.et_comment:
                //显示编辑弹窗
                break;
        }
    }

    @Override
    public void onLoadMoreRequested() {
        //上拉加载更多
//        ArrayList<Message> messages = new ArrayList<>();
//        mRecyclerView.changeData(messages,mAdapter);

    }

    @Override
    public void onRefresh() {
        //下拉加载更多。
//        ArrayList<Message> list = new ArrayList<>();
//        mRecyclerView.notifyChangeData(list,mAdapter);
//        mSwipeTopicComment.setRefreshing(true);
    }
}
