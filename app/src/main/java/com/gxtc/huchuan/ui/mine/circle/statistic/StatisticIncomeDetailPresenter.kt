package com.gxtc.huchuan.ui.mine.circle.statistic

import com.gxtc.huchuan.bean.StatisticDetailBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2018/3/17.
 */
class StatisticIncomeDetailPresenter(view:StatisticIncomeDetailContract.View):StatisticIncomeDetailContract.Presenter {

    var mView:StatisticIncomeDetailContract.View? = null
    var datas : StatisticSource.Source? = null

    init {
        mView = view
        mView?.setPresenter(this)
        datas = StatisticResposetory()
    }

    override fun getStatisticIncomeDetailData(start: Int?, groupId: Int?, timeSection: String?) {
        datas?.getStatisticIncomeDetailData(start,groupId,timeSection,object :ApiCallBack<MutableList<StatisticDetailBean>>(){
            override fun onSuccess(data: MutableList<StatisticDetailBean>?) {
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
        datas?.destroy()
    }
}