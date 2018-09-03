package com.gxtc.huchuan.ui.message

import com.gxtc.commlibrary.data.BaseSource
import com.gxtc.huchuan.bean.MessageBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2017/9/23.
 */
interface OverlookSource : BaseSource {
    fun getOverlook(token: String, userCode: String, callBack: ApiCallBack<Any>)
}