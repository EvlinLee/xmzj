package com.gxtc.huchuan.ui.live.hostpage

import com.gxtc.commlibrary.data.BaseRepository
import com.gxtc.huchuan.bean.CreateLiveBean
import com.gxtc.huchuan.bean.LiveRoomBean
import com.gxtc.huchuan.bean.OrderDetailBean
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.LiveApi
import com.gxtc.huchuan.http.service.MallApi
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by zzg on 2017/12/15.
 */
class LiveHostPageResposery :BaseRepository(),LiveHostSource{

    override fun saveChatRoom(map: HashMap<String, String>, callBack: ApiCallBack<CreateLiveBean>) {
        addSub(LiveApi.getInstance().saveChatRoom(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<CreateLiveBean>>(callBack)))
    }
}