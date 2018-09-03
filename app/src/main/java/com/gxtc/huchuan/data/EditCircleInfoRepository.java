package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.CircleInfoBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.ui.mine.circle.EditCircleInfoContract;

import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/5/10 .
 */

public class EditCircleInfoRepository extends BaseRepository implements EditCircleInfoContract.Source{

    @Override
    public void getGroupDesc(int groupId, ApiCallBack<CircleInfoBean> callBack) {
        addSub(CircleApi.getInstance().getGroupDesc(groupId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<CircleInfoBean>>(callBack)));
    }

    @Override
    public void saveGroupDesc(HashMap<String, Object> map, ApiCallBack<Object> callBack) {
        addSub(CircleApi.getInstance().saveGroupDesc(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }
}
