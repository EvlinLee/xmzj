package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.UnifyClassBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.classroom.history.MyHistoryContract;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ALing on 2017/3/28 .
 *
 */

public class MyHistoryResitory extends BaseRepository implements MyHistoryContract.Source {

    @Override
    public void getData(int start,String  clickType, ApiCallBack<List<UnifyClassBean>> callBack) {
        String token = UserManager.getInstance().getToken();

        Subscription sub = LiveApi.getInstance().getBuyedClassroom(token, start,clickType)
                                  .subscribeOn(Schedulers.io())
                                  .observeOn(AndroidSchedulers.mainThread())
                                  .subscribe(new ApiObserver<ApiResponseBean<List<UnifyClassBean>>>(callBack));
        addSub(sub);

    }

    @Override
    public void deleteChatUserRecord(String token, ArrayList<String> list, ApiCallBack<List<UnifyClassBean>> callBack) {
        Subscription sub = MineApi.getInstance().deleteChatUserRecord(token,list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<UnifyClassBean>>>(callBack));
        addSub(sub);
    }
}
