package com.gxtc.huchuan.data.deal;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.AllApi;
import com.gxtc.huchuan.ui.circle.topic.CircleDynamicManagerContract;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjr on 2017/2/15.
 * 动态管理
 */

public class CircleDynamicManagerRepository extends BaseRepository implements CircleDynamicManagerContract.Source {

    @Override
    public void getData(int groudId, final int start,long loadTime, final ApiCallBack<List<CircleHomeBean>> callBack) {
        String token = null;
        if (UserManager.getInstance().isLogin()) {
            token = UserManager.getInstance().getToken();
        }
        Subscription sub = AllApi.getInstance().listByGroup(groudId, token, start, 15,loadTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<CircleHomeBean>>>(callBack));
        addSub(sub);
    }

}
