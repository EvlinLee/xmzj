package com.gxtc.huchuan.ui.mine.incomedetail.incomeinfo

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.NewDistributeBean

/**
 * Created by zzg on 2017/12/22.
 */
class InComeContract {
    interface View : BaseUiView<Presenter>{
        fun showData(data:ArrayList<NewDistributeBean>)
        fun showProfitDetail(data:ArrayList<NewDistributeBean>)
    }
    interface Presenter : BasePresenter{
        fun getData(isLoadMore:Boolean,map:HashMap<String,String>)
        fun getProfitDetail(isLoadMore:Boolean,map:HashMap<String,String>)
    }
}