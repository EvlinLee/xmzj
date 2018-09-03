package com.gxtc.huchuan.ui.circle.homePage

import com.gxtc.commlibrary.BaseListPresenter
import com.gxtc.commlibrary.BaseListUiView
import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.CircleBean

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/8/17.
 */
interface ChatGroupContract {

    interface View : BaseUiView<ChatGroupContract.Presenter>,BaseListUiView<CircleBean>{
        fun showData(datas: MutableList<CircleBean>)
    }


    interface Presenter : BasePresenter,BaseListPresenter{
        fun getData(groupId : Int)
    }

}