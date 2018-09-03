package com.gxtc.huchuan.data

import com.gxtc.commlibrary.data.BaseRepository
import com.gxtc.huchuan.bean.MallBean
import com.gxtc.huchuan.bean.OrderBean
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.MallApi
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by zzg on 2017/10/24.
 */
class MallOrderStatusRepository : BaseRepository(),MallOrderSource{
    override fun deleteOrder(token: String, orderNo: String, mCallBack: ApiCallBack<Any>) {
        addSub(MallApi.getInstance().deleteOrder(token,orderNo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<Any>>(mCallBack)))
    }

    override fun confirmOrder(token: String, orderNo: String, mCallBack: ApiCallBack<Any>) {
        addSub(MallApi.getInstance().confirmOrder(token,orderNo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<Any>>(mCallBack)))
    }

    override fun getOrderList(map: HashMap<String, String>, mCallBack: ApiCallBack<ArrayList<OrderBean>>) {
        addSub(MallApi.getInstance().getAllOrderList(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<ArrayList<OrderBean>>>(mCallBack)))
    }
}