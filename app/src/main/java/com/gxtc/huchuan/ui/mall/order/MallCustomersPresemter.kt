package com.gxtc.huchuan.ui.mall.order

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.bean.AllStatusSumsBean
import com.gxtc.huchuan.bean.CoustomMerBean
import com.gxtc.huchuan.bean.MallBean
import com.gxtc.huchuan.data.MallCustomersRepository
import com.gxtc.huchuan.data.MallRepository
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2017/10/24.
 */
class MallCustomersPresemter(view: MallCustermersContract.View) : BasePresenter, MallCustermersContract.Prensenter {

    var  view : MallCustermersContract.View? = null
    var  datas : MallCustomersRepository? = null
    var  mMallRepository : MallRepository? = null

    init {
        this.view = view
        this.view?.setPresenter(this)
        datas = MallCustomersRepository()
        mMallRepository = MallRepository()
    }

    override fun getCustermersList(type: String?,rand: String?) {
        datas?.getCustermersList(type!!,rand!!,object :ApiCallBack<ArrayList<CoustomMerBean>>(){
            override fun onSuccess(data: ArrayList<CoustomMerBean>?) {
                view?.showCustermersList(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
                view?.showError(message)
            }

        })
    }

    override fun getAllStatusSums(token: String?) {
        datas?.getAllStatusSums(token!!,object :ApiCallBack<ArrayList<AllStatusSumsBean>>(){
            override fun onSuccess(data:ArrayList<AllStatusSumsBean>?) {
                view?.showAllStatusSums(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
                view?.showError(message)
            }

        })
    }

    override fun getTags(token: String?) {
        mMallRepository?.getTags(0,object :ApiCallBack<List<MallBean>>(){
            override fun onSuccess(data:List<MallBean>?) {
                view?.showHeadIcon(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
               ToastUtil.showShort(MyApplication.getInstance(),message)
            }

        })
    }

    override fun getActivitysData(token: String?, start: Int?) {
        mMallRepository?.getActivityData(start!!,object :ApiCallBack<List<MallBean>>(){
            override fun onSuccess(data:List<MallBean>?) {
                view?.showActivitysData(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
                ToastUtil.showShort(MyApplication.getInstance(),message)
            }

        })
    }
    override fun start() {}

    override fun destroy() {
        datas?.destroy()
        mMallRepository?.destroy()
    }
}