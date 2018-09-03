package com.gxtc.huchuan.ui.deal.guarantee

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUserView
import com.gxtc.huchuan.bean.GoodsDetailedBean

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/3/24.
 */
interface GuaranteeDetailedContract {

    interface View : BaseUserView<GuaranteeDetailedContract.Presenter>{
        fun showData(goodsBean: GoodsDetailedBean)
        fun showAgreen(data: GoodsDetailedBean)
        fun showDisargreen()
    }


    interface Presenter : BasePresenter{
        fun getData(id: String)

        fun agreeDeal(map: HashMap<String, String>)

        fun disagreenDeal(id: String)
    }
}