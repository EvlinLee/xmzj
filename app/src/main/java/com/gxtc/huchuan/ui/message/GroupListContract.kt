package com.gxtc.huchuan.ui.message

import com.gxtc.commlibrary.BaseListPresenter
import com.gxtc.commlibrary.BaseListUiView
import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.FocusBean
import com.gxtc.huchuan.bean.MessageBean

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/11.
 */
interface GroupListContract {

    interface View : BaseUiView<Presenter>, BaseListUiView<MessageBean> {
        fun showGroups(datas : MutableList<MessageBean>)
        fun showQueryResult(datas: MutableList<MessageBean>)
    }

    interface Presenter : BasePresenter, BaseListPresenter {
        fun getGroups()
        fun queryGroup(key: String)
    }

}