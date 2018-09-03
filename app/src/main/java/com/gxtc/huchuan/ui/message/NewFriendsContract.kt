package com.gxtc.huchuan.ui.message

import com.gxtc.commlibrary.*
import com.gxtc.huchuan.bean.FocusBean

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/11.
 */
interface NewFriendsContract {

    interface View : BaseUiView<NewFriendsContract.Presenter>,BaseListUiView<FocusBean>{
        fun showNewFriends(datas : MutableList<FocusBean>)
        fun showFollowUser(userCode: String, isSuccess: Boolean, error: String)
        fun showQueryResult(datas: MutableList<FocusBean>)
        fun showOverlookResult(isSuccess: Boolean, error: String,userCode: String)
    }

    interface Presenter : BasePresenter,BaseListPresenter{
        fun getNewFriends(groupChatId: String)
        fun followUser(userCode : String)
        fun queryUser(key: String)
        fun overlook(userCode : String)
    }

}