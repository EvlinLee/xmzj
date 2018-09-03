package com.gxtc.huchuan.ui.mine.circle.article;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.CircleNewsAdapter;
import com.gxtc.huchuan.bean.CircleNewsBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.event.EventArticleAuditeBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.news.NewsWebActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.LoginErrorCodeUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by sjr on 2017/6/14.
 * 未审核文章
 */

public class ArticleAuditeListFragment extends BaseTitleFragment implements ArticleAuditedListContract.View {

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

        new ArticleAuditedListPresenter(this, String.valueOf(circleId), 2, UserManager.getInstance().getUserCode());

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
        if(swipeNews != null)
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


    private AlertDialog mSureDialog;


    @Override
    public void showData(List<CircleNewsBean> datas) {
        mDatas.clear();
        mDatas.addAll(datas);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayout.VERTICAL, false));

        mAdapter = new CircleNewsAdapter(getContext(), datas, "1", R.layout.item_news_fragment);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                Intent intent = new Intent(ArticleAuditeListFragment.this.getActivity(), NewsWebActivity.class);
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
                bean.setRedirectUrl(data.getRedirectUrl());
                intent.putExtra("data", bean);
                startActivity(intent);

            }
        });


        mAdapter.setOnAuditListener(new CircleNewsAdapter.OnAuditListener() {
            @Override
            public void onAudit(final int position, final CircleNewsBean bean) {
                mSureDialog = DialogUtil.createDialog2(getActivity(), "是否通过审核？", "不通过", "通过", new DialogUtil.DialogClickListener() {
                    @Override
                    public void clickLeftButton(View view) {

                        mSureDialog.dismiss();
                        auditNews(bean.getId(), 2, position);

                    }

                    @Override
                    public void clickRightButton(View view) {
                        mSureDialog.dismiss();
                        auditNews(bean.getId(), 1, position);
                        EventBusUtil.post(new EventArticleAuditeBean());
                    }
                });
                mSureDialog.show();
            }
        });

    }


    Subscription sub;

    private void auditNews(String linkId, final Integer audit, final int position) {
        sub = CircleApi.getInstance().auditNC(UserManager.getInstance().getToken(), linkId, audit, 0, circleId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Void>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        ToastUtil.showShort(ArticleAuditeListFragment.this.getContext(), "审核成功");
                        mRecyclerView.removeData(mAdapter, position);
                        if ("1".equals(audit)) {//审核通过刷新已通过文章列表
                            EventBusUtil.post(new EventArticleAuditeBean());
                        }
                        if (mAdapter.getList().size() == 0) {
                            getBaseEmptyView().showEmptyContent();
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        LoginErrorCodeUtil.showHaveTokenError(ArticleAuditeListFragment.this.getActivity(), errorCode, message);
                    }
                }));

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

    @Override
    public void showNoMore() {
        mRecyclerView.loadFinish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sub != null && sub.isUnsubscribed()) {
            sub.unsubscribe();
        }
    }
}
