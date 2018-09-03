package com.gxtc.huchuan.ui.mine.circle.statistic

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.StatisticBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2018/3/16.
 */
class StatisticContract() {

    interface View :BaseUiView<Presenter>{
        fun showData(data:MutableList<StatisticBean>?);
    }

    interface Presenter :BasePresenter{
        fun getData(token: String, start: Int,groupId:Int,dateType:Int,type:Int)
    }

}