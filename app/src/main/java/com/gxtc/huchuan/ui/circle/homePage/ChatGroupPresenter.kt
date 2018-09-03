package com.gxtc.huchuan.ui.circle.homePage

import com.gxtc.commlibrary.utils.ErrorCodeUtil
import com.gxtc.huchuan.bean.CircleBean
import com.gxtc.huchuan.data.CircleRepository
import com.gxtc.huchuan.data.CircleSource
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.http.ApiCallBack

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/8/17.
 */
class ChatGroupPresenter(view: ChatGroupContract.View) : ChatGroupContract.Presenter {

    var mView: ChatGroupContract.View? = null
    var mData: CircleSource? = null
    var groupId: Int? = null;

    init {
        mView = view
        mView?.setPresenter(this)
        mData = CircleRepository()
    }

    override fun getData(groupId: Int) {
        mView?.showLoad()
        this.groupId = groupId
        val token = UserManager.getInstance().token

        mData?.getChatList(token,groupId,object : ApiCallBack<MutableList<CircleBean>>(){
            override fun onSuccess(data: MutableList<CircleBean>?) {
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

        } )

    }

    override fun refreshData() {
        val token = UserManager.getInstance().token

        mData?.getChatList(token,groupId!!,object : ApiCallBack<MutableList<CircleBean>>(){
            override fun onSuccess(data: MutableList<CircleBean>?) {
                mView?.showRefreshData(data)
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showRefreshData(null)
            }

        } )
    }

    override fun loadMoreData() {}


    override fun start() {
    }

    override fun destroy() {
        mData?.destroy()
    }
}