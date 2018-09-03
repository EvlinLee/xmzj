package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.editinfo.EditInfoContract;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ALing on 2017/2/22.
 */

public class EditInfoRepository extends BaseRepository implements EditInfoContract.Source {
    @Override
    public void getUsetInfo(ApiCallBack<User> callBack, String token) {
        addSub(MineApi.getInstance().getUserInfo(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<User>>(callBack)));
    }

    @Override
    public void getEditInfo(ApiCallBack<Object> callBack, Map<String, String> map) {
        addSub(MineApi.getInstance().editInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<Object>>(callBack)));
    }

    @Override
    public void uploadAvatar(ApiCallBack<User> callBack, RequestBody body) {
        addSub(MineApi.getInstance().UploadAvatar(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<User>>(callBack)));
    }
}
