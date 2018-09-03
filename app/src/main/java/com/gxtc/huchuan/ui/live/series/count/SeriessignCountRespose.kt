package com.gxtc.huchuan.ui.live.series.count

import com.gxtc.commlibrary.data.BaseRepository
import com.gxtc.huchuan.bean.ChatInfosBean
import com.gxtc.huchuan.bean.ChatJoinBean
import com.gxtc.huchuan.bean.SeriseCountBean
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.LiveApi
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.ArrayList

/**
 * Created by zzg on 2017/12/12.
 */
class SeriessignCountRespose :BaseRepository(),SeriseSignCountSource{

    override fun getSeriseSignCount(token: String, id: String, type: String, start: Int, searchKey: String, mCallBack: ApiCallBack<ArrayList<SeriseCountBean>>) {
        addSub(LiveApi.getInstance().getSeriseCount(token,id, start, type,searchKey).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<ArrayList<SeriseCountBean>>>(mCallBack)))
    }

    override fun getlistJoinMember(map :HashMap<String, String>, mCallBack: ApiCallBack<ArrayList<ChatJoinBean.MemberBean>>) {
        addSub(LiveApi.getInstance().getlistJoinMember(map).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<ArrayList<ChatJoinBean.MemberBean>>>(mCallBack)))
    }

}