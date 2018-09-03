package com.gxtc.huchuan.ui.mine.circle.circlesign

import com.gxtc.commlibrary.data.BaseRepository
import com.gxtc.huchuan.bean.CircleInfoBean
import com.gxtc.huchuan.bean.CircleSignBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.CircleApi
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by zzg on 2017/12/20.
 */
class CircleResposetory : BaseRepository(),CircleSignSource{

    override fun getData(groupId: String, start: String, joinType: String, userName: String, loadTime:Long,callBack: ApiCallBack<ArrayList<CircleSignBean>>) {
        val token = UserManager.getInstance().token
        addSub(CircleApi.getInstance().getPersonByFeeOrFree(groupId.toInt(),start.toInt(),joinType,userName,loadTime,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<ArrayList<CircleSignBean>>>(callBack)))
    }
}