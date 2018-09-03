package com.gxtc.huchuan.ui.mine.circle.circlesign

import com.gxtc.commlibrary.data.BaseSource
import com.gxtc.huchuan.bean.CircleSignBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2017/12/20.
 */
interface CircleSignSource:BaseSource {
    fun getData(groupId:String,start:String,joinType:String,userName:String,loadTime:Long,callBack: ApiCallBack<ArrayList<CircleSignBean>>)
}