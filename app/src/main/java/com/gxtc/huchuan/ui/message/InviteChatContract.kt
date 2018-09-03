package com.gxtc.huchuan.ui.message

import com.gxtc.commlibrary.BaseListPresenter
import com.gxtc.commlibrary.BaseListUiView
import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.FocusBean
import com.gxtc.huchuan.bean.MessageBean

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/7.
 */
interface InviteChatContract {

    interface View : BaseUiView<InviteChatContract.Presenter>, BaseListUiView<FocusBean> {
        fun showData(datas: MutableList<FocusBean>)
        fun showQuery(datas: MutableList<FocusBean>)
        fun showPic(url: String)
        fun showCreateResult(success: Boolean, bean: MessageBean?, error: String?)
    }

    interface Presenter : BasePresenter, BaseListPresenter {
        fun getData(groupChatId: String)
        fun getGroupMeamberData(star:Int,groupChatId: String)
        fun queryUser(name: String)
        fun uploadPic(path: String?)
        fun createGroup(name: String?, picUrl: String?, userCodes: MutableList<FocusBean>)
    }
}