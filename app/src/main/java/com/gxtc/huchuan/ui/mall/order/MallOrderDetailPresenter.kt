package com.gxtc.huchuan.ui.mall.order

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.utils.ErrorCodeUtil
import com.gxtc.huchuan.bean.CoustomMerBean
import com.gxtc.huchuan.bean.MallDetailBean
import com.gxtc.huchuan.bean.OrderDetailBean
import com.gxtc.huchuan.data.MallCustomersRepository
import com.gxtc.huchuan.data.MallOrderDetailRepository
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2017/11/4.
 */
class MallOrderDetailPresenter(view:MallOrderDetailContract.View):BasePresenter,MallOrderDetailContract.Presenter {

    var mView:MallOrderDetailContract.View? = null
    var data: MallOrderDetailRepository? = null
    var datas: MallCustomersRepository? = null

    init {
        mView = view
        mView?.setPresenter(this)
        data = MallOrderDetailRepository()
        datas = MallCustomersRepository()
    }

    override fun getOrderDetail(token: String, orderNo: String) {
        mView?.showLoad()
        data!!.getOrderDetail(token,orderNo,object :ApiCallBack<OrderDetailBean>(){
            override fun onSuccess(data: OrderDetailBean?) {
                mView?.showLoadFinish()
                mView?.showOrderDetail(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
                ErrorCodeUtil.handleErr(mView, errorCode, message)
            }

        })
    }

    override fun getComstumers(type: String, rand: String) {
        datas!!.getCustermersList(type,rand,object :ApiCallBack<ArrayList<CoustomMerBean>>(){
            override fun onSuccess(data: ArrayList<CoustomMerBean>?) {
                mView?.showComstumers(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showError(message)
            }

        })
    }

    override fun confirmOrder(token: String, orderNo: String) {
        data!!.confirmOrder(token,orderNo,object :ApiCallBack<Any>(){
            override fun onSuccess(data: Any?) {
                mView?.showfirmOrderResult(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
                ErrorCodeUtil.handleErr(mView, errorCode, message)
            }

        })
    }

    override fun deleteOrder(token: String, orderNo: String) {
        data!!.deleteOrder(token,orderNo,object :ApiCallBack<Any>(){
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
        data?.destroy()
        datas?.destroy()
    }
}