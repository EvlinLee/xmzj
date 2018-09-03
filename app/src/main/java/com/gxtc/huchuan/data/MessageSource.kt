package com.gxtc.huchuan.data

import com.gxtc.commlibrary.data.BaseSource
import com.gxtc.huchuan.bean.*
import com.gxtc.huchuan.http.ApiCallBack
import retrofit2.http.Field
import java.util.*

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/7.
 */
interface MessageSource : BaseSource {

    fun getGroupRole(token: String, groupChatId: String, callBack: ApiCallBack<MessageBean>)

    fun getGroupMembers(goupId: String, start: Int, pageSize: Int, loadTime: String, callBack: ApiCallBack<MutableList<MessageBean>>)

    fun createGroup(map: Map<String, String>, callBack: ApiCallBack<MessageBean>)

    fun searchGroup(map: Map<String, String>, callBack: ApiCallBack<MutableList<MessageBean>>)

    fun searchFriends(map: Map<String, String>, callBack: ApiCallBack<MutableList<SearchChatBean>>)

    fun searchNewFriends(map: Map<String, String>, callBack: ApiCallBack<MutableList<FocusBean>>)

    fun getGroups(map: Map<String, String>, callBack: ApiCallBack<MutableList<MessageBean>>)

    fun getNewFriends(userCode: String?, type: String, start: Int,groupChatId: String, callBack: ApiCallBack<MutableList<FocusBean>>)

    fun getFriendsByContacts(token: String, phones: MutableList<String>, callBack: ApiCallBack<MutableList<PersonInfoBean>>)

    fun getNewFriendsCount(token:String,callBack: ApiCallBack<Any>);

    fun saveMergeMessage(token:String, uidArray:String, callBack: ApiCallBack<MergeMessageBean>);

}