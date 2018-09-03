package com.gxtc.huchuan.ui.live.conversation

import com.gxtc.commlibrary.BaseListPresenter
import com.gxtc.commlibrary.BaseListUiView
import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.ChatInfosBean
import com.gxtc.huchuan.bean.LiveInsertBean
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/3/5.
 */
interface LiveInsertChooseContract {

    interface View : BaseUiView<LiveInsertChooseContract.Presenter>, BaseListUiView<LiveInsertBean>{
        fun showData(datas: MutableList<LiveInsertBean>)
        fun showSearchData(datas: MutableList<LiveInsertBean>)
        fun showShareSuccess(message: Message)
    }

    interface Presenter : BasePresenter, BaseListPresenter{
        fun getData()
        fun searchData(key: String?)
        fun sendShareMessage(infosBean: ChatInfosBean, bean: LiveInsertBean)
    }

}