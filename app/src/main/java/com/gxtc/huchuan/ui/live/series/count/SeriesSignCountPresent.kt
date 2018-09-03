package com.gxtc.huchuan.ui.live.series.count

import android.text.TextUtils
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.bean.ChatJoinBean
import com.gxtc.huchuan.bean.SeriseCountBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.http.ApiCallBack
import java.util.ArrayList

/**
 * Created by zzg on 2017/12/12.
 */
class SeriesSignCountPresent(view :SeriseSignCountContract.View):SeriseSignCountContract.Present {

    var mView :SeriseSignCountContract.View ? =null
    var data :SeriessignCountRespose ? =null
    var start = 0

    init {
        mView = view
        mView!!.setPresenter(this)
        data = SeriessignCountRespose()
    }

    override fun getSeriseSignCount(token: String, id: String, type: String, searchKey: String, isloadMore: Boolean) {
//        if(!isloadMore){
//            start = 0
//        }else{
//            start = start + 15
//        }
//        data?.getSeriseSignCount(token,id,type,start,searchKey,object :ApiCallBack<ArrayList<SeriseCountBean>>(){
//            override fun onSuccess(data: ArrayList<SeriseCountBean>?) {
//                if(mView == null) return
//                if(start == 0 && (data == null || data.size == 0)){
//                    mView?.showEmpty()
//                    return
//                }
//                mView?.showSeriseSignCount(data!!)
//            }
//
//            override fun onError(errorCode: String?, message: String?) {
//                mView?.showError(message)
//            }
//
//        })
    }

   var loadTime : Long = 0
    override fun getlistJoinMember(token: String, userType: String,  type : String, chatId: String, searchKey: String, isloadMore: Boolean) {
        if(!isloadMore){
            this.start = 0
            loadTime = System.currentTimeMillis()
        }else{
            start = start + 15
        }
        var map = HashMap<String, String>()
        map["token"] = UserManager.getInstance().token
        map["start"] = start.toString()
        map["userType"] = userType
        map["type"] = type
        map["chatId"] = chatId
        if(!searchKey.equals(""))
           map["searchKey"] = searchKey
        else
            map["loadTime"] = loadTime.toString()

        data?.getlistJoinMember(map,object : ApiCallBack<ArrayList<ChatJoinBean.MemberBean>>(){
            override fun onSuccess(data: ArrayList<ChatJoinBean.MemberBean>) {
                 if(data?.size > 0) {
                     mView?.showSeriseSignCount(data!!)
                 }else{
                     mView?.showLoadFinish()
                 }
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showError(message)
            }

        });
    }

    override fun start() {}

    override fun destroy() {
        data?.destroy()
    }
}