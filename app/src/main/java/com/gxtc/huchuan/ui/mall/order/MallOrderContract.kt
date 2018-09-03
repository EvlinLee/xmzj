package com.gxtc.huchuan.ui.mall.order

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUserView
import com.gxtc.huchuan.bean.MallBean
import com.gxtc.huchuan.bean.OrderBean

/**
 * Created by zzg on 2017/10/24.
 */
interface MallOrderContract {

    interface Presenter : BasePresenter{
         fun getOrderStatusList(boolean: Boolean,map:HashMap<String,String>)
         fun  confirmOrder(token:String,orderNo:String)
         fun  deleteOrder(token:String,orderNo:String)
    }

    interface View : BaseUserView<Presenter>{
        fun showOrderStatusList(datas:ArrayList<OrderBean>)
        fun showfirmOrderResult(result: Any)
        fun showDeleteOrderResult(result: Any)
    }
}