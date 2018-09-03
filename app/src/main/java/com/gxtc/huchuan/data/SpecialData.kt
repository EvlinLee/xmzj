package com.gxtc.huchuan.data

import com.gxtc.commlibrary.data.BaseRepository
import com.gxtc.commlibrary.data.BaseSource
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.MineApi
import com.gxtc.huchuan.http.service.SpecialApi
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/5/12.
 */

interface SpecialSource : BaseSource {
    fun getMySpecialList(map: HashMap<String, String>, callback: ApiCallBack<MutableList<SpecialBean>>)
    fun getMySpecialDetail(id: String, callback: ApiCallBack<SpecialBean>)
    fun getSpecialType(map:Map<String,String>?, callback: ApiCallBack<MutableList<ArticleSpecialBean>>)
}


class SpecialRepository : BaseRepository(), SpecialSource {
    override fun getSpecialType(map: Map<String, String>?, callback: ApiCallBack<MutableList<ArticleSpecialBean>>) {

        addSub(SpecialApi.getInstance().listNewsByNewsSpecial(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<MutableList<ArticleSpecialBean>>>(callback)))
    }

    override fun getMySpecialDetail(id: String, callback: ApiCallBack<SpecialBean>) {
        addSub(SpecialApi.getInstance().getSpecialDetail(UserManager.getInstance().token,id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<SpecialBean>>(callback)))
    }

    //获取我订阅的专题
    override fun getMySpecialList(map: HashMap<String, String>, callback: ApiCallBack<MutableList<SpecialBean>>) {
        MineApi.getInstance()
                .getMySpecialList(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ApiObserver<ApiResponseBean<MutableList<SpecialBean>>>(callback))
                .apply { addSub(this) }
    }

}