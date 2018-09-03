package com.gxtc.huchuan.ui.message

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.commlibrary.BaseUserView
import com.gxtc.huchuan.http.ApiCallBack
import java.util.*

/**
 * Created by zzg on 2017/12/8.
 */
class MessageFragmntContract {
    interface View : BaseUserView<Present>{
        fun showNewFriendsCount(data:Any?);
    }

    interface Present : BasePresenter{
      fun getNewFriendsCount(token:String);
    }
}