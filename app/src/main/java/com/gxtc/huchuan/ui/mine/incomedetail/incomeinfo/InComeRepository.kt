package com.gxtc.huchuan.ui.mine.incomedetail.incomeinfo

import com.gxtc.commlibrary.data.BaseRepository
import com.gxtc.huchuan.bean.InComeAllCountBean
import com.gxtc.huchuan.bean.NewDistributeBean
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.MineApi
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by zzg on 2017/12/22.
 */
class InComeRepository :BaseRepository(),InComeSource {
    override fun getProfitDetailData(map: HashMap<String, String>, callBack: ApiCallBack<ArrayList<NewDistributeBean>>) {
        addSub(MineApi.getInstance().getProfitDetailData(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<ArrayList<NewDistributeBean>>>(callBack)))
    }

    override fun getData(map: HashMap<String, String>, callBack: ApiCallBack<ArrayList<NewDistributeBean>>) {
        addSub(MineApi.getInstance().listByInvitingSuccess(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<ArrayList<NewDistributeBean>>>(callBack)))
    }
}