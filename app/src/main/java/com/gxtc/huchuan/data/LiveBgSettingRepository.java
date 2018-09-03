package com.gxtc.huchuan.data;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.BgPicBean;
import com.gxtc.huchuan.bean.ChatJoinBean;
import com.gxtc.huchuan.bean.LiveBgSettingBean;
import com.gxtc.huchuan.bean.LiveManagerBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;
import com.gxtc.huchuan.http.service.MineApi;
import com.gxtc.huchuan.ui.mine.classroom.directseedingbackground.livebgsetting.LiveBgSettingContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Describe:
 * Created by ALing on 2017/3/21 .
 */

public class LiveBgSettingRepository extends BaseRepository implements LiveBgSettingContract.Source {
    @Override
    public void getLiveManageData(HashMap<String, String> map, ApiCallBack<LiveRoomBean> callBack) {
        addSub(MineApi.getInstance().getLiveRoom(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<LiveRoomBean>>(callBack)));
    }

    @Override
    public void saveChatRoomSetting(ApiCallBack<LiveBgSettingBean> callBack, HashMap<String, String> map) {
        addSub(MineApi.getInstance().saveChatRoomSetting(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<LiveBgSettingBean>>(callBack)));
    }

    @Override
    public void getPicList(ApiCallBack<List<BgPicBean>> callBack, HashMap<String, String> map) {
        addSub(MineApi.getInstance().getPicList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<List<BgPicBean>>>(callBack)));
    }

    @Override
    public void getManagerList(ApiCallBack<LiveManagerBean> callBack, HashMap<String, String> map) {
        addSub(MineApi.getInstance().getChatFindList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<LiveManagerBean>>(callBack)));
    }

    @Override
    public void getChatJoinList(ApiCallBack<ArrayList<ChatJoinBean.MemberBean>> callBack, HashMap<String, String> map) {
        addSub(LiveApi.getInstance().getlistJoinMember(map)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new ApiObserver<ApiResponseBean<ArrayList<ChatJoinBean.MemberBean>>>(callBack)));
    }
    @Override
    public void getChatJoinList1(ApiCallBack<ChatJoinBean> callBack, HashMap<String, String> map) {
        addSub(LiveApi.getInstance().getChatJoinList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiObserver<ApiResponseBean<ChatJoinBean>>(callBack)));
    }

}
