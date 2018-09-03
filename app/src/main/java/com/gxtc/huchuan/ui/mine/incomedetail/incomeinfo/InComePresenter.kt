package com.gxtc.huchuan.ui.mine.incomedetail.incomeinfo

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.huchuan.bean.NewDistributeBean
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2017/12/22.
 */
class InComePresenter(view:InComeContract.View):BasePresenter,InComeContract.Presenter {

    var mView:InComeContract.View? = null
    var data:InComeRepository? = null

    init {
        mView = view
        mView?.setPresenter(this)
        data = InComeRepository()
    }

    override fun getData(isLoadMore: Boolean,map : HashMap<String,String>) {
        if(!isLoadMore){
            mView?.showLoad()
        }
        data?.getData(map!!,object :ApiCallBack<ArrayList<NewDistributeBean>>(){
            override fun onSuccess(data: ArrayList<NewDistributeBean>?) {
                mView?.showLoadFinish()
                if(data == null || data.size == 0){
                    mView?.showEmpty()
                    return
                }
                mView?.showData(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showError(message)
            }

        })
    }
    override fun getProfitDetail(isLoadMore: Boolean, map: HashMap<String, String>) {
        if(!isLoadMore){
            mView?.showLoad()
        }
        data?.getProfitDetailData(map!!,object :ApiCallBack<ArrayList<NewDistributeBean>>(){
            override fun onSuccess(data: ArrayList<NewDistributeBean>?) {
                mView?.showLoadFinish()
                if(data == null || data.size == 0){
                    mView?.showEmpty()
                    return
                }
                mView?.showProfitDetail(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
                mView?.showError(message)
            }

        })
    }

    override fun start() {}

    override fun destroy() {
        data?.destroy()
    }
}