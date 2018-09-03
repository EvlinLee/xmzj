package com.gxtc.huchuan.data

import com.gxtc.commlibrary.data.BaseSource
import com.gxtc.huchuan.bean.AllStatusSumsBean
import com.gxtc.huchuan.bean.CoustomMerBean
import com.gxtc.huchuan.bean.MallBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by Administrator on 2017/10/24.
 */
interface MallCustomerdSource : BaseSource{
    fun getCustermersList(type:String,rand: String,mCallback:ApiCallBack<ArrayList<CoustomMerBean>>)
    fun getAllStatusSums(token:String,mCallback:ApiCallBack<ArrayList<AllStatusSumsBean>>)
}