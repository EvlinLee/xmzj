package com.gxtc.huchuan.ui.mine.account

import com.gxtc.commlibrary.utils.ErrorCodeUtil
import com.gxtc.huchuan.bean.WithdrawRecordBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.data.WithdrawRecordRepository
import com.gxtc.huchuan.http.ApiCallBack

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/8/31.
 */
class WithdrawRecordPresenter: WithdrawRecordContract.Presenter{

    var mView: WithdrawRecordContract.View ? = null
    var mData: WithdrawRecordContract.Source ? = null

    var start = 0

    constructor(view: WithdrawRecordContract.View) {
        mView = view
        mView?.setPresenter(this)
        mData = WithdrawRecordRepository()
    }

    override fun refreshData() {
        start = 0
        val token = UserManager.getInstance().token
        mData?.getDataList(token,start,object : ApiCallBack<MutableList<WithdrawRecordBean>>(){

            override fun onSuccess(data: MutableList<WithdrawRecordBean>?) {
                if(data == null || data.size == 0){
                    mView?.showRefreshData(null)
                    return
                }

                mView?.showRefreshData(data)
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showError(message)
            }

        })
    }

    override fun loadMoreData() {
        start += 15
        val token = UserManager.getInstance().token
        mData?.getDataList(token,start,object : ApiCallBack<MutableList<WithdrawRecordBean>>(){

            override fun onSuccess(data: MutableList<WithdrawRecordBean>?) {
                if(data == null || data.size == 0){
                    mView?.showNoLoadMore()
                    return
                }

                mView?.showLoadMoreData(data)
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showError(message)
            }

        })

    }

    override fun getDataList() {
        mView?.showLoad()
        val token = UserManager.getInstance().token
        start = 0

        mData?.getDataList(token,start,object : ApiCallBack<MutableList<WithdrawRecordBean>>(){

            override fun onSuccess(data: MutableList<WithdrawRecordBean>?) {
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

    override fun start() {

    }

    override fun destroy() {
        mData?.destroy()
        mView = null
    }
}