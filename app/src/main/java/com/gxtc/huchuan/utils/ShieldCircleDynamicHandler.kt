package com.gxtc.huchuan.utils

import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.service.CircleApi
import com.luck.picture.lib.rxbus2.Subscribe
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by zzg on 2018/3/5.
 * type 1 屏蔽圈子的动态 0 解除;
 */
class ShieldCircleDynamicHandler {
    var subscription:Subscription? = null

    companion object {
        @JvmStatic
        fun getInstant() = Holder.Instant
    }

    object Holder{
        val Instant = ShieldCircleDynamicHandler()
    }

    fun receiveDynamic(token:String,groupId:Int,type:Int,callBack: ApiCallBack<Any>){
        subscription = CircleApi.getInstance().receiveDynamic(token, groupId, type).observeOn(
                AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<Any>(callBack))
    }

    fun addTask(tag:Any){
        RxTaskHelper.getInstance().addTask(tag,subscription)
    }

    fun cancelTask(tag:Any){
        RxTaskHelper.getInstance().cancelTask(tag)
    }
}