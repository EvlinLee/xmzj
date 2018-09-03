package com.gxtc.huchuan.data

import com.gxtc.commlibrary.data.BaseSource
import com.gxtc.huchuan.bean.DealData
import com.gxtc.huchuan.bean.DealListBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2017/10/28.
 */
interface DealRecomemndSource : BaseSource {
    fun getDealRecomemndData(map:HashMap<String,String>,mCallBack: ApiCallBack<DealData>)
}