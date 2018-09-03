package com.gxtc.huchuan.ui.mine.circle.statistic

import com.gxtc.commlibrary.data.BaseRepository
import com.gxtc.huchuan.bean.StatisticBean
import com.gxtc.huchuan.bean.StatisticDetailBean
import com.gxtc.huchuan.bean.VisitorBean
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.CircleApi
import com.gxtc.huchuan.http.service.MineApi
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by zzg on 2018/3/16.
 */
class StatisticResposetory:BaseRepository(), StatisticSource.Source {

    override fun getVisitorDetailData(groupId: Int, start: Int, checkType: Int, timeSection: String?, callBack: ApiCallBack<MutableList<VisitorBean>>) {
        addSub(CircleApi.getInstance().listGroupVisitDataByDateDetail(groupId,start,checkType.toString(),timeSection)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<MutableList<VisitorBean>>>(callBack)))
    }

    override fun getPentData(token: String, start: Int, groupId: Int, dateType: Int, type: Int, callBack: ApiCallBack<MutableList<StatisticBean>>) {
        addSub(CircleApi.getInstance().listGroupStatisticsDataByDate(groupId,start,type,token,dateType)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<MutableList<StatisticBean>>>(callBack)))
    }


    override fun getData(token: String, start: Int,groupId:Int,dateType:Int,type:Int, callBack: ApiCallBack<MutableList<StatisticBean>>) {
                addSub(CircleApi.getInstance().listGroupStatisticsDataByDate(groupId,start,type,token,dateType)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(ApiObserver<ApiResponseBean<MutableList<StatisticBean>>>(callBack)))
    }


    override fun getVisitorData(token:String?,groupId: Int, start: Int, dateType: Int, checkType: Int, callBack: ApiCallBack<MutableList<VisitorBean>>) {
        addSub(CircleApi.getInstance().listGroupVisitDataByDate(token,groupId,start,checkType.toString(),dateType.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<MutableList<VisitorBean>>>(callBack)))
    }


    override fun getStatisticIncomeDetailData(start: Int?,groupId:Int?,timeSection: String?, callBack: ApiCallBack<MutableList<StatisticDetailBean>>) {
        addSub(CircleApi.getInstance().listGroupStatisticsDataByDateDetail(groupId!!,start!!,timeSection)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<MutableList<StatisticDetailBean>>>(callBack)))
    }

}