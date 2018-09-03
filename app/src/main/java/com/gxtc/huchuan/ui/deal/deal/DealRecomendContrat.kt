package com.gxtc.huchuan.ui.deal.deal

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.BaseUiView
import com.gxtc.huchuan.bean.DealData
import com.gxtc.huchuan.bean.DealListBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2017/10/28.
 */
class DealRecomendContrat {

    public interface View : BaseUiView<Presenter>{
        fun showDealRecomemndData(datas: DealData)
    }

    public interface  Presenter :BasePresenter{
        fun getDealRecomemndData(isLoadMore:Boolean)
    }
}