package com.gxtc.huchuan.ui.live.liveroomlist;

import android.util.Log;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.bean.FollowLecturerBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.event.EventEditInfoBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Gubr on 2017/3/31.
 */

public class LiveRoomListSource extends BaseRepository implements LiveRoomListContract.source {
    @Override
    public void getDats(HashMap<String, String> map, ApiCallBack<List<LiveRoomBean>> callback) {
        addSub(LiveApi.getInstance().getChatRoomList(map).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<LiveRoomBean>>>(callback)));
    }

    @Override
    public void getFollowDats(HashMap<String, String> map,
            ApiCallBack<List<FollowLecturerBean>> callback) {
        addSub(LiveApi.getInstance().getFollowChatRoomList(map).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<List<FollowLecturerBean>>>(callback)));
    }

    @Override
    public void changeFolow(String token, String id, ApiCallBack<Void> callBack) {
        addSub(LiveApi.getInstance().setUserFollow(token, "2", id).subscribeOn(
                Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ApiObserver<ApiResponseBean<Void>>(callBack)));
    }
}
