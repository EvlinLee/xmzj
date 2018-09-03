package com.gxtc.huchuan.data.deal;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.news.NewsCollectContract;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/2/15.
 * 新闻收藏列表
 */

public class NewsCollectRepository extends BaseRepository implements NewsCollectContract.Source {

    @Override
    public void getData(final int start, final ApiCallBack<List<NewsBean>> callBack) {
           String token = UserManager.getInstance().getToken();

        Subscription sub = AllApi.getInstance().getCollectList(token, String.valueOf(start))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<NewsBean>>>(callBack));
        addSub(sub);
    }
//    new ApiCallBack() {
//        @Override
//        public void onSuccess(Object data) {
//            List<NewsBean> datas = (List<NewsBean>) data;
//            if (datas == null && datas.size() == 0) {
//                //没有更多数据
//                callBack.onSuccess(Collections.<NewsBean>emptyList());
//            } else {
//                callBack.onSuccess(datas);
//            }
//        }
//
//        @Override
//        public void onError(String errorCode, String message) {
//            ToastUtil.showShort(MyApplication.getInstance().getApplicationContext(),
//                    message + "错误码：" + errorCode);
//        }
//    }
}
