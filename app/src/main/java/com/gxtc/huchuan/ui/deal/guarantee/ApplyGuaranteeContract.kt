package com.gxtc.huchuan.ui.deal.guarantee

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUserView
import com.gxtc.huchuan.bean.GoodsDetailedBean

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/3/22.
 */
interface ApplyGuaranteeContract {


    interface View: BaseUserView<ApplyGuaranteeContract.Presenter>{
        fun showSubmitSuccess(id: String)
    }


    interface Presenter: BasePresenter{
        fun submint(map: Map<String, String>)
    }

}