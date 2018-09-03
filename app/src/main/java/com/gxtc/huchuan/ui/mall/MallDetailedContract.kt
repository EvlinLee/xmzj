package com.gxtc.huchuan.ui.mall

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.MallDetailBean
import java.util.HashMap

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/10/31.
 */
interface MallDetailedContract {

    interface View: BaseUiView<MallDetailedContract.Presenter> {

        fun showGoodsDetailed(mallDetailBean: MallDetailBean)

        fun showAddShopCartResult(datas: Any)

        fun showCollectResult()
    }

    interface Presenter: BasePresenter {

        fun getGoodsDetailed(id: String)

        fun addShopCar(map: HashMap<String, String>)

        fun collectMall(id: String)
    }

}