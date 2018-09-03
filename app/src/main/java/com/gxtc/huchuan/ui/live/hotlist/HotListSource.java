package com.gxtc.huchuan.ui.live.hotlist;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ClassHotBean;
import com.gxtc.huchuan.bean.ClassLike;
import com.gxtc.huchuan.bean.UnifyClassBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/3/30.
 */

public class HotListSource extends BaseRepository implements HotListContract.Source {


    @Override
    public void getData(HashMap<String,String> map,ApiCallBack<List<UnifyClassBean>> callBack) {
      addSub( LiveApi.getInstance().getHotClassroom(map)
                     .subscribeOn(Schedulers.io())
                     .observeOn(AndroidSchedulers.mainThread())
                     .subscribe(new ApiObserver<ApiResponseBean<List<UnifyClassBean>>>(callBack)));

    }

    @Override
    public void getboutiqueData(HashMap<String,String> map,ApiCallBack<List<ClassLike>> callBack) {
        addSub( LiveApi.getInstance().getClassLike(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<ClassLike>>>(callBack)));

    }

    @Override
    public void getHotData(HashMap<String,String> map,ApiCallBack<List<ClassHotBean>> callBack) {
        addSub( LiveApi.getInstance().getClassHotroom(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<ClassHotBean>>>(callBack)));

    }
}
