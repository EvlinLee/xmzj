package com.gxtc.huchuan.ui.mine.circle.statistic

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.CircleBean
import com.gxtc.huchuan.bean.StatisticBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2018/3/17.
 */
class StatisticPentActiviContract {

    interface View  :BaseUiView<Presenter>{
        fun showData(data:MutableList<StatisticBean>?)
    }

    interface Presenter  :BasePresenter{
         fun getPentDataList(token: String, start: Int,groupId:Int,dateType:Int,type:Int)
    }
}