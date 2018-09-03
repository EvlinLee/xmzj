package com.gxtc.huchuan.ui.mall;

import com.gxtc.commlibrary.utils.ACache;
import com.gxtc.commlibrary.utils.ErrorCodeUtil;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.CoustomMerBean;
import com.gxtc.huchuan.bean.MallBean;
import com.gxtc.huchuan.data.MallCustomersRepository;
import com.gxtc.huchuan.data.MallRepository;
import com.gxtc.huchuan.data.MallSource;
import com.gxtc.huchuan.http.ApiCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MallPresenter implements MallContract.Presenter {

    private MallSource              mData;
    private MallCustomersRepository datas;
    private MallContract.View       mView;
    private int start = 0;
    public MallPresenter(MallContract.View mView) {
        this.mView = mView;
        this.mView.setPresenter(this);
        mData = new MallRepository();
        datas = new MallCustomersRepository();
    }


    @Override
    public void getAdvertise() {
        mData.getAdvertise(new ApiCallBack<List<MallBean>>() {
            @Override
            public void onSuccess(List<MallBean> data) {
                if(mView == null) return;
                mView.showCbBanner(data);

                Observable.just(data)
                          .subscribeOn(Schedulers.io())
                          .map(new Func1<List<MallBean>, ArrayList<MallBean>>() {
                              @Override
                              public ArrayList<MallBean> call(List<MallBean> mallBeen) {
                                  ArrayList<MallBean> temps = new ArrayList<MallBean>();
                                  temps.addAll(mallBeen);
                                  ACache.get(MyApplication.getInstance()).put(NewMallFragment.class.getSimpleName() + "ad", temps);
                                  return temps;
                              }
                          })
                          .subscribe();
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void getTags(String token) {
       mData.getTags(0,new ApiCallBack<List<MallBean>>() {
           @Override
           public void onSuccess(List<MallBean> data) {
               if(mView == null) return;
               mView.showHeadIcon(data);

               Observable.just(data)
                         .subscribeOn(Schedulers.io())
                         .map(new Func1<List<MallBean>, ArrayList<MallBean>>() {
                             @Override
                             public ArrayList<MallBean> call(List<MallBean> mallBeen) {
                                 ArrayList<MallBean> temps = new ArrayList<MallBean>();
                                 temps.addAll(mallBeen);
                                 ACache.get(MyApplication.getInstance()).put(NewMallFragment.class.getSimpleName() + "tag", temps);
                                 return temps;
                             }
                         })
                         .subscribe();
           }

           @Override
           public void onError(String errorCode, String message) {
               if(mView == null) return;
               ErrorCodeUtil.handleErr(mView,errorCode,message);
           }
       });
    }

    @Override
    public void getLinesData(String token, int start) {
         mData.getLinesData(0,new ApiCallBack<List<MallBean>>() {
             @Override
             public void onSuccess(List<MallBean> data) {
                 if(mView == null) return;
                 mView.showLinesData(data);

                 Observable.just(data)
                           .subscribeOn(Schedulers.io())
                           .map(new Func1<List<MallBean>, ArrayList<MallBean>>() {
                               @Override
                               public ArrayList<MallBean> call(List<MallBean> mallBeen) {
                                   ArrayList<MallBean> temps = new ArrayList<MallBean>();
                                   temps.addAll(mallBeen);
                                   ACache.get(MyApplication.getInstance()).put(NewMallFragment.class.getSimpleName() + "lines", temps);
                                   return temps;
                               }
                           })
                           .subscribe();
             }

             @Override
             public void onError(String errorCode, String message) {
                 if(mView == null) return;
                 ErrorCodeUtil.handleErr(mView,errorCode,message);
             }
         });
    }

    @Override
    public void getGridData(boolean isLoadMore) {
        if(isLoadMore){
            start = start +15;
        }else {
            start = 0;
        }
        mData.getGridData(start,new ApiCallBack<List<MallBean>>() {
            @Override
            public void onSuccess(List<MallBean> data) {
                if(mView == null) return;
                mView.showGridData(data);

                Observable.just(data)
                        .subscribeOn(Schedulers.io())
                        .map(new Func1<List<MallBean>, ArrayList<MallBean>>() {
                            @Override
                            public ArrayList<MallBean> call(List<MallBean> mallBeen) {
                                ArrayList<MallBean> temps = new ArrayList<MallBean>();
                                temps.addAll(mallBeen);
                                ACache.get(MyApplication.getInstance()).put(NewMallFragment.class.getSimpleName() + "goods", temps);
                                return temps;
                            }
                        })
                        .subscribe();
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void addShopCar(HashMap<String, String> map) {
        mData.addShopCart(map, new ApiCallBack<Object>() {

            @Override
            public void onSuccess(Object data) {
                if(mView == null) return;
                mView.showAddShopCartResult(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void getCustermersList(String type,String rand) {
        datas.getCustermersList(type,rand, new ApiCallBack<ArrayList<CoustomMerBean>>() {

            @Override
            public void onSuccess(ArrayList<CoustomMerBean> data) {
                if(mView == null) return;
                mView.showCustermersList(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }

    @Override
    public void getActivitysData(String token, int start) {
        mData.getActivityData(0,new ApiCallBack<List<MallBean>>() {
            @Override
            public void onSuccess(List<MallBean> data) {
                if(mView == null) return;
                mView.showActivitysData(data);
            }

            @Override
            public void onError(String errorCode, String message) {
                if(mView == null) return;
                ErrorCodeUtil.handleErr(mView,errorCode,message);
            }
        });
    }


    @Override
    public void start() {}

    @Override
    public void destroy() {
        mData.destroy();
        datas.destroy();
        mView = null;
    }
}
