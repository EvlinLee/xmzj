package com.gxtc.huchuan.bean

import com.gxtc.commlibrary.utils.GsonUtil
import java.io.Serializable

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/11/30.
 */

//统一的课堂列表对象    1=话题，2=系列课
data class UnifyClassBean(var type: Int = 1, var data: SubData? = null): Serializable{

    data class SubData(var id: Int = 0): Serializable{    //type对应id
        var facePic = ""                    //封面
        var fee = 0.toDouble()              //价格
        var isfree = 0                      //0 = 免费，1 = 收费
        var joinCount = 0                   //参与人数
        var starttime = ""                  //开播时间，type=2时为空
        var status = 0                      //直播状态，type=2时为空
        var subtitle = ""                   //标题
        var chatRoomName = ""               //课堂名称
    }

}
//热门直播
data class ClassHotBean(var id: String ?){
   var name = ""
   var userCode = ""
   var headPic = ""
   var count = 0
}

data class ClassLike(var id: String?){
    var type = ""
    var subtitle = ""
    var headPic = ""
    var fee : Double? = null
    var joinNum : String? = null

}

data class ClassOrderBean(var id: String ?){

    var name : String ?= null                 //用户名
    var headPic : String ?= null             //用户头像
    var userCode : String ?= null            //用户新媒号
    var fee : Double ?= null                  //购买价格
    var midFee : Double ?= null               //平台中间费
    var saleFee : Double ?= null              //分销费
    var realIncome : Double ?= null           //实际获取收益
    var paySource : String ?= null            //来源0:APP  1:微信 5:其他
    var signuptime : Long ?= null
    var facePic : String ?= null
    var title : String ?= null
    var type : String ?= null
}
data class ClassHeadOrderBean(var id: String ?){

    var realIncome : Double ?= null           //获取收益
    var count : Int ?= null                    //笔数
}

//课堂插入内容
data class LiveInsertBean(var id: Int = 0): Serializable{

    var infoType = ""   // 0.圈子 1：文章，2：课程，3：系列课，4：交易，5：商品，6：个人名片

    //共同属性
    var cover = ""
        get() {
            if(dealBean != null){
                return dealBean!!.picUrl
            }else if(mallBean != null) {
                return mallBean!!.facePic
            }else{
                return field
            }
        }
    var title = ""
        get() {
            if(dealBean != null){
                return dealBean!!.title
            }else if(mallBean != null) {
                return mallBean!!.name
            }else{
                return field
            }
        }
    var audit: String ? = null  //审核标记0：新提交，未审核；1：审核通过；2：审核不通过


    //圈子
    var content = ""
    var infoNum = 0    //帖子数量
    var attention = 0  //关注数量


    //文章
    var author = ""
    var date = ""
    var readCount = ""


    //课堂
    var chatRoomName: String ?= null
    var type = 1                        //业务类型，1=话题，2=系列课
    var fee : String ?= null            //费用价格
    var isFree = 0                      //是否免费,0=免费，1=收费
    var joinCount : String ?= null      //参与人次
    var isSelf : String ?= null         //是否是自己的课程
    var status : String ?= null         //直播状态。1：预告，2：直播中，3：结束
    var time : String ?= null           //直播时间
    var chatInfoCount : String ?= null  //系列课内的课程数
    var showinfo : String ?= null       //是否下架 0  正常  1结束  2下架


    //交易
    var dealBean: DealListBean ? = null

    //商品
    var mallBean: CategoryBean ? = null

    data class PriceList(var id: Int): Serializable{
        var storeId: Int ?= null
        var sum: Int ?= null
        var name: String? = null
        var price: String? = null
    }

    companion object {
        @JvmStatic
        fun converByCirlce(data: MineCircleBean?): LiveInsertBean{
            val bean = LiveInsertBean()
            data?.let {
                bean.id = it.id
                bean.cover = it.cover
                bean.content = it.content
                bean.title = it.groupName
                bean.infoNum = it.infoNum
                bean.attention = it.attention
            }
            return bean
        }

        @JvmStatic
        fun converByArticle(data: NewsBean?): LiveInsertBean{
            val bean = LiveInsertBean()
            data?.let {
                bean.id = it.id.toInt()
                bean.cover = it.cover
                bean.title = it.title
                bean.author = it.source
                bean.date = it.date
                bean.readCount = it.readCount
                bean.audit = it.audit
            }
            return bean
        }

        @JvmStatic
        fun converByClass(data: ChatInfosBean?): LiveInsertBean{
            val bean = LiveInsertBean()
            data?.let {
                bean.id = it.id.toInt()
                if(it.cover.isNullOrEmpty()){
                    bean.cover = it.facePic
                }else{
                    bean.cover = it.cover
                }
                if (!it.title.isNullOrEmpty()){
                    bean.title = it.title
                }else{
                    bean.title = it.subtitle
                }
                bean.audit = it.audit
                bean.chatRoomName = it.chatRoomName
                bean.type = it.type
                bean.fee = it.fee
                bean.isFree = it.getIsFree()
                bean.joinCount = it.joinCount
                bean.isSelf = it.isSelf
                bean.status = it.status
                bean.time = it.time
                bean.chatInfoCount = it.chatInfoCount
                bean.showinfo = it.showinfo
            }
            return bean
        }

        @JvmStatic
        fun converByDeal(data: DealListBean?): LiveInsertBean{
            val bean = LiveInsertBean()
            data?.let {
                bean.dealBean = it
            }
            return bean
        }

        @JvmStatic
        fun converByMall(data: CategoryBean?): LiveInsertBean{
            val bean = LiveInsertBean()
            data?.let {
                bean.mallBean = data
            }
            return bean
        }

    }
}