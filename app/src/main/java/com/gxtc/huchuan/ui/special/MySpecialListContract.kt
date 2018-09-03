package com.gxtc.huchuan.ui.special

import com.gxtc.commlibrary.BaseListPresenter
import com.gxtc.commlibrary.BaseListUiView
import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.data.SpecialBean

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/5/12.
 */
interface MySpecialListContract {

    interface View: BaseUiView<MySpecialListContract.Presenter>, BaseListUiView<SpecialBean>{
        fun showData(datas: MutableList<SpecialBean>)
    }

    interface Presenter: BasePresenter, BaseListPresenter{
        fun getData()
    }
}