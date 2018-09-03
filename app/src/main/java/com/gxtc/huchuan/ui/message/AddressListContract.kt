package com.gxtc.huchuan.ui.message

import android.content.ContentResolver
import com.gxtc.commlibrary.BaseListPresenter
import com.gxtc.commlibrary.BaseListUiView
import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.PersonInfoBean

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/11/24.
 */
interface AddressListContract {

    interface View: BaseUiView<AddressListContract.Presenter>, BaseListUiView<PersonInfoBean>{

        fun showFriendsData(datas: MutableList<PersonInfoBean>)

        fun showSearchResult(datas: MutableList<PersonInfoBean>?)

        fun showFollowSuccess()

        fun showApplySuccess()
    }


    interface Presenter: BasePresenter, BaseListPresenter{

        fun searchData(key: String)

        fun readContacts(contentResolver: ContentResolver)               //读取通讯录好友

        fun getFriendsByContacts()

        fun folowUser(userCode: String)

        fun applyFriends(userCode: String,message:String)
    }

}