package com.gxtc.huchuan.ui.circle.classroom;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.commlibrary.utils.ACache;
import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ClassAndSeriseBean;
import com.gxtc.huchuan.bean.LiveHeadPageBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.CircleApi;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.ui.live.LiveContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/2/8.
 */

public class ClassroomRepository extends BaseRepository implements ClassroomContract.Source {

    @Override
    public void getDatas(HashMap<String, String> map, ApiCallBack<List<ChatInfosBean>> apiCallBack) {
        addSub(CircleApi.getInstance().getUserChatInfoList(map).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ApiObserver<ApiResponseBean<List<ChatInfosBean>>>(apiCallBack)));
    }

    @Override
    public void loadmoreDatas(HashMap<String, String> map,
            ApiCallBack<List<ChatInfosBean>> apiCallBack) {

    }

    @Override
    public void getDataHistory(String key, final LiveContract.CallBack callBack) {


    }

    @Override
    public void saveDataHistory(final String key, List<ChatInfosBean> data) {
    }

    @Override
    public void getUnauditGroup(HashMap<String, String> map,
                                ApiCallBack<List<ClassAndSeriseBean>> apiCallBack) {
        addSub(CircleApi.getInstance().getUnauditGroupInfoList(map).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ApiObserver<ApiResponseBean<List<ClassAndSeriseBean>>>(apiCallBack)));

    }

    @Override
    public void searchClass(HashMap<String, String> map, ApiCallBack<List<ClassAndSeriseBean>> apiCallBack) {
        addSub(CircleApi.getInstance()
                        .searchClass(map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ApiObserver<ApiResponseBean<List<ClassAndSeriseBean>>>(apiCallBack)));
    }


}
