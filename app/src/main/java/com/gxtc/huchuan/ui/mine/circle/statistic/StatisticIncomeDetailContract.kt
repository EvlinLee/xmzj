package com.gxtc.huchuan.ui.mine.circle.statistic

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.StatisticDetailBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2018/3/17.
 */
class StatisticIncomeDetailContract {

    interface Presenter :BasePresenter{
        fun getStatisticIncomeDetailData(start: Int?,groupId:Int?,timeSection: String?)
    }

    interface View :BaseUiView<Presenter>{
        fun showData(data:MutableList<StatisticDetailBean>)
    }
}