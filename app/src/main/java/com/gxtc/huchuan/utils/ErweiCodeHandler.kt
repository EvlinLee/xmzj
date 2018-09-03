package com.gxtc.huchuan.utils

import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.bean.CheckBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.service.AllApi
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by zzg on 2018/2/8.
 */

class ErweiCodeHandler() {

    companion object {
        @JvmStatic
        fun  getInstant() = Holder.INSTANT
    }

    private object Holder {
        val INSTANT = ErweiCodeHandler()
    }

    fun getBaseUrl(obj:Any,callBack: ApiCallBack<Any>){
        val subscription = AllApi.getInstance()
                .getBaseUrl(UserManager.getInstance().token,6,0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver(callBack))
        RxTaskHelper.getInstance().addTask(obj,subscription)
    }

    fun getUrl(obj:Any,callBack: ApiCallBack<Any>){
        val subscription = AllApi.getInstance()
                .getUrl(6)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver(callBack))
        RxTaskHelper.getInstance().addTask(obj,subscription)
    }

    fun destroy(obj:Any){
        RxTaskHelper.getInstance().cancelTask(obj)
    }

}