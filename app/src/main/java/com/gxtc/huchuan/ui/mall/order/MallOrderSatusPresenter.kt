package com.gxtc.huchuan.ui.mall.order

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.utils.ErrorCodeUtil
import com.gxtc.huchuan.bean.MallBean
import com.gxtc.huchuan.bean.OrderBean
import com.gxtc.huchuan.bean.pay.OrdersResultBean
import com.gxtc.huchuan.data.MallOrderStatusRepository
import com.gxtc.huchuan.data.pay.PayImp
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.utils.LoginErrorCodeUtil

/**
 * Created by zzg on 2017/10/24.
 */
class MallOrderSatusPresenter(view:MallOrderContract.View) :BasePresenter ,MallOrderContract.Presenter{
    var mView:MallOrderContract.View? = null
    var datas:MallOrderStatusRepository? = null
    var start:Int = 0

    init {
        mView = view
        mView?.setPresenter(this)
        datas = MallOrderStatusRepository()
    }

    override fun getOrderStatusList(isLoadMore: Boolean, map: HashMap<String, String>) {
         if(!isLoadMore){
             start = 0
         }else{
             start = start + 15
         }
        map.put("start",start.toString())
        datas?.getOrderList(map,object :ApiCallBack<ArrayList<OrderBean>>(){
            override fun onSuccess(data: ArrayList<OrderBean>?) {
                if( start == 0 && (data == null || data.size == 0)){
                    mView?.showEmpty()
                    return
                }
                mView?.showOrderStatusList(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
                ErrorCodeUtil.handleErr(mView, errorCode, message)
            }

        })
    }
    override fun confirmOrder(token: String, orderNo: String) {
        datas!!.confirmOrder(token,orderNo,object :ApiCallBack<Any>(){
            override fun onSuccess(data: Any?) {
                mView?.showfirmOrderResult(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
                ErrorCodeUtil.handleErr(mView, errorCode, message)
            }

        })
    }

    override fun deleteOrder(token: String, orderNo: String) {
        datas!!.deleteOrder(token,orderNo,object :ApiCallBack<Any>(){
            override fun onSuccess(data: Any?) {
                mView?.showDeleteOrderResult(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
                ErrorCodeUtil.handleErr(mView, errorCode, message)
            }

        })
    }

    override fun start() {}

    override fun destroy() {
        datas?.destroy()
    }
}