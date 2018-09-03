package com.gxtc.huchuan.ui.mine.circle.statistic

import com.gxtc.huchuan.bean.CircleBean
import com.gxtc.huchuan.data.CircleRepository
import com.gxtc.huchuan.data.CircleSource
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2018/3/17.
 */
class StatisticActiviPresenter(view:StatisticActiviContract.View):StatisticActiviContract.Presenter {
    var mView :StatisticActiviContract.View? = null
    var data : CircleSource? = null

    init {
        mView = view
        mView?.setPresenter(this)
        data = CircleRepository()
    }
    override fun getActiveDataList(start: Int, groupId: Int) {
        data?.getActiveDataList(start,groupId,object :ApiCallBack<MutableList<CircleBean>>(){
            override fun onSuccess(data: MutableList<CircleBean>?) {
                if(data == null || data.size == 0){
                    mView?.showEmpty()
                    return
                }
                mView?.showData(data)
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showError(message)
            }

        })
    }

    override fun start() {}

    override fun destroy() {
        data?.destroy()
    }
}