package com.gxtc.huchuan.ui.mall

import com.gxtc.commlibrary.utils.ErrorCodeUtil
import com.gxtc.huchuan.data.MallRepository
import com.gxtc.huchuan.bean.MallShopCartBean
import com.gxtc.huchuan.data.MallSource
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.http.ApiCallBack

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/10/23.
 */
class MallShopCartPresenter(view: MallShopCartContract.View): MallShopCartContract.Presenter {

    private var mData: MallSource ? = null
    private var mView: MallShopCartContract.View ? = null

    init {
        mView = view
        mView?.setPresenter(this)
        mData = MallRepository()
    }

    override fun getData() {
        val token = UserManager.getInstance().token
        mView?.showLoad()
        mData?.getShopCartList(token,object : ApiCallBack<MutableList<MallShopCartBean>>(){

            override fun onSuccess(data: MutableList<MallShopCartBean>?) {
                mView?.showLoadFinish()
                if(data == null || data.size == 0){
                    mView?.showEmpty()
                    return
                }
                mView?.showData(data)
            }

            override fun onError(errorCode: String?, message: String?) {
                ErrorCodeUtil.handleErr(mView,errorCode,message)
            }
        })
    }

    override fun refreshData() {
        val token = UserManager.getInstance().token
        mData?.getShopCartList(token,object : ApiCallBack<MutableList<MallShopCartBean>>(){

            override fun onSuccess(data: MutableList<MallShopCartBean>?) {
                mView?.showLoadFinish()
                if(data == null || data.size == 0){
                    return
                }
                mView?.showRefreshData(data)
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showLoadFinish()
                mView?.showError(message)
            }
        })
    }

    override fun removeGoods(id: String) {
        val token = UserManager.getInstance().token

        mData?.removeGoods(token, id, object: ApiCallBack<Any>(){
            override fun onSuccess(data: Any?) {
                mView?.showLoadFinish()
                mView?.showRemoveSuccess(id)
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showLoadFinish()
                mView?.showError(message)
            }
        })
    }

    override fun loadMoreData() {}

    override fun start() {}

    override fun destroy() {
        mData?.destroy()
        mView = null
    }

}