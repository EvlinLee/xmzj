package com.gxtc.huchuan.ui.mine.circle.statistic.statisticvisitor

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.huchuan.bean.VisitorBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.ui.mine.circle.statistic.StatisticResposetory
import com.gxtc.huchuan.ui.mine.circle.statistic.StatisticSource

/**
 * Created by zzg on 2018/3/16.
 */
class StatisticVisitorPresenter(view :StatisticVisitorContract.View) :StatisticVisitorContract.Present {

    var datas : StatisticSource.Source? = null
    var mView:StatisticVisitorContract.View? = null

    init {
        mView = view
        mView?.setPresenter(this)
        datas = StatisticResposetory()
    }

    override fun getData(token:String?,groupId: Int, start: Int, dateType: Int, checkType: Int, isRefresh: Boolean) {

        datas?.getVisitorData(token,groupId,start,dateType,checkType,object :ApiCallBack<MutableList<VisitorBean>>(){
            override fun onSuccess(data: MutableList<VisitorBean>?) {
                if(data == null || data.size == 0){
                    mView?.showEmpty()
                    return
                }
                if (isRefresh) {
                    mView?.showRefreshFinish(data)
                } else {
                    mView?.showData(data)
                }
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showError(message)
            }

        })
    }

    override fun getDetailData(groupId: Int, start: Int, checkType: Int, timeSection: String?) {
        datas?.getVisitorDetailData(groupId,start,checkType,timeSection,object :ApiCallBack<MutableList<VisitorBean>>(){
            override fun onSuccess(data: MutableList<VisitorBean>?) {
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



    override fun loadMore() {}

    override fun start() {}

    override fun destroy() {
        datas?.destroy()
    }
}