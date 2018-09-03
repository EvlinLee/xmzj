package com.gxtc.huchuan.http.service;

import com.gxtc.huchuan.bean.FocusBean;
import com.gxtc.huchuan.bean.MergeMessageBean;
import com.gxtc.huchuan.bean.MessageBean;
import com.gxtc.huchuan.bean.PersonInfoBean;
import com.gxtc.huchuan.bean.SearchChatBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.http.ApiBuild;
import com.gxtc.huchuan.http.ApiResponseBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/8.
 */

public class MessageApi {

    private static MessageApi.Service instance;

    public interface Service{

        //搜索好友接口
        @POST("publish/search/searchFriend.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<SearchChatBean>>> searchFriends(@FieldMap Map<String,String> map);


        //搜索新的朋友接口
        @POST("publish/search/searchFans.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<FocusBean>>> searchNewFriends(@FieldMap Map<String,String> map);


        //获取群聊列表
        @POST("publish/userGroup/listGroup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<MessageBean>>> getGroupList(@FieldMap Map<String,String> map);


        //搜索群聊列表
        @POST("publish/userGroup/searchUserGroup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<MessageBean>>> searchGroup(@FieldMap Map<String,String> map);

        //搜索好友接口
        @POST("publish/userGroup/createGroup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<MessageBean>> createGroup(@FieldMap Map<String,String> map);

        //获取群聊成员列表
        @POST("publish/userGroup/listMember.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<MessageBean>>> getGroupMembers(@Field("groupId") String groupId, @Field("start") int start, @Field("pageSize") int pageSize, @Field("loagTime") long loagTime);


        //获取群聊成员角色
        @POST("publish/userGroup/getMemberByChat.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<MessageBean>> getGroupRole(@Field("token") String token, @Field("groupChatId") String groupChatId);


        @POST("publish/userGroup/listMember.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<FocusBean>>> getlistMember(@Field("start") int start, @Field("groupId") String groupChatId, @Field("loagTime") long loagTime);


        //280.	获取被申请好友数量
        @POST("publish/newsFollow/newFriends.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> getNewFriendsCount(@Field("token") String token);

        //251.	忽略好友申请接口
        @POST("publish/newsFollow/overlook.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> getOverlook(@Field("token") String token, @Field("userCode") String userCode);

        //251.	根据手机号获取用户信息接口
        @POST("publish/member/phoneByUser.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ArrayList<PersonInfoBean>>> phoneByUser(@Field("token") String token, @Field("phone") List<String> phone);

        //普通成员退出群聊
        @POST("publish/userGroup/quitGroup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> quitGroup(@Field("userCode") String userCode, @Field("groupChatId") String groupChatId);

        //群主退出群聊
        @POST("publish/userGroup/dissolve.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> dissolve(@Field("token") String token, @Field("groupChatId") String groupChatId);

        //修改群聊
        @POST("publish/userGroup/update.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> editGroupName(@FieldMap Map<String,String> map);

        //加入群聊
        @POST("publish/userGroup/joinGroup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> joinGroup(@FieldMap Map<String,String> map);

        //踢人出群聊
        @POST("publish/userGroup/clean.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> cleanGroup(@FieldMap Map<String,String> map);

        //搜索成员
        @POST("publish/userGroup/searchMember.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<MessageBean>>> searchMember(@FieldMap Map<String,String> map);

        //保存要合并消息内容接口
        @POST("publish/mergeMessage/saveMergeMessage.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<MergeMessageBean>> saveMergeMessage(@Field("token") String token , @Field("uidArray") String uidArray);

        //获取合并消息内容接口
        @POST("publish/mergeMessage/listMergeMessageDetailed.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<MergeMessageBean>>> getMergeMessage(@FieldMap HashMap<String, String> map);
    }

    public static MessageApi.Service getInstance() {
        if (instance == null) {
            synchronized (DealApi.class) {
                if (instance == null)
                    instance = ApiBuild.getRetrofit().create(MessageApi.Service.class);
            }
        }
        return instance;
    }

}
