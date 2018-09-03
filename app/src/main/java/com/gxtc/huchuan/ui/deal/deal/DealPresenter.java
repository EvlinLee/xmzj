package com.gxtc.huchuan.ui.deal.deal;

import com.gxtc.commlibrary.utils.ACache;
import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.DealData;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.NewsAdsBean;
import com.gxtc.huchuan.data.deal.DealRepository;
import com.gxtc.huchuan.data.deal.DealSource;
import com.gxtc.huchuan.helper.RxTaskHelper;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.DealApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DealPresenter implements DealContract.Presenter {

    private DealSource        mData;
    private DealContract.View mView;
    private String            type;

    public DealPresenter(DealContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new DealRepository();
    }

    @Override
    public void start() {}

    @Override
    public void destroy() {
        mData.destroy();
        mView = null;
        RxTaskHelper.getInstance().cancelTask(this);
    }


    private boolean loadmoreing = false;

    @Override
    public void loadmore(int position) {
        if (loadmoreing) {
            return;
        }
        loadmoreing = true;
        HashMap<String, String> map = new HashMap<>();
        map.put("start", "" + position);
        map.put("flag", "0");
        map.put("type",type==null?"":type);
        mData.getData(map, new ApiCallBack<DealData>() {

            @Override
            public void onSuccess(DealData data) {
                if(mView == null)   return;
                if (data.getInfos().size() > 0) {
                    mView.showloadmore(data);
                } else {
                    mView.showLoadMoreFinish();
                }
                loadmoreing = false;

            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null)   return;
                loadmoreing = false;
                mView.showError(message);
                mView.showLoadMoreFinish();
            }
        });
    }

    @Override
    public void getHomeData() {
        mView.showLoad();

        getCache("", "");

        Subscription sub =
                AllApi.getInstance()
                      .getNewsAds("04")
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())

                      //轮播图的网络请求
                      .filter(new Func1<ApiResponseBean<List<NewsAdsBean>>, Boolean>() {
                          @Override
                          public Boolean call(ApiResponseBean<List<NewsAdsBean>> datas) {
                              if(datas.getResult() == null || datas.getResult().size() == 0){
                                  mView.showEmpty();
                                  return false;
                              }
                              return true;
                          }
                      })
                      .doOnNext(new Action1<ApiResponseBean<List<NewsAdsBean>>>() {
                          @Override
                          public void call(ApiResponseBean<List<NewsAdsBean>> datas) {
                              ArrayList<NewsAdsBean> cache = new ArrayList<>();
                              cache.addAll(datas.getResult());
                              mData.putCache(DealFragment.class.getName()+"ad",cache);
                              mView.showAdvertise(cache);
                          }
                      })
                      .observeOn(Schedulers.io())

                      //列表的网络请求
                      .concatMap(new Func1<ApiResponseBean<List<NewsAdsBean>>, Observable<ApiResponseBean<DealData>>>() {
                          @Override
                          public Observable<ApiResponseBean<DealData>> call(ApiResponseBean<List<NewsAdsBean>> listApiResponseBean) {
                              HashMap<String, String> map = new HashMap<>();
                              map.put("start", "0");
                              map.put("flag", "1");
                              if (type!=null){
                                  map.put("type",type);
                              }
                              return DealApi.getInstance().getDealData(map);
                          }
                      })
                      .observeOn(AndroidSchedulers.mainThread())
                      .filter(new Func1<ApiResponseBean<DealData>, Boolean>() {
                          @Override
                          public Boolean call(ApiResponseBean<DealData> data) {
                              if(data.getResult() == null){
                                  mView.showEmpty();
                                  return false;
                              }
                              return true;
                          }
                      })
                      .subscribe(new ApiObserver<ApiResponseBean<DealData>>(new ApiCallBack<DealData>() {
                          @Override
                          public void onSuccess(DealData data) {
                              mView.showLoadFinish();
                              mView.showData(data);
                              mData.putCache(DealFragment.class.getName(),data);
                          }

                          @Override
                          public void onError(String errorCode, String message) {
                              int code = Integer.valueOf(errorCode);
                              if(code == ErrorCodeUtil.ON_NETWORK_400 || code == ErrorCodeUtil.SERVER_ERROR || ApiObserver.SERVER_TIME_OUT.equals(errorCode) || ApiObserver.UNKONW_SERVER_ERROR.equals(errorCode)){
                                  mView.showLoadFinish();
                                  mView.showError(message);
                                  getCache(errorCode,message);
                              }else{
                                  ErrorCodeUtil.handleErr(mView, errorCode, message);
                              }
                          }
                      }));

        RxTaskHelper.getInstance().addTask(this,sub);
    }

    private void getCache(final String errorCode, final String message) {
        Observable<List<NewsAdsBean>> adObs = Observable.create(new Observable.OnSubscribe<List<NewsAdsBean>>() {
            @Override
            public void call(Subscriber<? super List<NewsAdsBean>> subscriber) {
                List<NewsAdsBean> cache = (List<NewsAdsBean>) ACache.get(MyApplication.getInstance()).getAsObject(DealFragment.class.getName()+"ad");
                subscriber.onNext(cache);
            }
        });

        final Observable<DealData> listObs = Observable.create(new Observable.OnSubscribe<DealData>() {
            @Override
            public void call(Subscriber<? super DealData> subscriber) {
                DealData cacheData = (DealData) ACache.get(MyApplication.getInstance()).getAsObject(DealFragment.class.getName());
                subscriber.onNext(cacheData);
            }
        });


        Subscription sub =
                     adObs.subscribeOn(Schedulers.io())
                          .observeOn(AndroidSchedulers.mainThread())
                          .filter(new Func1<List<NewsAdsBean>, Boolean>() {
                              @Override
                              public Boolean call(List<NewsAdsBean> newsAdsBeen) {
                                  if(newsAdsBeen == null || newsAdsBeen.size() == 0){
                                      return false;
                                  }
                                  return true;
                              }
                          })
                          .doOnNext(new Action1<List<NewsAdsBean>>() {
                              @Override
                              public void call(List<NewsAdsBean> newsAdsBeen) {
                                  mView.showAdvertise(newsAdsBeen);
                              }
                          })
                          .observeOn(Schedulers.io())
                          .concatMap(new Func1<List<NewsAdsBean>, Observable<DealData>>() {
                              @Override
                              public Observable<DealData> call(List<NewsAdsBean> newsAdsBeen) {
                                  return listObs;
                              }
                          })
                          .observeOn(AndroidSchedulers.mainThread())
                          .filter(new Func1<DealData, Boolean>() {
                              @Override
                              public Boolean call(DealData data) {
                                  if(data == null){
                                      return false;
                                  }
                                  return true;
                              }
                          })
                          .subscribe(new Action1<DealData>() {
                              @Override
                              public void call(DealData data) {
                                  mView.showData(data);
                              }
                          });

        RxTaskHelper.getInstance().addTask(this,sub);
    }

    @Override
    public void getData() {
        mView.showLoad();
        HashMap<String, String> map = new HashMap<>();
        map.put("start", "0");
        map.put("flag", "1");
        if (type!=null){
            map.put("type",type);
        }
        mData.getData(map, new ApiCallBack<DealData>() {

            @Override
            public void onSuccess(final DealData data) {
                if(mView == null)   return;
                mView.showLoadFinish();
                mView.showData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                int code = Integer.valueOf(errorCode);
                if(code == ErrorCodeUtil.ON_NETWORK_400 || code == ErrorCodeUtil.SERVER_ERROR){
                    getCache(errorCode,message);
                }else{
                    ErrorCodeUtil.handleErr(mView, errorCode, message);
                }
            }
        });
    }


    @Override
    public void changeShowType(String s) {
        type = s;
        if (loadmoreing) {
            loadmoreing = false;
            mData.cancelRequest();
        }
        mView.showReLoad();
        getData();
    }
}
