package com.gxtc.huchuan.data

import com.gxtc.commlibrary.data.BaseRepository
import com.gxtc.huchuan.bean.CoustomMerBean
import com.gxtc.huchuan.bean.OrderDetailBean
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.MallApi
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by zzg on 2017/11/4.
 */
class MallOrderDetailRepository : BaseRepository(),MallOrderDetailSource{
    override fun getOrderDetail(token: String, orderNo: String, callBack: ApiCallBack<OrderDetailBean>) {
        addSub(MallApi.getInstance().getOrderDetail(token,orderNo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<OrderDetailBean>>(callBack)))
    }
    override fun deleteOrder(token: String, orderNo: String, mCallBack: ApiCallBack<Any>) {
        addSub(MallApi.getInstance().deleteOrder(token,orderNo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<Any>>(mCallBack)))
    }

    override fun confirmOrder(token: String, orderNo: String, mCallBack: ApiCallBack<Any>) {
        addSub(MallApi.getInstance().confirmOrder(token,orderNo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<Any>>(mCallBack)))
    }
}