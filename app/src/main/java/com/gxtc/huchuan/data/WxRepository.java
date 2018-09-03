package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.WxResponse;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.service.WxApi;

import java.util.HashMap;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Steven on 17/3/24.
 */

public class WxRepository extends BaseRepository implements WxSource{

    @Override
    public void login(HashMap<String, String> map, ApiCallBack<WxResponse> callBack) {
        Subscription sub = WxApi.getInstance().login(map)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new ApiObserver<WxResponse>(callBack));

        addSub(sub);
    }
}
