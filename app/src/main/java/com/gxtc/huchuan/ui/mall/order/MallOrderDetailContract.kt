package com.gxtc.huchuan.ui.mall.order

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.CoustomMerBean
import com.gxtc.huchuan.bean.OrderDetailBean
import java.util.ArrayList

/**
 * Created by zzg on 2017/11/4.
 */
class MallOrderDetailContract {
    interface Presenter : BasePresenter{
        fun getOrderDetail(token:String,orderNo:String)
        fun getComstumers(type:String,rand:String)
        fun deleteOrder(token: String, orderNo: String)
        fun confirmOrder(token: String, orderNo: String)
    }
    interface View : BaseUiView<Presenter>{
        fun showOrderDetail(data: OrderDetailBean)
        fun showComstumers(data: ArrayList<CoustomMerBean>)
        fun showfirmOrderResult(result: Any)
        fun showDeleteOrderResult(result: Any)
    }
}