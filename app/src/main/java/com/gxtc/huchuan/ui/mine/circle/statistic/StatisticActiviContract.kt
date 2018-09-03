package com.gxtc.huchuan.ui.mine.circle.statistic

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.CircleBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2018/3/17.
 */
class StatisticActiviContract {

    interface View  :BaseUiView<Presenter>{
        fun showData(data:MutableList<CircleBean>?)
    }

    interface Presenter  :BasePresenter{
         fun getActiveDataList(start: Int, groupId: Int)
    }
}