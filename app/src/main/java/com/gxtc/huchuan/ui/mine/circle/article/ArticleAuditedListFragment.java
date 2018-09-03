package com.gxtc.huchuan.ui.mine.circle.article;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseTitleFragment;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleNewsAdapter;
import com.gxtc.huchuan.bean.CircleNewsBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.event.EventArticleAuditeBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.news.NewsWebActivity;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * Created by sjr on 2017/6/14.
 * 已审核文章
 */

public class ArticleAuditedListFragment extends BaseTitleFragment implements ArticleAuditedListContract.View {

    @BindView(R.id.rl_circle_news)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_circle_news)
    SwipeRefreshLayout swipeNews;

    private int circleId;

    private ArticleAuditedListContract.Presenter mPresenter;

    private CircleNewsAdapter mAdapter;
    private List<CircleNewsBean> mDatas = new ArrayList<>();

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        EventBusUtil.register(this);
        return inflater.inflate(R.layout.fragment_circle_news_audited, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        circleId = ((ArticleManagerActivity) context).getCircleId();
    }

    @Override
    public void initData() {
        swipeNews.setColorSchemeResources(R.color.refresh_color1,
                R.color.refresh_color2, R.color.refresh_color3,
                R.color.refresh_color4);
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);

        new ArticleAuditedListPresenter(this, String.valueOf(circleId), 1, UserManager.getInstance().getUserCode());

        mPresenter.getData(false);
    }

    @Override
    public void initListener() {
        swipeNews.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData(true);
                mRecyclerView.reLoadFinish();
            }
        });
        mRecyclerView.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPresenter.loadMrore();
            }
        });
    }

    @Override
    public void tokenOverdue() {
        Intent intent = new Intent(this.getContext(), LoginAndRegisteActivity.class);
        startActivity(intent);
    }

    @Override
    public void setPresenter(ArticleAuditedListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        swipeNews.setRefreshing(false);
        getBaseLoadingView().hideLoading();
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
        if (mDatas == null || mDatas.size() == 0) {
            getBaseEmptyView().showEmptyContent();
        }
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
    public void showData(List<CircleNewsBean> datas) {
        mDatas.clear();
        mDatas.addAll(datas);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayout.VERTICAL, false));

        mAdapter = new CircleNewsAdapter(getContext(), datas, "2", R.layout.item_news_fragment);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Intent intent = new Intent(ArticleAuditedListFragment.this.getActivity(), NewsWebActivity.class);
                NewsBean bean = new NewsBean();
//                    NewNewsBean.DataBean data = mAdapter.getList().get(position).getData();
                CircleNewsBean data = mAdapter.getList().get(position);
                bean.setId(data.getId());
                bean.setSource(data.getSource());
                bean.setIsVideo(data.getIsVideo());
                bean.setVideoUrl(data.getVideoUrl());
                bean.setCommentCount(data.getCommentCount());
                bean.setThumbsupCount(data.getThumbsupCount());
                bean.setIsThumbsup(data.getIsThumbsup());
                bean.setIsCollect(data.getIsCollect());
                bean.setCover(data.getCover());
                bean.setTitle(data.getTitle());
                bean.setDigest(data.getDigest());
                bean.setUserCode(data.getUserCode());
                bean.setSourceicon(data.getSourceicon());
                bean.setRedirectUrl(data.getRedirectUrl());
                intent.putExtra("data", bean);
                startActivity(intent);

            }
        });
    }

    @Override
    public void showRefreshFinish(List<CircleNewsBean> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        mRecyclerView.notifyChangeData(mDatas, mAdapter);
    }

    @Override
    public void showLoadMore(List<CircleNewsBean> datas) {
        mRecyclerView.changeData(datas, mAdapter);
        mDatas.addAll(datas);
    }

    @Subscribe
    public void onEvent(EventArticleAuditeBean bean) {
        initData();
    }

    @Override
    public void showNoMore() {
        mRecyclerView.loadFinish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBusUtil.unregister(this);
        mPresenter.destroy();
    }
}
