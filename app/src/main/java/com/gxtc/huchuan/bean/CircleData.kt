package com.gxtc.huchuan.bean

import java.io.Serializable

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/11/3.
 * 圈子有关的data对象
 */

data class SearchChatBean(var type: Int = 0,
                          var code: String = "",
                          var name: String = "",
                          var remarkName: String = "",
                          var pic: String = "") {

    var isSelect: Boolean? = null

    override fun equals(other: Any?): Boolean = code == (other as? SearchChatBean)?.code

}


data class RecentBean(var id: String) {

    var dateName: String  ?= null
    var count: String  ?= null

}