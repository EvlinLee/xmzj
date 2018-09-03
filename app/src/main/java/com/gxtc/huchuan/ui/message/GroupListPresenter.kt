package com.gxtc.huchuan.ui.message

import com.gxtc.commlibrary.utils.ErrorCodeUtil
import com.gxtc.huchuan.bean.MessageBean
import com.gxtc.huchuan.data.MessageRepository
import com.gxtc.huchuan.data.MessageSource
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.http.ApiCallBack

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/11.
 */
class GroupListPresenter(view : GroupListContract.View) : GroupListContract.Presenter {

    private var mView : GroupListContract.View? = null
    private var mData : MessageSource? = null

    private var start = 0

    init {
        mView = view
        mView?.setPresenter(this)
        mData = MessageRepository()
    }

    override fun getGroups() {
        start = 0
        mView?.showLoad()

        val map = HashMap<String,String>()
        map.put("token",UserManager.getInstance().token)
        map.put("start",start.toString())

        mData?.getGroups(map, object : ApiCallBack<MutableList<MessageBean>>(){

            override fun onSuccess(data: MutableList<MessageBean>?) {
                mView?.showLoadFinish()
                if(data == null || data.size == 0){
                    mView?.showEmpty()
                    return
                }
                mView?.showGroups(data)
            }

            override fun onError(errorCode: String?, message: String?) {
                ErrorCodeUtil.handleErr(mView, errorCode, message)
            }
        })
    }

    override fun refreshData() {
        start = 0

        val map = HashMap<String,String>()
        map.put("token",UserManager.getInstance().token)
        map.put("start",start.toString())

        mData?.getGroups(map, object : ApiCallBack<MutableList<MessageBean>>(){

            override fun onSuccess(data: MutableList<MessageBean>?) {
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

        val map = HashMap<String,String>()
        map.put("token",UserManager.getInstance().token)
        map.put("start",start.toString())

        mData?.getGroups(map, object : ApiCallBack<MutableList<MessageBean>>(){

            override fun onSuccess(data: MutableList<MessageBean>?) {
                if(data == null || data.size == 0){
                    mView?.showNoLoadMore()
                    return
                }
                mView?.showLoadMoreData(data)
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showError(message)
                start -= 15
            }
        })
    }

    override fun queryGroup(key: String) {
        val map = HashMap<String,String>()
        map.put("token",UserManager.getInstance().token)
        map.put("start","0")
        map.put("searchKey",key)
        map.put("pageSize","1000")

        mData?.searchGroup(map,object : ApiCallBack<MutableList<MessageBean>>(){
            override fun onSuccess(data: MutableList<MessageBean>?) {
                if(data == null || data.size == 0){
                    mView?.showLoadFinish()
                    mView?.showError("暂无搜索结果")
                    return
                }
                mView?.showQueryResult(data)
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showLoadFinish()
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