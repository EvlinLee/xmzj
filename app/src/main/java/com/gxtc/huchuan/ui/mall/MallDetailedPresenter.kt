package com.gxtc.huchuan.ui.mall

import com.gxtc.commlibrary.utils.ErrorCodeUtil
import com.gxtc.huchuan.bean.MallDetailBean
import com.gxtc.huchuan.data.MallRepository
import com.gxtc.huchuan.data.MallSource
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.http.ApiCallBack

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/10/31.
 */
class MallDetailedPresenter(view: MallDetailedContract.View): MallDetailedContract.Presenter {

    private var mView: MallDetailedContract.View ?= null
    private var mData: MallSource ?= null

    init {
        mView = view
        mView?.setPresenter(this)
        mData = MallRepository()
    }

    override fun getGoodsDetailed(id: String) {
        val token = UserManager.getInstance().token

        mView?.showLoad()
        mData?.getGoodsDetailed(id, token, object :ApiCallBack<MallDetailBean>(){

            override fun onSuccess(data: MallDetailBean?) {
                mView?.showLoadFinish()
                if(data == null){
                    mView?.showEmpty()
                    return
                }
                mView?.showGoodsDetailed(data)
            }

            override fun onError(errorCode: String?, message: String?) {
                ErrorCodeUtil.handleErr(mView, errorCode, message)
            }
        })
    }

    override fun addShopCar(map: HashMap<String, String>) {
        mData?.addShopCart(map, object : ApiCallBack<Any>() {

            override fun onSuccess(data: Any) {
                mView?.showLoadFinish()
                mView?.showAddShopCartResult(data)
            }

            override fun onError(errorCode: String, message: String) {
                mView?.showLoadFinish()
                mView?.showError(message)
            }
        })
    }

    override fun collectMall(id: String) {
        val token = UserManager.getInstance().token
        val userName = UserManager.getInstance().userName
        val userPic = UserManager.getInstance().headPic

        val map = HashMap<String, String>()
        map.put("token", token)
        map.put("userName", userName)
        map.put("userPic", userPic)
        map.put("bizType", 10.toString())
        map.put("bizId", id)

        mData?.collectMall(map, object: ApiCallBack<Any>() {
            override fun onSuccess(data: Any?) {
                mView?.showCollectResult()
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showError(message)
            }
        })
    }

    override fun start() {

    }

    override fun destroy() {
        mData?.destroy()
        mView = null
    }
}