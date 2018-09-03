package com.gxtc.huchuan.data

import com.gxtc.commlibrary.data.BaseSource
import com.gxtc.huchuan.bean.CategoryBean
import com.gxtc.huchuan.bean.MallBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2017/10/24.
 */
interface MallSeachSource : BaseSource{
    fun getSearchData(map:HashMap<String,String>,mCallBack: ApiCallBack<ArrayList<CategoryBean>>)
}