package com.gxtc.huchuan.ui.mine.circle.statistic

import com.gxtc.commlibrary.data.BaseSource
import com.gxtc.huchuan.bean.StatisticBean
import com.gxtc.huchuan.bean.StatisticDetailBean
import com.gxtc.huchuan.bean.VisitorBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2018/3/16.
 */
interface StatisticSource {
    interface Source : BaseSource {
       fun getData(token: String, start: Int,groupId:Int,dateType:Int,type:Int, callBack: ApiCallBack<MutableList<StatisticBean>>)
       fun getVisitorData(token:String?,groupId: Int, start: Int,dateType: Int,checkType: Int, callBack: ApiCallBack<MutableList<VisitorBean>>)
       fun getVisitorDetailData(groupId: Int, start: Int,checkType: Int,timeSection: String?,callBack: ApiCallBack<MutableList<VisitorBean>>)
       fun getPentData(token: String, start: Int,groupId:Int,dateType:Int,type:Int, callBack: ApiCallBack<MutableList<StatisticBean>>)
       fun getStatisticIncomeDetailData(start: Int?,groupId:Int?,timeSection: String?, callBack: ApiCallBack<MutableList<StatisticDetailBean>>)
    }
}