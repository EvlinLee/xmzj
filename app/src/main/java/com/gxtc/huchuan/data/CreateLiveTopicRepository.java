package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.CreateLiveTopicBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.createtopic.CreateTopicContract;

import java.util.HashMap;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/3/20.
 */

public class CreateLiveTopicRepository extends BaseRepository implements CreateTopicContract.Source {

    @Override
    public void createLiveTopic(HashMap<String, String> map, ApiCallBack<CreateLiveTopicBean> callBack) {
        addSub(MineApi.getInstance().saveChatTopic(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<CreateLiveTopicBean>>(callBack)));
    }
}
