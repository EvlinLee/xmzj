package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.commlibrary.utils.ACache;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.NewsAdsBean;
import com.gxtc.huchuan.bean.UnifyClassBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.ui.live.LiveContract;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/2/8.
 *
 */

public class LiveRepository extends BaseRepository implements LiveContract.Source {

    @Override
    public void getDatas(String token,int start,String type, ApiCallBack<ArrayList<ChatInfosBean>> apiCallBack) {
        addSub(LiveApi.getInstance().getChatRoomHomePageInfo(token,start,type).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<ArrayList<ChatInfosBean>>>(apiCallBack)));
    }

    @Override
    public void getAdvertise(String token, ApiCallBack<List<NewsAdsBean>> callBack) {
        addSub(AllApi.getInstance().getNewsAds("05").subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe(new ApiObserver<ApiResponseBean<List<NewsAdsBean>>>(callBack)));
    }

    @Override
    public void getDataHistory(String key, final LiveContract.CallBack callBack) {
        Observable.just(key)
                  .map(new Func1<String, ArrayList<ChatInfosBean>>() {
                      @Override
                      public ArrayList<ChatInfosBean> call(String key) {
                          ArrayList<ChatInfosBean> infosBeen = (ArrayList<ChatInfosBean>) ACache.get(MyApplication.getInstance()).getAsObject(key);
                          return infosBeen;
                      }
                  })
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Action1<ArrayList<ChatInfosBean>>() {
                      @Override
                      public void call(ArrayList<ChatInfosBean> data) {
                        callBack.successful(data);
                      }
                  });
    }

    @Override
    public void saveDataHistory(final String key, ArrayList<ChatInfosBean> data) {
        Observable.just(data)
                  .subscribeOn(Schedulers.io())
                  .observeOn(Schedulers.io())
                  .subscribe(new Action1<ArrayList<ChatInfosBean>>() {
                        @Override
                        public void call(ArrayList<ChatInfosBean> chatInfosBeen) {
                            ACache.get(MyApplication.getInstance()).put(key, chatInfosBeen);
                        }
                    });
    }

    @Override
    public void getLiveRoom(String token, int start, String typeId,String clickType, ApiCallBack<List<UnifyClassBean>> apiCallBack) {

        //id是-1就获取推荐的课程
        if("-1".equals(typeId)){
            addSub(LiveApi.getInstance()
                          .getClassroomIndexRecomment(start)
                          .subscribeOn(Schedulers.io())
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(new ApiObserver<ApiResponseBean<List<UnifyClassBean>>>(apiCallBack)));

        //其他的话获取对应分类的课程
        }else{
            addSub(LiveApi.getInstance()
                         .getClassroomByType(typeId,start,clickType)
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(new ApiObserver<ApiResponseBean<List<UnifyClassBean>>>(apiCallBack)));
        }

    }


}
