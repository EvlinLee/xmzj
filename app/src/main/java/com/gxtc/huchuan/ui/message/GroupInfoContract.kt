package com.gxtc.huchuan.ui.message

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.MessageBean

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/13.
 */
interface GroupInfoContract {

    interface View : BaseUiView<GroupInfoContract.Presenter> {
        fun showPic(url: String)
        fun showMembers(datas: MutableList<MessageBean>)
        fun showRoleType(bean: MessageBean)
    }

    interface Presenter : BasePresenter {
        fun uploadPic(path: String?)
        fun getMembers(id: String)
        fun getRoleType(id: String)
    }

}