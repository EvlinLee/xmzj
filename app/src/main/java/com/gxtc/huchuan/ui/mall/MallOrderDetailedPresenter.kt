package com.gxtc.huchuan.ui.mall

import com.gxtc.commlibrary.utils.ErrorCodeUtil
import com.gxtc.huchuan.bean.AddressBean
import com.gxtc.huchuan.data.MallRepository
import com.gxtc.huchuan.data.MallSource
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.data.deal.DealRepository
import com.gxtc.huchuan.data.deal.DealSource
import com.gxtc.huchuan.http.ApiCallBack

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/10/23.
 */
class MallOrderDetailedPresenter(view: MallOrderDetailedContract.View): MallOrderDetailedContract.Presenter{

    private var mView: MallOrderDetailedContract.View ? = null
    private var mData: MallSource ? = null
    private var mDealData: DealSource ? = null

    init {
        mView = view
        mView?.setPresenter(this)
        mData = MallRepository()
        mDealData = DealRepository()
    }

    override fun getAddressList() {
        val token = UserManager.getInstance().token
        mView?.showLoad()
        mDealData?.getAddress(token, object : ApiCallBack<List<AddressBean>>() {
            override fun onSuccess(data: List<AddressBean>?) {
                mView?.showLoadFinish()
                if (data != null && data.isNotEmpty()) {
                    mView?.showAddress(data)
                } else {
                    mView?.showEmpty()
                }
            }

            override fun onError(errorCode: String, message: String) {
                ErrorCodeUtil.handleErr(mView, errorCode, message)
            }
        })
    }

    override fun start() {

    }

    override fun destroy() {
        mData?.destroy()
        mDealData?.destroy()
        mView = null
    }

}