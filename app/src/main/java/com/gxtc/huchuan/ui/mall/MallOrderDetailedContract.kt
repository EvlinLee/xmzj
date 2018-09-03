package com.gxtc.huchuan.ui.mall

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.AddressBean

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/10/23.
 */
interface MallOrderDetailedContract {

    interface View: BaseUiView<MallOrderDetailedContract.Presenter>{
        fun showAddress(beans: List<AddressBean>)
    }

    interface Presenter: BasePresenter{
        fun getAddressList()
    }

}