package com.gxtc.huchuan.ui.deal

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.huchuan.bean.DealData
import com.gxtc.huchuan.bean.DealListBean
import com.gxtc.huchuan.data.DealRecommendRepository
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.ui.deal.deal.DealRecomendContrat

/**
 * Created by zzg on 2017/10/28.
 */
class DealRecommendPresenter(view:DealRecomendContrat.View) :BasePresenter , DealRecomendContrat.Presenter {
    var view :DealRecomendContrat.View? = null
    var datas : DealRecommendRepository? = null
    var map : HashMap<String, String>? = null
    var start : Int = 0

    init {
        this.view = view
        this.view!!.setPresenter(this)
        map =  HashMap<String, String>()
        datas = DealRecommendRepository()
    }
    override fun getDealRecomemndData(isLoadMore: Boolean) {
        if(isLoadMore){
            start = start + 15
        }else{
            start = 0
        }
        map!!.put("flag","0")
        map!!.put("start",start.toString())
        map!!.put("type","3")
        datas!!.getDealRecomemndData(map!!,object : ApiCallBack<DealData>(){
            override fun onSuccess(data: DealData?) {
                view!!.showDealRecomemndData(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
                view!!.showError(message)
            }

        })
    }

    override fun start() {}

    override fun destroy() {
        datas!!.destroy()
    }
}