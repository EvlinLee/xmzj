package com.gxtc.huchuan.bean

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/5/29.
 */
data class MergeMessageBean(var id: String?){

    var objectName: String ? = null
    var createTime: String ? = null
    var headPic: String ? = null
    var content: String ? = null            //content 为一个json字符串 ，每种消息的json格式都不一样
    var userName: String ? = null
    var userCode: String ? = null

}