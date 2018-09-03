package com.gxtc.huchuan.data

import com.gxtc.commlibrary.data.BaseRepository
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.bean.*
import com.gxtc.huchuan.http.ApiCallBack
import com.gxtc.huchuan.http.ApiObserver
import com.gxtc.huchuan.http.ApiResponseBean
import com.gxtc.huchuan.http.service.AllApi
import com.gxtc.huchuan.http.service.MessageApi
import kotlinx.android.synthetic.main.activity_mall_search.*
import rx.Observable
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/7.
 */
class MessageRepository : BaseRepository(),MessageSource{

    override fun getGroupRole(token: String, groupChatId: String, callBack: ApiCallBack<MessageBean>) {
        addSub(
            MessageApi.getInstance()
                    .getGroupRole(token,groupChatId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(ApiObserver<ApiResponseBean<MessageBean>>(callBack))
        )
    }

    override fun getGroupMembers(goupId: String, start: Int, pageSize: Int, loadTime: String ,callBack: ApiCallBack<MutableList<MessageBean>>) {
        addSub(
            MessageApi.getInstance()
                    .getGroupMembers(goupId,start,pageSize, loadTime.toLong())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(ApiObserver<ApiResponseBean<MutableList<MessageBean>>>(callBack))
        )
    }

    override fun createGroup(map: Map<String, String>, callBack: ApiCallBack<MessageBean>) {
        addSub(
            MessageApi.getInstance()
                    .createGroup(map)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(ApiObserver<ApiResponseBean<MessageBean>>(callBack))
        )
    }

    override fun searchGroup(map: Map<String, String>, callBack: ApiCallBack<MutableList<MessageBean>>) {
        addSub(
            MessageApi.getInstance()
                    .searchGroup(map)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(ApiObserver<ApiResponseBean<MutableList<MessageBean>>>(callBack))
        )
    }

    override fun getGroups(map: Map<String, String>, callBack: ApiCallBack<MutableList<MessageBean>>) {
        addSub(
                MessageApi.getInstance()
                        .getGroupList(map)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(ApiObserver<ApiResponseBean<MutableList<MessageBean>>>(callBack))
        )
    }

    override fun searchNewFriends(map: Map<String, String>, callBack: ApiCallBack<MutableList<FocusBean>>) {
        addSub(
                MessageApi.getInstance()
                        .searchNewFriends(map)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(ApiObserver<ApiResponseBean<MutableList<FocusBean>>>(callBack))
        )
    }

    override fun getNewFriends(userCode: String?, type: String, start: Int,groupChatId:String, callBack: ApiCallBack<MutableList<FocusBean>>) {
        addSub(
                AllApi.getInstance()
                        .getFocus(userCode,type,start.toString(),groupChatId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(ApiObserver<ApiResponseBean<MutableList<FocusBean>>>(callBack))
        )
    }

    override fun searchFriends(map: Map<String, String>,callBack: ApiCallBack<MutableList<SearchChatBean>>) {
        addSub(
            MessageApi.getInstance()
                    .searchFriends(map)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(ApiObserver<ApiResponseBean<MutableList<SearchChatBean>>>(callBack))
        )
    }


    override fun getFriendsByContacts(token: String, phones: MutableList<String>, callBack: ApiCallBack<MutableList<PersonInfoBean>>) {
        addSub(MessageApi.getInstance()
                .phoneByUser(token, phones)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ApiObserver<ApiResponseBean<ArrayList<PersonInfoBean>>>(callBack)))
    }


    override fun getNewFriendsCount(token: String, callBack: ApiCallBack<Any>) {
        addSub(
                MessageApi.getInstance()
                        .getNewFriendsCount(token)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(ApiObserver<ApiResponseBean<Any>>(callBack)))
    }

    override fun saveMergeMessage(token: String, uidArray: String, callBack: ApiCallBack<MergeMessageBean>) {
        addSub(
                MessageApi.getInstance()
                        .saveMergeMessage(token, uidArray)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(ApiObserver<ApiResponseBean<MergeMessageBean>>(callBack)))
    }
}