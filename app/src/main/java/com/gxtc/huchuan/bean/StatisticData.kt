package com.gxtc.huchuan.bean

/**
Created by zzg on 2018/3/19.

Fee 总金额
saleFee 佣金
midFee 平台中间费
count 有效交易数
dateName 日期名称
realIncome 实付
timeSection 时间区间
*/
data class StatisticBean(var fee: Double?, var saleFee: Double?, var midFee:Double?,
                         var count:Int,var dateName:String,var realIncome:Double?,var timeSection:String?,var userCode:String?)

/**
 参数名称 说明
fee 总金额
saleFee 佣金
midFee 平台中间费
signuptime 日期名称
realIncome 实际收入
userCode 新媒号
name 用户名
paySource 来源0, app, 1微信, 5其他
headPic 用户头像
 */
data class StatisticDetailBean(var fee: Double?, var saleFee: Double?, var midFee:Double?,var name:String?,var headPic:String?,
                                var userCode:String?,var signuptime:String,var realIncome:Double?,var paySource:String?)