package com.gxtc.huchuan.data

import com.gxtc.commlibrary.data.BaseSource
import com.gxtc.huchuan.bean.MallBean
import com.gxtc.huchuan.bean.OrderBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2017/10/24.
 */
interface MallOrderSource :BaseSource {

    fun getOrderList(map: HashMap<String,String>,mCallBack:ApiCallBack<ArrayList<OrderBean>>)
    fun  confirmOrder(token:String,orderNo:String,mCallBack:ApiCallBack<Any>)
    fun  deleteOrder(token:String,orderNo:String,mCallBack:ApiCallBack<Any>)
}