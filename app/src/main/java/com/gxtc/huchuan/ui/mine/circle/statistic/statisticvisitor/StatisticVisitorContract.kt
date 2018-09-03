package com.gxtc.huchuan.ui.mine.circle.statistic.statisticvisitor

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.VisitorBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2018/3/16.
 */
class StatisticVisitorContract {

    interface View:BaseUiView<Present>{
         fun showData(datas: MutableList<VisitorBean>)
         fun showRefreshFinish(datas: MutableList<VisitorBean>)
         fun showLoadMore(datas: MutableList<VisitorBean>)
         fun showNoMore()
    }

    interface Present :BasePresenter{
         fun getData(token:String?,groupId: Int, start: Int,dateType: Int,checkType: Int,isRefresh: Boolean)
         fun getDetailData(groupId: Int, start: Int,checkType: Int,timeSection: String?)
         fun loadMore()
    }
}