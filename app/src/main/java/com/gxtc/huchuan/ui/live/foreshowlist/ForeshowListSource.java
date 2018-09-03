package com.gxtc.huchuan.ui.live.foreshowlist;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.ui.live.hotlist.HotListContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/3/30.
 */

public class ForeshowListSource extends BaseRepository implements ForeshowListContract.Source {
    @Override
    public void getData(HashMap<String,String> map,ApiCallBack<ArrayList<ChatInfosBean>> callBack) {
        String token = map.get("token");
        String start = map.get("start");
        addSub( LiveApi.getInstance().getChatRoomHomePageInfo(token,Integer.valueOf(start),"0")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<ArrayList<ChatInfosBean>>>(callBack)));

    }
}
