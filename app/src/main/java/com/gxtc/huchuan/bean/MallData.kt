package com.gxtc.huchuan.bean

import android.os.Parcel
import android.os.Parcelable
import io.rong.common.ParcelUtils
import java.io.Serializable

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/10/31.
 */
data class MallDetailBean(var storeId: Int?): Parcelable {

    var pinpai: String ? = null
    var sortName: String ? = null
    var storeName : String ? = null
    var facePic : String ? = null
    var originalPrice : String ? = null
    var price : String ? = null
    var yinYu : String ? = null
    var specification : String ? = null
    var description : String ? = null
    var descUrl : String ? = null
    var sort : String ? = null
    var isCollect = 0               //是否收藏
    var priceList: ArrayList<DetailParamBean> ? = null      //商品规格集合
    var currParam: DetailParamBean? = null

    companion object {
        @JvmField
        val CREATOR : Parcelable.Creator<MallDetailBean> = object : Parcelable.Creator<MallDetailBean> {
            override fun newArray(size: Int): Array<MallDetailBean?> = arrayOfNulls(size)

            override fun createFromParcel(source: Parcel): MallDetailBean = MallDetailBean(source)

        }
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        ParcelUtils.writeToParcel(dest, storeId)
        ParcelUtils.writeToParcel(dest, pinpai)
        ParcelUtils.writeToParcel(dest, sortName)
        ParcelUtils.writeToParcel(dest, storeName)
        ParcelUtils.writeToParcel(dest, facePic)
        ParcelUtils.writeToParcel(dest, originalPrice)
        ParcelUtils.writeToParcel(dest, price)
        ParcelUtils.writeToParcel(dest, yinYu)
        ParcelUtils.writeToParcel(dest, specification)
        ParcelUtils.writeToParcel(dest, description)
        ParcelUtils.writeToParcel(dest, descUrl)
        ParcelUtils.writeToParcel(dest, sort)
        ParcelUtils.writeToParcel(dest, isCollect)
        dest?.writeParcelable(currParam,flags)
        dest?.writeTypedList(priceList)
    }

    constructor(parcel: Parcel) : this(ParcelUtils.readIntFromParcel(parcel)) {
        pinpai = ParcelUtils.readFromParcel(parcel)
        sortName = ParcelUtils.readFromParcel(parcel)
        storeName = ParcelUtils.readFromParcel(parcel)
        facePic = ParcelUtils.readFromParcel(parcel)
        originalPrice = ParcelUtils.readFromParcel(parcel)
        price = ParcelUtils.readFromParcel(parcel)
        yinYu = ParcelUtils.readFromParcel(parcel)
        specification = ParcelUtils.readFromParcel(parcel)
        description = ParcelUtils.readFromParcel(parcel)
        descUrl = ParcelUtils.readFromParcel(parcel)
        sort = ParcelUtils.readFromParcel(parcel)
        isCollect = ParcelUtils.readIntFromParcel(parcel)
        currParam = parcel.readParcelable(MallDetailBean::class.java.classLoader)
        priceList = arrayListOf()
        parcel.readTypedList(priceList, DetailParamBean)
    }

    constructor(storeId: Int = 0,
                pinpai: String = "",               //品牌信息
                sortName: String = "",             //商品分类名称
                storeName: String = "",            //商品名称
                facePic: String = "",              //商品封面
                originalPrice: String = "",        //商品原价
                price: String = "",                //商品单价
                yinYu: String = "",                //引语
                specification: String = "",        //产品参数
                description: String = "",          //产品详情
                descUrl: String = "",              //详情url
                sort: String = "",                 //分类ID
                isCollect: Int = 0,                 //是否收藏
                priceList: ArrayList<DetailParamBean>) : this(storeId) {

        this.pinpai = pinpai
        this.sortName = sortName
        this.storeName = storeName
        this.facePic = facePic
        this.originalPrice = originalPrice
        this.price = price
        this.yinYu = yinYu
        this.specification = specification
        this.description = description
        this.descUrl = descUrl
        this.sort = sort
        this.isCollect = isCollect
        this.priceList = priceList

    }

    override fun describeContents(): Int = 0

    data class DetailParamBean(var id: Int = 0,                 //规格ID
                               var name: String ?,           //规格名称
                               var price: String ?,          //金额
                               var storeId: Int = 0,            //商品ID
                               var sum: Int = 0): Parcelable {   //库存

        constructor(parcel: Parcel) : this(
                ParcelUtils.readIntFromParcel(parcel),
                ParcelUtils.readFromParcel(parcel),
                ParcelUtils.readFromParcel(parcel),
                ParcelUtils.readIntFromParcel(parcel),
                ParcelUtils.readIntFromParcel(parcel)) {
        }

        override fun writeToParcel(dest: Parcel?, flags: Int) {
            ParcelUtils.writeToParcel(dest, id)
            ParcelUtils.writeToParcel(dest, name)
            ParcelUtils.writeToParcel(dest, price)
            ParcelUtils.writeToParcel(dest, storeId)
            ParcelUtils.writeToParcel(dest, sum)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<DetailParamBean> {
            override fun createFromParcel(parcel: Parcel): DetailParamBean = DetailParamBean(parcel)

            override fun newArray(size: Int): Array<DetailParamBean?> = arrayOfNulls(size)
        }
    }
}



data class CategoryBean(var merchandiseId: String = "0",      //商品ID
                           var categoryName: String = "0",     //分类名称
                           var facePic: String = "0",          //商品封面
                           var name: String = "",       //商品名称
                           var yinyu: String = "",         //商品引语
                           var redirectUrl: String = "",      //商品详情页（暂时无用，待定）
                           var price: String = "",            //价格
                           var priceList: ArrayList<MallBean.PriceList> )            //价格

data class CoustomMerBean(var userCode: String = "",      //用户编码
                            var name: String = "",     //用户名字
                            var headPic: String = "")  //用户头像


data class OrderBean(var createTime: String ?,      //创建时间
                     var money: String ?,        //订单总金额
                     var name: String ?,         //店铺名称
                     var orderId: String ?,      //订单号
                     var orderStatus: String ?,  //订单状态   订单状态: 默认获取全部 0:已取消 1:未支付 2:待发货 3:待收货 4：待评价 5：已结束
                     var transType: String  ?,   //订单代号（固定“SC”）
                     var storeOrderList: ArrayList<OrderMallBean> ?) {

    data class OrderMallBean(var name: String?, //商品名称
                             var storeId: Int?,
                             var storePriceId: Int?,
                             var picUrl: String?, //商品封面
                             var price: Double?, //单价
                             var priceName: String?, //规格名称
                             var sum: Int = 0)            //购买数量
}

data class MallShopCartBean(var id: String = "",
                            var amount: Int = 0,                 //数量
                            var storePriceId: Int = 0,           //款式Id
                            var storePriceName: String = "",     //款式名称
                            var storePricePrice: String = "",    //价格
                            var storeId: String = "",            //商品id
                            var storeName: String = "",          //商品名称
                            var picUrl: String = "" ){           //商品图片

    var isSelect = false

    override fun equals(other: Any?): Boolean {
        return other is MallShopCartBean && id == other.id
    }
}

data class OrderDetailBean(var province: String = "",
                           var city: String = "",
                           var area: String = "",
                           var address: String = "",
                           var createDate: String = "",
                           var feeType: String = "",
                           var logisticeCopany: String = "",
                           var logisticsNo: String = "",
                           var name: String = "",
                           var orderMoney: String = "",
                           var orderNo: String = "",
                           var payType: String = "",
                           var postalId: String = "",
                           var status: String = "",
                           var tel: String = "",
                           var isLogistics: String = "",
                           var message: String = "",
                           var orderList: ArrayList<OrderMallTypeBean>?,
                           var statusList:ArrayList<OrderDetailStatusBean>?){
    data class OrderMallTypeBean(var storeId: String?,
                                 var storeName: String?,
                                 var price: Double?,
                                 var facePic: String?,
                                 var priceName: String?,
                                 var amount: Int = 0)

    data class OrderDetailStatusBean(var createTime: String?,
                                       var status: String?)
}

data class AllStatusSumsBean(var status: String?,
                               var sum: Int = 0)

/**
 * 因为收藏那里的序列化实现接口不是用 Parcelable 的 不能 复用
 * @see MallDetailBean
 */
data class CollectMallDetailBean(var storeId: Int?): Serializable {

    var pinpai: String ? = null
    var sortName: String ? = null
    var storeName : String ? = null
    var facePic : String ? = null
    var originalPrice : String ? = null
    var price : String ? = null
    var yinYu : String ? = null
    var specification : String ? = null
    var description : String ? = null
    var descUrl : String ? = null
    var sort : String ? = null
    var isCollect = 0               //是否收藏
    var priceList: ArrayList<DetailParamBean> ? = null      //商品规格集合
    var currParam: DetailParamBean? = null

    constructor(storeId: Int = 0,
                pinpai: String = "",               //品牌信息
                sortName: String = "",             //商品分类名称
                storeName: String = "",            //商品名称
                facePic: String = "",              //商品封面
                originalPrice: String = "",        //商品原价
                price: String = "",                //商品单价
                yinYu: String = "",                //引语
                specification: String = "",        //产品参数
                description: String = "",          //产品详情
                descUrl: String = "",              //详情url
                sort: String = "",                 //分类ID
                isCollect: Int = 0,                 //是否收藏
                priceList: ArrayList<DetailParamBean>) : this(storeId) {

        this.pinpai = pinpai
        this.sortName = sortName
        this.storeName = storeName
        this.facePic = facePic
        this.originalPrice = originalPrice
        this.price = price
        this.yinYu = yinYu
        this.specification = specification
        this.description = description
        this.descUrl = descUrl
        this.sort = sort
        this.isCollect = isCollect
        this.priceList = priceList

    }


    data class DetailParamBean(var id: Int = 0,                 //规格ID
                               var name: String ?,           //规格名称
                               var price: String ?,          //金额
                               var storeId: Int = 0,            //商品ID
                               var sum: Int = 0): Serializable {   //库存
    }
}