package com.gxtc.huchuan.data

import com.gxtc.commlibrary.data.BaseRepository
import com.gxtc.huchuan.bean.WithdrawRecordBean
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.MineApi
import com.gxtc.huchuan.ui.mine.account.WithdrawRecordContract
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/8/31.
 */
class WithdrawRecordRepository: WithdrawRecordContract.Source, BaseRepository() {
    override fun getDataList(token: String, start: Int, callBack: ApiCallBack<MutableList<WithdrawRecordBean>>) {
        addSub(MineApi.getInstance()
                .getWithdrawList(token,start)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<MutableList<WithdrawRecordBean>>>(callBack)))
    }
}