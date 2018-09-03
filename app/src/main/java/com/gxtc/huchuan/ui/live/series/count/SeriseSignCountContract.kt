package com.gxtc.huchuan.ui.live.series.count

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.ChatJoinBean
import com.gxtc.huchuan.bean.SeriseCountBean
import com.gxtc.huchuan.http.ApiCallBack
import java.util.ArrayList

/**
 * Created by zzg on 2017/12/12.
 */
class SeriseSignCountContract {

    interface View :BaseUiView<Present>{
        fun showSeriseSignCount(data: ArrayList<ChatJoinBean.MemberBean>)
    }

    interface Present :BasePresenter{
        fun getlistJoinMember(token: String, userType: String, type : String, chatId: String, searchKey: String, isloadMore: Boolean)

        fun getSeriseSignCount(token:String,id:String,type:String,searchKey:String,isloadMore:Boolean)
    }
}