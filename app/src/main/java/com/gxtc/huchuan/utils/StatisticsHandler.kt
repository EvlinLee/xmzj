package com.gxtc.huchuan.utils

import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.bean.CheckBean
import com.gxtc.huchuan.helper.RxTaskHelper
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.service.AllApi
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by zzg on 2018/1/15.
 * 统计的相关业务
 * 接口使用区域：
   首页 ： 热门课程 1/1/1； 最新视频 2/1/1； 资源交易 3/1/1； 加入圈子 4/12/1； 营销软件 6/1/1；
   加号入口： 课程  1/1/2； 视频     2/1/2； 交易     3/1/2； 商城     6/1/2； 分销     8/1/2； 钱包  9/1/2
   对应字段名   ( businessType/useType/clickType )


  热门课程 => 免费课程 1/19/1;
  人气课程 1/19/2;
  精品课程 1/19/3;
  已报课程 1/19/4;
  对应字段名   ( businessType/useType/extraType )

  businessType 业务类型
  useType 使用类型
  clickType 点击类型
  extraType 分类类型
 */

class StatisticsHandler(map: HashMap<String, String>) {

    var map:HashMap<String,String>? = null

    init {
       this.map = map!!
    }

    companion object {
        @JvmField
        val STRING_CHANEL_NEW_VIDEO:Int = 1 // 最新视屏
        @JvmField
        val STRING_CHANEL_MAll:Int = 2 // 营销工具
        @JvmField
        val STRING_CHANEL_HOT_CLASS:Int = 3//热门课程
        @JvmField
        val STRING_CHANEL_FIND_CIRCLE:Int = 4//发现圈子
        @JvmField
        val STRING_CHANEL_DEAL:Int = 5//资源交易
        @JvmField
        val STRING_CHANEL_DISTRIBUTE:Int = 6//分销
        @JvmField
        val STRING_CHANEL_PACKGE:Int = 7//钱包
        @JvmField
        val STRING_CHANEL_TOP_FREE_CLASS:Int = 8//免费课程
        @JvmField
        val STRING_CHANEL_TOP_HOT_CLASS:Int = 9//人气课程
        @JvmField
        val STRING_CHANEL_TOP_BEST_CLASS:Int = 10//精品课程
        @JvmField
        val STRING_CHANEL_TOP_SIGNED_CLASS:Int = 11//已报课程

        fun  getInstant() = Holder.INSTANT
    }

    private object Holder {
        val map = HashMap<String,String>()
        val INSTANT = StatisticsHandler(map)
    }

    fun handleStatisticsByType(type:Int,fromAdd:Boolean){
        map?.clear()
        when(type){
            STRING_CHANEL_NEW_VIDEO ->  {
                map?.put("businessType", "2")
                map?.put("useType", "1")
                if(!fromAdd){
                    map?.put("clickType", "1")
                }else{
                    map?.put("clickType", "2")
                }
            }
            STRING_CHANEL_MAll ->  {
                map?.put("businessType", "6")
                map?.put("useType", "1")
                if(!fromAdd){
                    map?.put("clickType", "1")
                }else{
                    map?.put("clickType", "2")
                }
            }
            STRING_CHANEL_HOT_CLASS ->  {
                map?.put("businessType", "1")
                map?.put("useType", "1")
                if(!fromAdd){
                    map?.put("clickType", "1")
                }else{
                    map?.put("clickType", "2")
                }
            }
            STRING_CHANEL_FIND_CIRCLE ->  {
                map?.put("businessType", "4")
                map?.put("useType", "12")
                map?.put("clickType", "1")
            }
            STRING_CHANEL_DEAL ->  {
                map?.put("businessType", "3")
                map?.put("useType", "1")
                if(!fromAdd){
                    map?.put("clickType", "1")
                }else{
                    map?.put("clickType", "2")
                }
            }
            STRING_CHANEL_DISTRIBUTE ->  {
                map?.put("businessType", "8")
                map?.put("useType", "1")
                map?.put("clickType", "2")
            }
            STRING_CHANEL_PACKGE ->  {
                map?.put("businessType", "9")
                map?.put("useType", "1")
                map?.put("clickType", "2")
            }
            STRING_CHANEL_TOP_FREE_CLASS ->  {
                map?.put("businessType", "1")
                map?.put("useType", "19")
                map?.put("extraType", "1")
            }
            STRING_CHANEL_TOP_HOT_CLASS ->  {
                map?.put("businessType", "1")
                map?.put("useType", "19")
                map?.put("extraType", "2")
            }
            STRING_CHANEL_TOP_BEST_CLASS ->  {
                map?.put("businessType", "1")
                map?.put("useType", "19")
                map?.put("extraType", "3")
            }
            STRING_CHANEL_TOP_SIGNED_CLASS ->  {
                map?.put("businessType", "1")
                map?.put("useType", "19")
                map?.put("extraType", "4")
            }
        }
        getStatistics(map!!)
    }

    fun getStatistics(map:HashMap<String,String>){
        val subscription = AllApi.getInstance()
                .saveClickRecord(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver(object :ApiCallBack<Object>(){
                    override fun onSuccess(data: Object?) = Unit

                    override fun onError(errorCode: String?, message: String?) =
                            ToastUtil.showShort(MyApplication.getInstance(),message)

                }))
        RxTaskHelper.getInstance().addTask(this,subscription)
    }

    fun destroy() = RxTaskHelper.getInstance().cancelTask(this)

}