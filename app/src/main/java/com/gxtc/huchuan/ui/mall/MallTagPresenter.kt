package com.gxtc.huchuan.ui.mall

import com.gxtc.commlibrary.BasePresenter
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.bean.CategoryBean
import com.gxtc.huchuan.bean.MallBean
import com.gxtc.huchuan.data.MallRepository
import com.gxtc.huchuan.data.MallTagRepository
import com.gxtc.huchuan.http.ApiCallBack

/**
 * Created by zzg on 2017/10/24.
 */
class MallTagPresenter(view:MallTagDataContract.view ):BasePresenter,MallTagDataContract.presenter {

    var view:MallTagDataContract.view ? = null
    var mData: MallTagRepository? = null
    var mallRepository: MallRepository? = null
    var map: HashMap<String,String>? = null
    var start = 0

    init {
        this.view = view
        this.view!!.setPresenter(this)
        map = HashMap()
        mData = MallTagRepository()
        mallRepository = MallRepository()
    }

    override fun getTagDatas(isLoadMore:Boolean,categoryId:String) {
        if(!isLoadMore){
            start = 0
        }else{
            start = start + 15
        }
        map!!.put("categoryId",categoryId)
        map!!.put("start",start.toString())
        mData!!.getTagDatas(map!!,object :ApiCallBack<ArrayList<CategoryBean>>(){
            override fun onSuccess(data: ArrayList<CategoryBean>?) {
                if(start == 0 && (data == null || data.size == 0)){
                    view!!.showEmpty()
                }
                view!!.showTagDatas(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
               view!!.showError(message)
            }

        })
    }

    override fun addShopCar(map: java.util.HashMap<String, String>) {
        mallRepository!!.addShopCart(map!!,object :ApiCallBack<Any>(){
            override fun onSuccess(data: Any?) {
                view!!.showAddShopCarResule(data!!)
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
                ToastUtil.showLong(MyApplication.getInstance(),message)
            }

        })
    }

    override fun getActivitysData(token: String, start: Int) {
        mallRepository!!.getActivityData(start,object :ApiCallBack<List<MallBean>>(){
            override fun onSuccess(data:List<MallBean>?) {
                view?.showActivitysData(data!!)
            }

            override fun onError(errorCode: String?, message: String?) {
                ToastUtil.showLong(MyApplication.getInstance(),message)
            }

        })
    }

    override fun start() {}

    override fun destroy() {
        mData!!.destroy()
        mallRepository!!.destroy()
    }
}