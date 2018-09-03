package com.gxtc.huchuan.data

import com.gxtc.commlibrary.data.BaseSource
import com.gxtc.huchuan.bean.CategoryBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by Administrator on 2017/10/24.
 */
interface MallTagSource : BaseSource{
    fun getTagDatas(map:HashMap<String,String>,mCallBack: ApiCallBack<ArrayList<CategoryBean>>)
}