package com.gxtc.huchuan.ui.message

import com.gxtc.commlibrary.data.BaseRepository
import com.gxtc.huchuan.bean.MessageBean
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.MessageApi
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by zzg on 2017/9/23.
 */
class OverlookRepository : BaseRepository(),OverlookSource{
    override fun getOverlook(token: String, userCode: String, callBack: ApiCallBack<Any>) {
        addSub(
                MessageApi.getInstance()
                        .getOverlook(token,userCode)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(ApiObserver<ApiResponseBean<Any>>(callBack))
        )
    }

}