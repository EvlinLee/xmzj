package com.gxtc.huchuan.data

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/5/12.
 */
data class SpecialBean(var id: Int?) {
    /**
     * isFee : 0
     * isPay : 1
     * abstracts : 测试专题2
     * author : {"name":"Trevet_","pic":"https://xmzjvip.b0.upaiyun.com/xmzj/24631497857771857.gif","userCode":"Trevet"}
     * introduce : 专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍专题介绍
     * subscribeSum :
     * recommend : 0
     * updateTime : 1527064775000
     * label : [{"name":"标签1","id":1},{"name":"标签3","id":3},{"name":"标签2","id":2}]
     * pic : http://xmzjvip.b0.upaiyun.com/admin/newsSpecial/2018/05/23/com.xinmei6.app_163852.jpg
     * top : 1
     * typeByListCss : 0
     * price : 11
     * name : 测试专题1_免费
     * id : 1
     */
    var name: String? = null
    var pic: String? = null
    var abstracts: String? = null
    var introduce: String? = null
    var author: Author? = null
    var isSubscribe: String? = null
    var subscribeSum: String? = null
    var updateTime: Long? = null
    var label: List<Label>? = null
    var isFee: Int? = 0
    var price: Double? = 0.0
    var isPay: String? = null
    var recommend: Int? = 0
    var top: Int? = 0
    var typeByListCss: Int? = 0
    var isCollect: String? = null//是否收藏

    data class Author(var name: String?, var pic: String?, var userCode: String?) {}

    data class Label(var name: String?, var id: Int?) {

    }

}

data class ArticleSpecialBean(var id: String?, var title: String?, var cover: String?, var isFreeSee: Int?) {}