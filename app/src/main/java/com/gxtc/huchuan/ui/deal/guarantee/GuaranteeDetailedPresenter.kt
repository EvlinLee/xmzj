package com.gxtc.huchuan.ui.deal.guarantee

import com.gxtc.commlibrary.utils.ErrorCodeUtil
import com.gxtc.huchuan.bean.GoodsDetailedBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.data.deal.DealRepository
import com.gxtc.huchuan.data.deal.DealSource
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/3/24.
 */
class GuaranteeDetailedPresenter(var mView: GuaranteeDetailedContract.View?): GuaranteeDetailedContract.Presenter {

    private var mData: DealSource ? = null

    init {
        mData = DealRepository()
        mView?.setPresenter(this)
    }

    override fun getData(id: String) {
        mView?.showLoad()

        val token = UserManager.getInstance().token
        mData?.getGoodsDetailed(token, id, object : ApiCallBack<GoodsDetailedBean>(){

            override fun onSuccess(data: GoodsDetailedBean?) {
                mView?.showLoadFinish()
                if(data == null){
                    mView?.showEmpty()
                    return
                }
                mView?.showData(data)
            }

            override fun onError(errorCode: String?, message: String?) =
                    ErrorCodeUtil.handleErr(mView, errorCode,  message)

        })
    }


    override fun agreeDeal(map: HashMap<String, String>) {
        mView?.showLoad()
        mData?.CreateGuaranteeTrade(map, object : ApiCallBack<GoodsDetailedBean>(){
            override fun onSuccess(data: GoodsDetailedBean?) {
                mView?.showLoadFinish()
                data?.let {
                    mView?.showAgreen(data)
                }
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showLoadFinish()
                mView?.showError(message)
            }
        })

    }

    override fun disagreenDeal(id: String) {
        mView?.showLoad()
        mData?.disagreeFastTrade(UserManager.getInstance().token, id, object : ApiCallBack<Any>(){
            override fun onSuccess(data: Any?) {
                mView?.showLoadFinish()
                mView?.showDisargreen()
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showLoadFinish()
                mView?.showError(message)
            }
        })
    }



    override fun destroy() {
        mView = null
        mData?.destroy()
        RxTaskHelper.getInstance().cancelTask(this)
    }


    override fun start() = Unit
}