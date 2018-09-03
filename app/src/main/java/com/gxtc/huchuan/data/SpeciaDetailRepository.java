package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.SpecialDetailBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.http.service.SpecialApi;
import com.gxtc.huchuan.ui.news.NewsCollectContract;
import com.gxtc.huchuan.ui.special.SpecialDetailContract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2018/5/25.
 * 专题详情
 */

public class SpeciaDetailRepository extends BaseRepository implements SpecialDetailContract.Source {


    @Override
    public void getData(String id, ApiCallBack<SpecialBean> callBack) {
        String token = UserManager.getInstance().getToken();


        Subscription sub = SpecialApi.getInstance().getSpecialDetail(token, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<SpecialBean>>(callBack));
        addSub(sub);
    }

    /**
     * 订阅专题
     *
     * @param id
     * @param callBack
     */
    @Override
    public void getSubscription(String id, ApiCallBack<Void> callBack) {
        String token = UserManager.getInstance().getToken();

        Subscription sub = SpecialApi.getInstance().subscription(token, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Void>>(callBack));
        addSub(sub);
    }

    @Override
    public void collectSpecia(String bizId, ApiCallBack<Object> callBack) {
        String token = UserManager.getInstance().getToken();
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("bizType", "13");
        map.put("bizId", bizId);
        Subscription sub = AllApi.getInstance().saveCollection(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack));
        addSub(sub);
    }

}
