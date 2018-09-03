package com.gxtc.huchuan.ui.message

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.utils.ErrorCodeUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.data.MessageRepository
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2017/12/8.
 */
class MessageFragmentPresent(view:MessageFragmntContract.View) :BasePresenter,MessageFragmntContract.Present {
    var mView:MessageFragmntContract.View? = null
    var data: MessageRepository? = null

    init {
        mView = view
        mView?.setPresenter(this)
        data = MessageRepository()
    }

    override fun getNewFriendsCount(token: String) {
        if(mView == null) return
        data?.getNewFriendsCount(token,object :ApiCallBack<Any>(){
            override fun onSuccess(data: Any?) {
                mView?.showNewFriendsCount(data)
            }

            override fun onError(errorCode: String?, message: String?) {
                ErrorCodeUtil.handleErr(mView,errorCode,message)
            }

        })
    }

    override fun start() {}

    override fun destroy() {
        data?.destroy()
    }
}