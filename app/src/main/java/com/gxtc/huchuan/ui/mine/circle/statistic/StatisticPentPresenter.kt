package com.gxtc.huchuan.ui.mine.circle.statistic

import com.gxtc.huchuan.bean.StatisticBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2018/3/16.
 */
class StatisticPentPresenter(view: StatisticPentActiviContract.View) : StatisticPentActiviContract.Presenter {

    var  mView : StatisticPentActiviContract.View? = null
    var  source: StatisticSource.Source? = null

    init {
        mView = view
        source = StatisticResposetory()
        mView?.setPresenter(this)
    }


    override fun getPentDataList(token: String, start: Int, groupId: Int, dateType: Int, type: Int) {
        source?.getPentData(token, start, groupId, dateType, type, object : ApiCallBack<MutableList<StatisticBean>>() {
            override fun onSuccess(data: MutableList<StatisticBean>?) {
                if (data == null || data.size == 0) {
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
        source?.destroy()
    }
}