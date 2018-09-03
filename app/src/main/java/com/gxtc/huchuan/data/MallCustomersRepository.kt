package com.gxtc.huchuan.data

import com.gxtc.commlibrary.data.BaseRepository
import com.gxtc.huchuan.bean.AllStatusSumsBean
import com.gxtc.huchuan.bean.CoustomMerBean
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.MallApi
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by zzg on 2017/10/24.
 */
class MallCustomersRepository :BaseRepository(), MallCustomerdSource {

    override fun getCustermersList(type: String,rand: String, mCallback: ApiCallBack<ArrayList<CoustomMerBean>>) {
        addSub(MallApi.getInstance().getIMServiceList(type,rand)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<ArrayList<CoustomMerBean>>>(mCallback)))
    }

    override fun getAllStatusSums(token: String, mCallback: ApiCallBack<java.util.ArrayList<AllStatusSumsBean>>) {
        addSub(MallApi.getInstance()
                .getAllStatusSums(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<ArrayList<AllStatusSumsBean>>>(mCallback)))
    }
}