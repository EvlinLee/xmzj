package com.gxtc.huchuan.ui.live;

import android.text.TextUtils;

import com.gxtc.commlibrary.utils.ACache;
import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ClassHotBean;
import com.gxtc.huchuan.bean.ClassLike;
import com.gxtc.huchuan.bean.NewsAdsBean;
import com.gxtc.huchuan.bean.UnifyClassBean;
import com.gxtc.huchuan.data.LiveRepository;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.ui.MainActivity;
import com.gxtc.huchuan.ui.live.hotlist.HotListSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.gxtc.huchuan.ui.MainActivity.STATISTICS;
import static com.gxtc.huchuan.ui.MainActivity.STATISTICS_EMTPT;

/**
 * Created by Gubr on 2017/2/8.
 */

public class LivePresenter implements LiveContract.Presenter {

    private  String clickType;
    private LiveContract.Source mData;
    private HotListSource hotListSource;
    private LiveContract.View   mView;

    private List<UnifyClassBean> cacheDatas;

    public LivePresenter(LiveContract.View mView, String clickType) {
        this.mView = mView;
        this.mView.setPresenter(this);
        this.clickType = clickType;
        mData = new LiveRepository();
        cacheDatas = new ArrayList<>();
        hotListSource = new HotListSource();
    }


    @Override
    public void getAdvertise() {
        getAdvertise(null);
    }

    @Override
    public void getAdvertise(String token) {
        mData.getAdvertise(token, new ApiCallBack<List<NewsAdsBean>>() {
            @Override
            public void onSuccess(List<NewsAdsBean> data) {
                if (mView == null) return;
                mView.showCbBanner(data);
                NewsAdsBean bean = new NewsAdsBean();
                bean.setDatas(data);
                Observable.just(bean).subscribeOn(Schedulers.io()).subscribe(new Action1<NewsAdsBean>() {
                    @Override
                    public void call(NewsAdsBean newsAdsBeen) {
                        ACache.get(MyApplication.getInstance()).put("live_home_head_bean", newsAdsBeen);
                    }
                });
            }

            @Override
            public void onError(String errorCode, String message) {
                if (errorCode.equals(ApiObserver.UNKONW_SERVER_ERROR)||errorCode.equals(ApiObserver.SERVER_ERROR)) {
                    Observable.just("live_home_head_bean").map(new Func1<String, NewsAdsBean>() {

                        @Override
                        public NewsAdsBean call(String s) {
                            NewsAdsBean cacheData = (NewsAdsBean) ACache.get(MyApplication.getInstance()).getAsObject(s);
                            return cacheData  ;
                        }
                    }).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<NewsAdsBean>() {
                                @Override
                                public void call(NewsAdsBean newsAdsBeen) {
                                    if(newsAdsBeen == null){
                                        mView.showEmpty();
                                        return;
                                    }
                                    mView.showCbBanner(newsAdsBeen.getDatas());
                                }
                            });
                }
            }
        });
    }

    @Override
    public void getLiveRoom(String token, final int start, boolean isRefresh , final String typeId) {

        if (mView == null) return;
        if (start == 0 ) {
            mView.showLoad();
        }

        if(isRefresh){
            clickType = STATISTICS_EMTPT;
        }else {
            clickType = STATISTICS;
        }

        if(cacheDatas.size() != 0 && !isRefresh){
            mView.showLoadFinish();
            mView.showLiveRoom(cacheDatas);
            return;
        }

        mData.getLiveRoom(token, start,typeId,clickType, new ApiCallBack<List<UnifyClassBean>>() {
            @Override
            public void onSuccess(List<UnifyClassBean> data) {
                if (mView == null) return;
                mView.showLoadFinish();
                if (start == 0 && data.size() > 0) {
                    mView.showLiveRoom(data);
                    cacheDatas.clear();
                    cacheDatas.addAll(data);
                    UnifyClassBean bean = new UnifyClassBean();
                    Observable.just(bean).subscribeOn(Schedulers.io()).subscribe(new Action1<UnifyClassBean>() {
                        @Override
                        public void call(UnifyClassBean chatInfosBean) {
                            ACache.get(MyApplication.getInstance()).put(LiveFragment.class.getSimpleName() + typeId +"list", chatInfosBean);
                        }
                    });

                }
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
                if (errorCode.equals(ApiObserver.UNKONW_SERVER_ERROR)||errorCode.equals(ApiObserver.SERVER_ERROR)) {
                    Observable.just(LiveFragment.class.getSimpleName() + typeId +"list")
                              .map(new Func1<String, UnifyClassBean>() {

                                    @Override
                                    public UnifyClassBean call(String s) {
                                        UnifyClassBean cacheData = (UnifyClassBean) ACache.get(MyApplication.getInstance()).getAsObject(s);
                                        return cacheData  ;
                                    }})
                              .subscribeOn(Schedulers.io())
                              .observeOn(AndroidSchedulers.mainThread())
                              .subscribe(new Action1<UnifyClassBean>() {
                                    @Override
                                    public void call(UnifyClassBean chatInfosBean) {
                                        if(chatInfosBean == null ) return;
                                        ArrayList<UnifyClassBean> list = new ArrayList<>();
                                        list.add(chatInfosBean);
                                        mView.showLiveRoom(list);
                                    }
                              });
                }
            }
        });
    }

    @Override
    public void getLiveLoadMore(int start, String typeId) {
        String token = UserManager.getInstance().getToken();
        mData.getLiveRoom(token, start,typeId, STATISTICS_EMTPT,new ApiCallBack<List<UnifyClassBean>>() {
            @Override
            public void onSuccess(List<UnifyClassBean> data) {
                if (mView == null) return;
                if (data!= null && data.size() > 0) {
                    mView.showloadMoreLiveRoom(data);
                    cacheDatas.addAll(data);
                } else {
                    mView.showNoLoadMoreLiveRoom();
                }
            }

            @Override
            public void onError(String errorCode, String message) {
                ErrorCodeUtil.handleErr(mView, errorCode, message);
            }
        });
    }

    @Override
    public void getData(int start, int pageSize) {

        if (start == 0) mView.showLoad();

        HashMap<String, String> map = new HashMap<>();
        map.put("num", pageSize+"");
        String token = UserManager.getInstance().getToken();
        if(!TextUtils.isEmpty(token)) map.put("token", token);

        hotListSource.getboutiqueData(map, new ApiCallBack<List<ClassLike>>() {
            @Override
            public void onSuccess(List<ClassLike> data) {
                if(mView == null)   return;

                    mView.showData(data);
                    mView.showLoadFinish();
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                    mView.showLoadFinish();
            }
        });
    }

    @Override
    public void gethotData(int start, int pageSize) {
        if (start == 0) mView.showLoad();

        HashMap<String, String> map = new HashMap<>();
        map.put("start", String.valueOf(start));
        map.put("pageSize", pageSize+"");
        String token = UserManager.getInstance().getToken();
        if(!TextUtils.isEmpty(token)) map.put("token", token);

        hotListSource.getHotData(map, new ApiCallBack<List<ClassHotBean>>() {
            @Override
            public void onSuccess(List<ClassHotBean> data) {
                if(mView == null)   return;

                mView.showHotData(data);
                mView.showLoadFinish();
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                mView.showLoadFinish();
            }
        });
    }

    @Override
    public void start() {}

    @Override
    public void destroy() {
        mData.destroy();
        hotListSource.destroy();
        mView = null;
    }
}
