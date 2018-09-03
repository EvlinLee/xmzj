package com.gxtc.huchuan.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.recyclerview.RecyclerView;
import com.gxtc.commlibrary.utils.ACache;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.NewsBannerAdapter;
import com.gxtc.huchuan.adapter.NewsHeadRecyleViewAdapter;
import com.gxtc.huchuan.bean.DealTypeBean;
import com.gxtc.huchuan.bean.ListNewsHeadBean;
import com.gxtc.huchuan.bean.NewsAdsBean;
import com.gxtc.huchuan.bean.NewsHeadBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.circle.findCircle.CircleListActivity;
import com.gxtc.huchuan.ui.deal.DealActivity;
import com.gxtc.huchuan.ui.deal.deal.hotdeallist.HotDealListActivity;
import com.gxtc.huchuan.ui.live.LiveActivity;
import com.gxtc.huchuan.ui.live.hotlist.HotListActivity;
import com.gxtc.huchuan.ui.mall.MallActivity;
import com.gxtc.huchuan.ui.mine.focus.FocusActivity;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.news.NewsItemFragment;
import com.gxtc.huchuan.ui.news.VideoNewsActivity;
import com.gxtc.huchuan.utils.AdClickUtil;
import com.gxtc.huchuan.utils.StatisticsHandler;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.gxtc.huchuan.utils.StatisticsHandler.STRING_CHANEL_DEAL;
import static com.gxtc.huchuan.utils.StatisticsHandler.STRING_CHANEL_FIND_CIRCLE;
import static com.gxtc.huchuan.utils.StatisticsHandler.STRING_CHANEL_HOT_CLASS;
import static com.gxtc.huchuan.utils.StatisticsHandler.STRING_CHANEL_MAll;
import static com.gxtc.huchuan.utils.StatisticsHandler.STRING_CHANEL_NEW_VIDEO;

/**
 * Created by sjr on 2017/2/20.
 * 新闻头部布局
 * 2017/3/16
 * 头部八个数据换成单行滚动，注释了以前代码，资讯改了不知道多少个版本...
 * 2017/4/19
 * 头部只有五个数据，不能滚动
 */

public class NewsHeadView extends LinearLayout implements NewsItemFragment.RefreshLisner{
    public NewsItemFragment mNewsItemFragment;
    private com.gxtc.commlibrary.recyclerview.RecyclerView mRecyclerView;
    private android.support.v7.widget.RecyclerView rvNewsHomeItem;
    ConvenientBanner mBannerView;
    private Context mContext;
    private Activity mActivity;

//    private String[] title = {"最新视频", "商城工具", "分享赚钱", "我的好友", "我的关注"};
    private String[] title = {"最新视频", "营销工具", "热门课程", "发现圈子", "资源交易"};

    Subscription sub;

    public NewsHeadView(Activity activity, Context context, RecyclerView mRecyclerView,NewsItemFragment mNewsItemFragment) {
        super(context);
        this.mRecyclerView = mRecyclerView;
        this.mContext = context;
        this.mActivity = activity;
        this.mNewsItemFragment = mNewsItemFragment;
        initView();
        readCache();
        initHeadRecycleView();

    }


    //先从本地读缓存填充上去 再去请求网络
    private void readCache() {
        Observable.just("news_home_head_bean")
                  .subscribeOn(Schedulers.io())
                  .map(new Func1<String, ListNewsHeadBean>() {
                      @Override
                      public ListNewsHeadBean call(String key) {
                          ListNewsHeadBean bean = (ListNewsHeadBean) ACache.get(MyApplication.getInstance()).getAsObject(key);
                          return bean;
                      }
                  })
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Subscriber<ListNewsHeadBean>() {
                      @Override
                      public void onCompleted() {

                      }

                      @Override
                      public void onError(Throwable e) {
                          ACache.get(MyApplication.getInstance()).remove("news_home_head_bean");
                          initAdvertise();
                      }

                      @Override
                      public void onNext(ListNewsHeadBean bean) {
                          if (bean != null && bean.getAdvertise() != null && bean.getAdvertise().size() > 0)
                              showHeadView(bean.getAdvertise());
                          initAdvertise();
                      }
                  });
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_news_head, mRecyclerView, false);
        mBannerView = (ConvenientBanner) view.findViewById(R.id.cb_news_banner);
        rvNewsHomeItem = (android.support.v7.widget.RecyclerView) view.findViewById(R.id.rv_news_home_item);

        int height = (int) WindowUtil.getScaleHeight(16,7.5f,mContext);
        LayoutParams params = (LayoutParams) mBannerView.getLayoutParams();
        params.height = height;

        this.addView(view);

        mNewsItemFragment.setRefreshLisner(this);
    }


    /**
     * 初始化轮播图数据
     */
    private void initAdvertise() {
        sub = AllApi.getInstance().getNewsAds("02")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<NewsAdsBean>>>(new ApiCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        final List<NewsAdsBean> advertise = (List<NewsAdsBean>) data;
                        showHeadView(advertise);

                        final ListNewsHeadBean bean = new ListNewsHeadBean();
                        bean.setAdvertise(advertise);
                        Observable.just(bean).subscribeOn(Schedulers.io()).subscribe(
                                new Action1<ListNewsHeadBean>() {
                                    @Override
                                    public void call(ListNewsHeadBean bean) {
                                        ACache.get(MyApplication.getInstance()).put("news_home_head_bean", bean);
                                    }
                                });
                    }

                    @Override
                    public void onError(String errorCode, String message) {
                        if (errorCode.equals(ApiObserver.NET_ERROR)||errorCode.equals(ApiObserver.SERVER_ERROR)) {
                            Observable.just("news_home_head_bean")
                                      .subscribeOn(Schedulers.io())
                                      .map(new Func1<String, ListNewsHeadBean>() {
                                        @Override
                                        public ListNewsHeadBean call(String key) {
                                            ListNewsHeadBean bean = (ListNewsHeadBean) ACache.get(MyApplication.getInstance()).getAsObject(key);
                                            return bean;
                                        }
                                        })
                                      .observeOn(AndroidSchedulers.mainThread())
                                      .subscribe(new Action1<ListNewsHeadBean>() {
                                            @Override
                                            public void call(ListNewsHeadBean bean) {
                                                if (bean != null && bean.getAdvertise() != null && bean.getAdvertise().size() > 0)
                                                    showHeadView(bean.getAdvertise());
                                            }
                                      });
                        }
                        ToastUtil.showShort(mContext, message);
                    }
                }));

    }

    private void showHeadView(final List<NewsAdsBean> advertise) {
        mBannerView.startTurning(5000);
        mBannerView.setPages(new CBViewHolderCreator<NewsBannerAdapter>() {
            @Override
            public NewsBannerAdapter createHolder() {
                return new NewsBannerAdapter();
            }
        }, advertise);
        //设置指示点样式
        mBannerView.setPageIndicator(new int[]{R.drawable.news_icon_dot_small, R.drawable.news_icon_dot_big});
        //设置指示点对其方式
        mBannerView.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
        mBannerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                AdClickUtil.performClick(mActivity,advertise.get(position));
            }
        });

    }
    //是否显示全部菜单
//    boolean isShowAll = false;

    /**
     * 头部八个数据
     */
    private void initHeadRecycleView() {
        final List<NewsHeadBean> list = new ArrayList<>();
        final int[] iv = {R.drawable.news_icon_nav_1, R.drawable.news_icon_nav_2
                , R.drawable.tabbar_news_rmkc, R.drawable.news_icon_nav_4, R.drawable.news_icon_nav_5};
        for (int i = 0; i < title.length; i++) {
            NewsHeadBean bean = new NewsHeadBean(iv[i], title[i]);
            list.add(bean);
        }
        NewsHeadRecyleViewAdapter recycleAdapter = new NewsHeadRecyleViewAdapter(mContext, list,
                R.layout.item_news_head_recyclerview);
        //这是可以滚动时的代码
        //StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        GridLayoutManager manager = new GridLayoutManager(mContext, 5);
        rvNewsHomeItem.setLayoutManager(manager);
        rvNewsHomeItem.setAdapter(recycleAdapter);
        recycleAdapter.setOnReItemOnClickListener(new BaseRecyclerAdapter.OnReItemOnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                DealTypeBean bean = new DealTypeBean();
                Map<String,String> map = new HashMap<>();
                switch (position) {
                    //最新视频
                    case 0:
                        StatisticsHandler.Companion.getInstant().handleStatisticsByType(STRING_CHANEL_NEW_VIDEO,false);
                        GotoUtil.goToActivity(mActivity, VideoNewsActivity.class);
                        break;

                    //商城工具
                    case 1:
                        StatisticsHandler.Companion.getInstant().handleStatisticsByType(STRING_CHANEL_MAll,false);
                        GotoUtil.goToActivity(mActivity, MallActivity.class);
                        break;

                    //热门课程
                    case 2:
                        //gotoHotListActivity("热门课程", "0");
                        StatisticsHandler.Companion.getInstant().handleStatisticsByType(STRING_CHANEL_HOT_CLASS,false);
                        GotoUtil.goToActivity(mActivity, LiveActivity.class);
                        break;

                    //热门圈子
                    case 3:
                        StatisticsHandler.Companion.getInstant().handleStatisticsByType(STRING_CHANEL_FIND_CIRCLE,false);
                        GotoUtil.goToActivity(mActivity, CircleListActivity.class);
                        break;

                    //人气交易
                    case 4:
                        //GotoUtil.goToActivity(mActivity, HotDealListActivity.class);
                        StatisticsHandler.Companion.getInstant().handleStatisticsByType(STRING_CHANEL_DEAL,false);
                        GotoUtil.goToActivity(mActivity, DealActivity.class);
                        break;

                }
            }
        });

    }

    private void gotoHotListActivity(String title, String type) {
        HashMap<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("type", type);
        GotoUtil.goToActivityWithData(mActivity, HotListActivity.class, map);
    }

    private void goToFocusActivity(String focusFlag) {
        if (UserManager.getInstance().isLogin()) {
            Intent intent = new Intent(mActivity, FocusActivity.class);
            intent.putExtra("focus_flag", focusFlag);
            mActivity.startActivity(intent);
        } else GotoUtil.goToActivity(mActivity, LoginAndRegisteActivity.class);

    }

    public android.support.v7.widget.RecyclerView getItemView() {
        return rvNewsHomeItem;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        StatisticsHandler.Companion.getInstant().destroy();
        if (mBannerView != null) {
            mBannerView.stopTurning();
        }
    }

    @Override
    public void OnRefresh() {
        initAdvertise();
    }
}
