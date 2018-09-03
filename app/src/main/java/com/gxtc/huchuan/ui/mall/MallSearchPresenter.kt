package com.gxtc.huchuan.ui.mall

import android.content.Context
import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.bean.CategoryBean
import com.gxtc.huchuan.bean.MallBean
import com.gxtc.huchuan.data.MallRepository
import com.gxtc.huchuan.data.MallSearchContract
import com.gxtc.huchuan.data.MallSearchRepository
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2017/10/24.
 */
class MallSearchPresenter(view:MallSearchContract.view) : BasePresenter, MallSearchContract.Presenter {

    var view : MallSearchContract.view? = null
    var mData : MallSearchRepository? = null
    var mallRepository : MallRepository? = null
    var start = 0

     init {
         this.view = view
         this.view?.setPresenter(this)
         mData = MallSearchRepository()
         mallRepository = MallRepository()
     }

    override fun getSearchData(isLoadMore:Boolean,map: HashMap<String, String>) {
        if(!isLoadMore){
            start = 0
        }else{
            start += 15
        }
        map.put("start", start.toString())
        mData?.getSearchData(map,object : ApiCallBack<ArrayList<CategoryBean>>(){
            override fun onSuccess(data: ArrayList<CategoryBean>?) {
                if(start == 0 && (data == null || data.size == 0)){
                    view?.showEmpty()
                    return
                }
                view?.showSearchData(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
                ToastUtil.showShort(MyApplication.getInstance(),message)
            }

        })
    }

    override fun addShopCar(map: HashMap<String, String>) {
        mallRepository?.addShopCart(map,object :ApiCallBack<Any>(){
            override fun onSuccess(data: Any?) {
                view?.showAddShopCarResule(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
                ToastUtil.showShort(MyApplication.getInstance(),message)
            }

        })
    }

    override fun getActivitysData(token: String, start: Int) {
        mallRepository!!.getActivityData(start,object :ApiCallBack<List<MallBean>>(){
            override fun onSuccess(data:List<MallBean>?) {
                view?.showActivitysData(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
                ToastUtil.showShort(MyApplication.getInstance(),message)
            }

        })
    }

    override fun getTags(token: String) {
        mallRepository!!.getTags(0,object :ApiCallBack<List<MallBean>>(){
            override fun onSuccess(data:List<MallBean>?) {
                view?.showHeadIcon(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
                ToastUtil.showShort(MyApplication.getInstance(),message)
            }

        })
    }

    override fun start() {}

    override fun destroy() {
        mData?.destroy()
        mallRepository?.destroy()
    }
}