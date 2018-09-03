package com.gxtc.huchuan.ui.mall

import com.gxtc.commlibrary.*
import com.gxtc.huchuan.bean.MallShopCartBean

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/10/23.
 */
interface MallShopCartContract {

    interface View: BaseUiView<MallShopCartContract.Presenter>,BaseListUiView<MallShopCartBean>{
        fun showData(datas: MutableList<MallShopCartBean>)

        fun showRemoveSuccess(id: String)
    }

    interface Presenter: BasePresenter,BaseListPresenter{
        fun getData()

        fun removeGoods(id: String)
    }
}