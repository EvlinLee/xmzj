package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.ClassMyMessageBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.classroom.message.MyMessageContract;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ALing on 2017/3/10.
 */

public class MyMessageRepository extends BaseRepository implements MyMessageContract.Source{

    @Override
    public void getData(String token, String start, ApiCallBack<List<ClassMyMessageBean>> callBack) {
        addSub(MineApi.getInstance().getMessageRecordList(token,start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<ClassMyMessageBean>>>(callBack)));
    }
}
