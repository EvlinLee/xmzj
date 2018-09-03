package com.gxtc.huchuan.data

import com.gxtc.commlibrary.data.BaseRepository
import com.gxtc.huchuan.bean.CategoryBean
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.MallApi
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Administrator on 2017/10/24.
 */
class MallTagRepository : BaseRepository(),MallTagSource {

    override fun getTagDatas(map: HashMap<String, String>, mCallBack: ApiCallBack<ArrayList<CategoryBean>>) {
        addSub(MallApi.getInstance().merchandiseList(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<ArrayList<CategoryBean>>>(mCallBack)))
    }
}