package com.gxtc.huchuan.ui.mall.order

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.AllStatusSumsBean
import com.gxtc.huchuan.bean.CoustomMerBean
import com.gxtc.huchuan.bean.MallBean

/**
 * Created by zzg on 2017/10/24.
 */
interface MallCustermersContract {

    interface View : BaseUiView<Prensenter>{
        fun showCustermersList(datas:ArrayList<CoustomMerBean>)
        fun showAllStatusSums(data: ArrayList<AllStatusSumsBean>)
        fun showHeadIcon(datas: List<MallBean>)
        fun showActivitysData(datas: List<MallBean>)
    }
    interface Prensenter : BasePresenter{
        fun getCustermersList(type: String?,rand: String?)
        fun getAllStatusSums(token:String?)
        fun getTags(token: String?)
        fun getActivitysData(token: String?, start: Int?)
    }
}