package com.gxtc.huchuan.data

import com.gxtc.commlibrary.data.BaseSource
import com.gxtc.huchuan.bean.OrderDetailBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2017/11/4.
 */
interface MallOrderDetailSource :BaseSource {
    fun getOrderDetail(token:String,orderNo:String,callBack: ApiCallBack<OrderDetailBean>)

    fun deleteOrder(token: String, orderNo: String, mCallBack: ApiCallBack<Any>)

    fun confirmOrder(token: String, orderNo: String, mCallBack: ApiCallBack<Any>)
}