package com.gxtc.huchuan.ui.message

import com.gxtc.commlibrary.utils.ErrorCodeUtil
import com.gxtc.huchuan.bean.FocusBean
import com.gxtc.huchuan.data.MessageRepository
import com.gxtc.huchuan.data.MessageSource
import com.gxtc.huchuan.data.PersonalInfoRepository
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoContract

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/11.
 */
class NewFriendsPresenter(view : NewFriendsContract.View) : NewFriendsContract.Presenter{

    private var mView : NewFriendsContract.View ? = null
    private var mData : MessageSource ? = null
    private var mFollowData : PersonalInfoContract.Source ? = null
    private var mOverlookData : OverlookSource ? = null

    private var start = 0
    private var groupChatId : String = ""

    init {
        mView = view
        mView?.setPresenter(this)
        mData = MessageRepository()
        mFollowData = PersonalInfoRepository()
        mOverlookData = OverlookRepository()
    }

    override fun getNewFriends(groupChatId: String) {
        start = 0
        this.groupChatId = groupChatId
        mView?.showLoad()

        val code = UserManager.getInstance().userCode
        mData?.getNewFriends(code, "7", start,groupChatId, object : ApiCallBack<MutableList<FocusBean>>(){

            override fun onSuccess(data: MutableList<FocusBean>) {
                mView?.showLoadFinish()
                mView?.showNewFriends(data)
            }

            override fun onError(errorCode: String?, message: String?) {
                ErrorCodeUtil.handleErr(mView,errorCode,message)
            }
        })
    }

    override fun refreshData() {
        start = 0

        val code = UserManager.getInstance().userCode
        mData?.getNewFriends(code, "7", start,groupChatId, object : ApiCallBack<MutableList<FocusBean>>(){

            override fun onSuccess(data: MutableList<FocusBean>?) {
                if(data == null || data.size == 0){
                    mView?.showRefreshData(data)
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

        val code = UserManager.getInstance().userCode
        mData?.getNewFriends(code, "7", start, groupChatId,object : ApiCallBack<MutableList<FocusBean>>(){

            override fun onSuccess(data: MutableList<FocusBean>?) {
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

    override fun followUser(userCode: String) {
        val token = UserManager.getInstance().token
        mFollowData?.setUserFocus(object : ApiCallBack<Any>(){

            override fun onSuccess(data: Any?) {
                mView?.showFollowUser(userCode,true,"")
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showFollowUser(userCode,false,message!!)
            }
        },token,"3",userCode)
    }

    override fun queryUser(key: String) {
        val map = HashMap<String,String>()
        map.put("userCode",UserManager.getInstance().userCode)
        map.put("start","0")
        map.put("searchKey",key)
        map.put("pageSize","1000")

        mData?.searchNewFriends(map,object : ApiCallBack<MutableList<FocusBean>>(){
            override fun onSuccess(data: MutableList<FocusBean>?) {
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
    override fun overlook(userCode: String) {
        mOverlookData!!.getOverlook(UserManager.getInstance().token,userCode,object : ApiCallBack<Any>(){
            override fun onSuccess(data: Any?) {
                if(mView == null) return
                mView?.showOverlookResult(true,"",userCode)
            }
            override fun onError(errorCode: String?, message: String?) {
                if(mView == null) return
                mView?.showOverlookResult(false,message!!,userCode)
            }
        })
    }
    override fun start() {

    }

    override fun destroy() {
        mData?.destroy()
        mFollowData?.destroy()
        mOverlookData?.destroy()
        mView = null
    }
}