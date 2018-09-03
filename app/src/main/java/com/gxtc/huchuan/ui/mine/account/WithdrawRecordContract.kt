package com.gxtc.huchuan.ui.mine.account

import com.gxtc.commlibrary.BaseListPresenter
import com.gxtc.commlibrary.BaseListUiView
import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.commlibrary.data.BaseSource
import com.gxtc.huchuan.bean.WithdrawRecordBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/8/31.
 */
interface WithdrawRecordContract {

    interface View : BaseUiView<WithdrawRecordContract.Presenter>,BaseListUiView<WithdrawRecordBean>{
        fun showData(datas: MutableList<WithdrawRecordBean>)
    }


    interface Presenter : BasePresenter,BaseListPresenter{
        fun getDataList()
    }


    interface Source : BaseSource{
        fun getDataList(token : String , start: Int, callBack: ApiCallBack<MutableList<WithdrawRecordBean>>)
    }

}