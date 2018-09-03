package com.gxtc.huchuan.ui.live.hostpage.newhostpage;

import com.gxtc.commlibrary.data.BaseRepository;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.http.ApiCallBack;
import com.gxtc.huchuan.http.ApiObserver;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.http.service.LiveApi;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zzg on 2017/12/18.
 */

public class ClassAndSeriseResposery extends BaseRepository implements ClassAndSeriseSourse.Source {
    @Override
    public void getData(String chatRoomId, String token, String start,
                        ApiCallBack<List<ChatInfosBean>> callBack) {
        addSub(LiveApi.getInstance().getChatRoomByChatInfoAndChatSeries(
                chatRoomId,token,start).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(new ApiObserver<ApiResponseBean<List<ChatInfosBean>>>(callBack)));
    }
}
