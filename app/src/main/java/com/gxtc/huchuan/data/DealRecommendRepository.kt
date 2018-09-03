package com.gxtc.huchuan.data

import com.gxtc.commlibrary.data.BaseRepository
import com.gxtc.commlibrary.utils.ACache
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.bean.DealData
import com.gxtc.huchuan.bean.DealListBean
import com.gxtc.huchuan.bean.GoodsDetailedBean
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.DealApi
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by zzg on 2017/10/28.
 */
class DealRecommendRepository : BaseRepository(),DealRecomemndSource {

    override fun getDealRecomemndData(map: HashMap<String, String>, mCallBack: ApiCallBack<DealData>) {
        var mSub = DealApi.getInstance()
                .getDealData(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<DealData>>(mCallBack))

        addSub(mSub)
    }
}