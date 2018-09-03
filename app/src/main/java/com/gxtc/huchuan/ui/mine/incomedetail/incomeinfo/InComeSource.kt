package com.gxtc.huchuan.ui.mine.incomedetail.incomeinfo

import com.gxtc.huchuan.bean.NewDistributeBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2017/12/22.
 */
interface InComeSource {
    fun getData(map :HashMap<String,String>,callBack: ApiCallBack<ArrayList<NewDistributeBean>>)
    fun getProfitDetailData(map :HashMap<String,String>,callBack: ApiCallBack<ArrayList<NewDistributeBean>>)
}