package com.gxtc.huchuan.ui.deal.guarantee

import com.gxtc.commlibrary.utils.ErrorCodeUtil
import com.gxtc.huchuan.bean.GoodsDetailedBean
import com.gxtc.huchuan.data.deal.DealRepository
import com.gxtc.huchuan.data.deal.DealSource
import com.gxtc.huchuan.http.ApiCallBack

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/3/22.
 */
class ApplyGuaranteePresenter(var mView: ApplyGuaranteeContract.View?): ApplyGuaranteeContract.Presenter {

    private var mData: DealSource ? = null

    init {
        mData = DealRepository()
        mView?.setPresenter(this)
    }

    override fun submint(map: Map<String, String>) {
        mView?.showLoad()

        mData?.issueGuaranteeInfo(map, object : ApiCallBack<GoodsDetailedBean>() {

            override fun onSuccess(bean: GoodsDetailedBean?) {
                mView?.showLoadFinish()
                if(bean != null){
                    mView?.showSubmitSuccess(bean.id.toString())
                }else{
                    mView?.showError("订单创建失败")
                }
            }

            override fun onError(errorCode: String?, message: String?) =
                ErrorCodeUtil.handleErr(mView, errorCode, message)
        })
    }

    override fun start() = Unit

    override fun destroy() {
        mView = null
        mData?.destroy()
    }
}