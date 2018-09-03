package com.gxtc.huchuan.data

import com.gxtc.commlibrary.data.BaseSource
import com.gxtc.huchuan.bean.MallBean
import com.gxtc.huchuan.bean.MallDetailBean
import com.gxtc.huchuan.bean.MallShopCartBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/10/23.
 */
interface MallSource : BaseSource {
    fun getAdvertise(mCallBack: ApiCallBack<List<MallBean>>)

    fun getTags(start: Int, mCallBack: ApiCallBack<List<MallBean>>)

    fun getLinesData(start: Int, mCallBack: ApiCallBack<List<MallBean>>)

    fun getGridData(start: Int, mCallBack: ApiCallBack<List<MallBean>>)

    fun getGoodsDetailed(id: String, token: String, callback: ApiCallBack<MallDetailBean>)

    fun addShopCart(map: HashMap<String,String>,mCallBack:ApiCallBack<Any>)

    fun getShopCartList(token: String, callback: ApiCallBack<MutableList<MallShopCartBean>>)

    fun removeGoods(token: String, id: String, callback: ApiCallBack<Any>)

    fun collectMall(map: Map<String, String>, callback: ApiCallBack<Any>)

    fun getActivityData(start: Int, mCallBack: ApiCallBack<List<MallBean>>)
}