package com.gxtc.huchuan.bean

import java.io.Serializable

/**
 * Created by zzg on 2017/12/12.
 */
 data class SeriseCountBean(var userCode:String = "",var name :String = "",
                              var headPic :String = "", var fee :String = "",
                              var payType:String,var signuptime:Long = 0L,
                              var isH5:String = "",var isRefund:String = "") : Serializable {

}