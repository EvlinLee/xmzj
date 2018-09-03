package com.gxtc.huchuan.ui.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.base.BaseLazyFragment;
import com.gxtc.commlibrary.base.BaseMoreTypeRecyclerAdapter;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.recyclerview.wrapper.LoadMoreWrapper;
import com.gxtc.commlibrary.utils.ACache;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.NewsAdapter;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ListNewNewsBean;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.UpdataBean;
import com.gxtc.huchuan.bean.event.EventCollectBean;
import com.gxtc.huchuan.bean.event.EventGuideBean;
import com.gxtc.huchuan.bean.event.EventLoadBean;
import com.gxtc.huchuan.bean.event.EventScorllTopBean;
import com.gxtc.huchuan.bean.event.EventThumbsupBean;
import com.gxtc.huchuan.data.SpecialBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.dialog.UpdataDialog;
import com.gxtc.huchuan.handler.CircleShareHandler;
import com.gxtc.huchuan.helper.GuideHelper;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;
import com.gxtc.huchuan.ui.deal.deal.goodsDetailed.GoodsDetailedActivity;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.mine.setting.SettingActivity;
import com.gxtc.huchuan.ui.special.SpecialDetailActivity;
import com.gxtc.huchuan.utils.DialogUtil;
import com.gxtc.huchuan.utils.ReLoginUtil;
import com.gxtc.huchuan.utils.SystemTools;
import com.gxtc.huchuan.widget.NewsHeadView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import cn.jzvd.JZVideoPlayer;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.gxtc.huchuan.ui.MainActivity.STATISTICS;

/**
 * Created by sjr on 2017/2/16.
 * 新闻条目
 */
public class NewsItemFragment extends BaseLazyFragment implements NewsItemContract.View {

    @BindView(R.id.rl_news)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_news)
    SwipeRefreshLayout swipeNews;

    private NewsItemContract.Presenter mPresenter;
    private NewsAdapter mAdapter;
    private NewsHeadView headView;

    private int id;
    private String name;

    private int flag;        //1是资讯里的  2是个人中心里的
    private String userCode;    //用户编码

    private CircleShareHandler mShareHandler;

    private List<NewNewsBean> mDatas = new ArrayList<>();
    private AlertDialog mAlertDialog;

    public RefreshLisner mRefreshLisner;

    public interface RefreshLisner {
        void OnRefresh();
    }

    public void setRefreshLisner(RefreshLisner refreshLisner) {
        mRefreshLisner = refreshLisner;
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_news_first, container, false);
        EventBusUtil.register(this);
        return view;
    }


    @Override
    public void initData() {
        swipeNews.setColorSchemeResources(Constant.REFRESH_COLOR);
        mRecyclerView.setLoadMoreView(R.layout.model_footview_loadmore);
        mShareHandler = new CircleShareHandler(getContext());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false));

        if ("推荐".equals(name)) {
            if (headView != null) {
                headView.removeAllViews();
            }
            headView = new NewsHeadView(getActivity(), getContext(), mRecyclerView, this);
            if (mRecyclerView.getHeadCount() == 0) {
                mRecyclerView.addHeadView(headView);
            }
            SpUtil.putBoolean(getContext(), GuideHelper.GUIDE_TWO, true);
        }

        mAdapter = new NewsAdapter(getContext(), new ArrayList<NewNewsBean>(),
                R.layout.item_news_video_fragment,
                R.layout.item_news_fragment,
                R.layout.item_live_room,
                R.layout.item_deal_2_level,
                R.layout.item_news_ads1_fragment,
                R.layout.item_news_fragment,
                R.layout.item_live_room,
                R.layout.item_deal_2_level,
                R.layout.item_my_special_list,
                R.layout.item_no_support);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnReItemOnClickListener(
                new BaseMoreTypeRecyclerAdapter.OnReItemOnClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                        //新闻
                        if ("news".equals(mAdapter.getDatas().get(position).getType())) {
                            Intent intent = new Intent(getActivity(), NewsWebActivity.class);
                            NewsBean bean = new NewsBean();
                            NewNewsBean.DataBean data = mAdapter.getDatas().get(position).getData();
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
                            bean.setRedirectUrl(data.getRedirectUrl());
                            bean.setSourceicon(data.getSourceicon());
                            bean.setUserCode(data.getUserCode());
                            intent.putExtra("data", bean);
                            startActivity(intent);

                            //交易
                        } else if ("tradeInfo".equals(mAdapter.getDatas().get(position).getType())) {
                            Intent intent = new Intent(getActivity(), GoodsDetailedActivity.class);
                            intent.putExtra(Constant.INTENT_DATA, mAdapter.getDatas().get(position).getData().getId());
                            startActivity(intent);

                            //课程
                        } else if ("chatInfo".equals(mAdapter.getDatas().get(position).getType())) {
                            if (mAdapter.getDatas().get(position).getData().getChatSeriesData() != null) {
                                SeriesActivity.startActivity(getContext(), mAdapter.getDatas().get(position).getData().getChatSeriesData().getId(), true);
                            } else {
                                mShareHandler.getLiveInfo(mAdapter.getDatas().get(position).getData().getId(), null);
                            }

                            //专题
                        } else if ("newsSpecial".equals(mAdapter.getDatas().get(position).getType())) {
                            NewNewsBean.DataBean dataBean = mAdapter.getDatas().get(position).getData();
                            SpecialDetailActivity.gotoSpecialDetailActivity(getActivity(), dataBean.getId());

                            //大图广告
                        } else if (("ad".equals(mAdapter.getDatas().get(position).getType())) && (0 == mAdapter.getDatas().get(position).getData().getContentType())) {

                            Intent intent = new Intent(getActivity(), CommonWebViewActivity.class);
                            intent.putExtra("web_url", mAdapter.getDatas().get(position).getData().getUrl());
                            intent.putExtra("web_title", mAdapter.getDatas().get(position).getData().getTitle());
                            intent.putExtra("web_cover", mAdapter.getDatas().get(position).getData().getCover());
                            startActivity(intent);

                            //列表文章
                        } else if (("ad".equals(mAdapter.getDatas().get(position).getType())) && (1 == mAdapter.getDatas().get(position).getData().getContentType())) {

                            Intent intent = new Intent(getActivity(), NewsWebActivity.class);
                            NewsBean bean = new NewsBean();
                            NewNewsBean.DataBean.NewsRespVoBean data = new NewNewsBean.DataBean.NewsRespVoBean();
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
                            bean.setRedirectUrl(data.getRedirectUrl());
                            intent.putExtra("data", bean);
                            startActivity(intent);

                            //交易广告
                        } else if (("ad".equals(mAdapter.getDatas().get(position).getType())) && (4 == mAdapter.getDatas().get(position).getData().getContentType())) {
                            Intent intent = new Intent(getContext(), GoodsDetailedActivity.class);
                            intent.putExtra(Constant.INTENT_DATA, mAdapter.getDatas().get(position).getData().getTradeInfoVo().getId());
                            getContext().startActivity(intent);

                            //课堂广告
                        } else if (("ad".equals(mAdapter.getDatas().get(position).getType())) && (5 == mAdapter.getDatas().get(position).getData().getContentType())) {
                            ChatInfosBean chatInfosBean = mAdapter.getDatas().get(position).getData().getChatInfoRespVo();
                            //LiveIntroActivity.startActivity(getContext(), chatInfosBean.getId());
                            mShareHandler.getLiveInfo(chatInfosBean.getId(), null);
                        } else {
                            checkUpdata();
                        }
                    }
                });

        mAdapter.setOnShieldListener(new NewsAdapter.OnShieldListener() {
            @Override
            public void onShieldArticle(String id) {
                if (UserManager.getInstance().isLogin(getActivity())) {
                    mPresenter.shieldType(id, "", 1, "1");
                }
            }

            @Override
            public void onShieldUser(String userCode) {
                if (UserManager.getInstance().isLogin(getActivity())) {
                    mPresenter.shieldType("", userCode, 2, "1");
                }
            }
        });
    }

    private void checkUpdata() {
        getBaseLoadingView().showLoading();
        final int code = SystemTools.getAppVersionCode(MyApplication.getInstance());
        Subscription sub = AllApi.getInstance()
                .getAppVersion(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<UpdataBean>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (getBaseLoadingView() != null && data != null && data instanceof UpdataBean) {
                            getBaseLoadingView().hideLoading();
                            UpdataBean bean = (UpdataBean) data;
                            if (code < bean.getVersionCode()) {
                                showUpdataDialog(bean);
                            } else {
                                ToastUtil.showShort(NewsItemFragment.this.getContext(), "当前已是最新版本");
                            }
                        }
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if (getBaseLoadingView() != null) {
                            getBaseLoadingView().hideLoading();
                            ToastUtil.showShort(NewsItemFragment.this.getContext(), message);
                        }
                    }
                }));
        RxTaskHelper.getInstance().addTask(this, sub);
    }


    private UpdataDialog mUpdataDialog;

    private void showUpdataDialog(UpdataBean bean) {
        mUpdataDialog = new UpdataDialog();
        mUpdataDialog.setUpdataInfo(bean);
        mUpdataDialog.show(NewsItemFragment.this.getActivity().getSupportFragmentManager(), UpdataDialog.class.getSimpleName());
    }

    @Override
    protected void onGetBundle(Bundle bundle) {
        flag = bundle.getInt("news_type");
        id = bundle.getInt("id");
        name = bundle.getString("name");
        userCode = bundle.getString("userCode");
    }

    @Override
    public void initListener() {
        swipeNews.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if ("推荐".equals(name)) {
                    if (mRefreshLisner != null)
                        mRefreshLisner.OnRefresh();//该接口是用来刷新广告位的，所以仅在推荐这里有刷新广告数据的
                }
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
    public void showData(final List<NewNewsBean> datas) {
        if (mRecyclerView == null) return;
        mRecyclerView.notifyChangeData(datas, mAdapter);

        mDatas.clear();
        mDatas.addAll(datas);

        final ListNewNewsBean bean = new ListNewNewsBean();
        bean.setNewNewsBeen(mDatas);
        Observable.just(bean)
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<ListNewNewsBean>() {
                    @Override
                    public void call(ListNewNewsBean liveHeadPageBean) {
                        ACache.get(MyApplication.getInstance()).put(NewsItemFragment.class.getSimpleName() + id, bean);
                    }
                });

        //new GuideHelper(getActivity()).show(GuideHelper.TYPE_TWO, headView.getItemView().getChildAt(1));
    }

    @Override
    public void showRefreshFinish(List<NewNewsBean> datas) {
        if (mRecyclerView == null) return;
        mDatas.clear();
        mDatas.addAll(datas);
        mRecyclerView.notifyChangeData(mDatas, mAdapter);
    }

    @Override
    public void showLoadMore(List<NewNewsBean> datas) {
        if (mRecyclerView == null) return;
        mRecyclerView.changeData(datas, mAdapter);
        mDatas.addAll(datas);
    }

    @Override
    public void showNoMore() {
        mRecyclerView.loadFinish();
    }

    @Override
    public void showShieldSuccess(String articleId, String targetUserCode) {
        if (mAdapter == null) return;

        List<NewNewsBean> bean = mAdapter.getDatas();
        //屏蔽用户
        if (TextUtils.isEmpty(targetUserCode)) {
            for (int i = 0; i < bean.size(); i++) {
                if (!TextUtils.isEmpty(articleId) && articleId.equals(bean.get(i).getData().getId())) {
                    mRecyclerView.removeData(mAdapter, i);
                    return;
                }
            }

            //屏蔽文章
        } else {
            Iterator<NewNewsBean> it = mAdapter.getDatas().iterator();
            while (it.hasNext()) {
                NewNewsBean temp = it.next();
                if (!TextUtils.isEmpty(targetUserCode) && targetUserCode.equals(temp.getData().getUserCode())) {
                    it.remove();
                }
            }
            mRecyclerView.notifyChangeData();
        }
    }

    @Override
    public void showShieldError(String articleId, String targetUserCode, String msg) {
        ToastUtil.showShort(getContext(), msg);
    }

    @Override
    public void tokenOverdue() {
        mAlertDialog = DialogUtil.showDeportDialog(getActivity(), false, null, getResources().getString(R.string.token_overdue), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tv_dialog_confirm) {
                    ReLoginUtil.ReloginTodo(getActivity());
                }
                mAlertDialog.dismiss();
            }
        });
    }

    @Override
    public void showLoad() {
        getBaseLoadingView().showLoading();
    }

    @Override
    public void showLoadFinish() {
        if (swipeNews != null) {
            swipeNews.setRefreshing(false);
            getBaseLoadingView().hideLoading();
        }
    }

    @Override
    public void showEmpty() {
        getBaseEmptyView().showEmptyContent(getString(R.string.empty_no_data));
    }

    @Override
    public void showReLoad() {

    }

    /**
     * 加载更多时网络错误，直接打吐司
     */
    @Override
    public void showError(String info) {
        //有数据加载更多的时候出现错误
        if (mDatas.size() > 0) {
            ToastUtil.showShort(this.getContext(), info);
            getBaseLoadingView().hideLoading();

            //没有数据的时候出现错误
        } else if (mDatas.size() == 0) {
            Observable.just(NewsItemFragment.class.getSimpleName() + id)
                    .subscribeOn(Schedulers.io())
                    .map(new Func1<String, ListNewNewsBean>() {
                        @Override
                        public ListNewNewsBean call(String key) {
                            ListNewNewsBean bean = (ListNewNewsBean) ACache.get(MyApplication.getInstance()).getAsObject(key);
                            return bean;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ListNewNewsBean>() {
                        @Override
                        public void call(ListNewNewsBean bean) {
                            if (bean != null)
                                showData(bean.getNewNewsBeen());
                            else {
                                getBaseEmptyView().showNetWorkViewReload(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mPresenter.getData(false);
                                                getBaseEmptyView().hideEmptyView();
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    /**
     * 初始网络错误，点击重新加载
     */
    @Override
    public void showNetError() {
        Observable.just(NewsItemFragment.class.getSimpleName() + id)
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, ListNewNewsBean>() {
                    @Override
                    public ListNewNewsBean call(String key) {
                        ListNewNewsBean bean = (ListNewNewsBean) ACache.get(MyApplication.getInstance()).getAsObject(key);
                        return bean;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<ListNewNewsBean>() {
                            @Override
                            public void call(ListNewNewsBean bean) {
                                if (bean != null) {
                                    showData(bean.getNewNewsBeen());

                                } else {
                                    getBaseEmptyView().showNetWorkViewReload(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mPresenter.getData(false);
                                            getBaseEmptyView().hideEmptyView();
                                        }
                                    });
                                }
                            }
                        });

    }

    @Override
    public void setPresenter(NewsItemContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Subscribe
    public void onEvent(EventGuideBean bean) {
        if ("推荐".equals(name) && bean.index == 2 && headView != null) {
            new GuideHelper(getActivity()).show(GuideHelper.TYPE_TWO, headView.getItemView().getChildAt(1));
        }
    }

    @Subscribe
    public void onEvent(EventScorllTopBean bean) {
        if (bean.position == 0 && mAdapter != null && mRecyclerView != null) {
            mRecyclerView.scrollToPosition(0);
        }
    }

    @Subscribe
    public void onEvent(EventThumbsupBean bean) {
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).getData().getId().equals(bean.newsId)) {
                mDatas.get(i).getData().setIsThumbsup(bean.isThumbsup);
                mRecyclerView.notifyItemChanged(i);
            }
        }
    }

    @Subscribe
    public void onEvent(EventCollectBean bean) {
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).getData().getId().equals(bean.newsId)) {
                mDatas.get(i).getData().setIsCollect(bean.isCollect);
                mRecyclerView.notifyItemChanged(i);
            }
        }
    }

    public void onEvent(EventLoadBean bean) {
        getBaseLoadingView().show(bean.isLoading);
    }

    @Override
    public void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }


    @Override
    public void onDestroy() {
        EventBusUtil.unregister(this);
        if (mPresenter != null)
            mPresenter.destroy();
        if (mShareHandler != null)
            mShareHandler.destroy();
        super.onDestroy();
    }

    @Override
    protected void lazyLoad() {
        if (mPresenter == null) {
            new NewsItemPresenter(this, String.valueOf(id), flag, userCode, STATISTICS);
        }
        readCache();
    }

    //先拿缓存显示上去 再去请求网络
    private void readCache() {
        Observable.just(NewsItemFragment.class.getSimpleName() + id)
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, ListNewNewsBean>() {
                    @Override
                    public ListNewNewsBean call(String key) {
                        ListNewNewsBean bean = (ListNewNewsBean) ACache.get(MyApplication.getInstance()).getAsObject(key);
                        return bean;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ListNewNewsBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ACache.get(MyApplication.getInstance()).remove(NewsItemFragment.class.getSimpleName() + id);
                        mPresenter.getData(false);
                    }

                    @Override
                    public void onNext(ListNewNewsBean bean) {
                        if (bean != null)
                            showData(bean.getNewNewsBeen());
                        mPresenter.getData(false);
                    }
                });
    }


}
