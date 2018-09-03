package com.gxtc.huchuan.ui.mine.circle.circlesign

import com.gxtc.huchuan.bean.CircleSignBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2017/12/20.
 */
class CirclePresenter(view :CircleSignContract.View):CircleSignContract.Presenter {
    var mView :CircleSignContract.View? = null
    var data:CircleResposetory? = null
    var loadTime:Long? = 0L
    init {
        mView = view
        mView?.setPresenter(this)
        data = CircleResposetory()
    }

    override fun start() {}

    override fun getData(groupId: String, start: String, joinType: String, userName: String) {
        if("0".equals(start)){
            loadTime = System.currentTimeMillis()
        }
        data?.getData(groupId,start,joinType,userName,loadTime!!,object :ApiCallBack<ArrayList<CircleSignBean>>(){
            override fun onSuccess(data: ArrayList<CircleSignBean>?) {
                if(data == null || data.size == 0){
                    mView?.showEmpty()
                    return
                }
                mView?.showData(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showError(message)
            }

        })
    }

    override fun destroy() {
        data?.destroy()
    }
}