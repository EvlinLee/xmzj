package com.gxtc.huchuan.http.service;

import com.gxtc.huchuan.bean.AuditBean;
import com.gxtc.huchuan.bean.ChatInFoStatusBean;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ChatJoinBean;
import com.gxtc.huchuan.bean.ClassHeadOrderBean;
import com.gxtc.huchuan.bean.ClassHotBean;
import com.gxtc.huchuan.bean.ClassLike;
import com.gxtc.huchuan.bean.ClassOrderBean;
import com.gxtc.huchuan.bean.ClassOrderHeadBean;
import com.gxtc.huchuan.bean.CreateLiveBean;
import com.gxtc.huchuan.bean.FollowLecturerBean;
import com.gxtc.huchuan.bean.LiveHeadTitleBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.bean.SeriesSelBean;
import com.gxtc.huchuan.bean.SeriseCountBean;
import com.gxtc.huchuan.bean.ShareImgBean;
import com.gxtc.huchuan.bean.SignUpMemberBean;
import com.gxtc.huchuan.bean.TopicShareListBean;
import com.gxtc.huchuan.bean.UnifyClassBean;
import com.gxtc.huchuan.bean.UploadPPTFileBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.http.ApiBuild;
import com.gxtc.huchuan.http.ApiResponseBean;
import com.gxtc.huchuan.im.bean.RemoteMessageBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Gubr on 2017/3/6.
 */

public class LiveApi {
    private volatile static LiveApi.Service instance;


    public interface Service {
        /**
         * 新建直播间
         * <p>
         * token	是	用户token
         * chatTypeId	是	直播类型id
         * property	是	直播认证类型， 认证身份。0，1，2
         * 0:讲师，
         * 1：机构，
         * 2：听众
         * roomname	是	直播间名称
         * bakpic	否	直播间背景图片url
         * headpic	否	直播间头像
         * introduce	否	直播间介绍
         *
         * @param map
         * @return
         */
        @POST("publish/chatRoom/saveChatRoom.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CreateLiveBean>> saveChatRoom(
                @FieldMap HashMap<String, String> map);



        @POST("publish/chatInfo/getChatInfoList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ChatInfosBean>>> getChatInfoList(
               @Field("token")String token,@Field("chatRoom")String chatRoom,@Field("chatSeries")String chatSeries,@Field("start")Integer start);

        /**
         * 285 接口用于获取对应系列课下子课程列表数据
         * @param token
         * @param start
         * @return
         */
        @POST("publish/chatSeries/listChatSeriesChildren.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ChatInfosBean>>> listChatSeriesChildren(
               @Field("token")String token,@Field("chatSeriesId")String ChatSeriesId,@Field("start")Integer start);






        /**
         * 新建直播间课程接口
         * <p>
         * token	是	用户token
         * chatRoom	是	直播间ID
         * subtitle	是	课程主题
         * starttime	是	直播课程开始时间，时间戳
         * chatway	是	直播形式。0和1
         * 0：讲座，
         * 1：幻灯片
         * chattype	是	直播类型 。0，1，2
         * 0：公开，
         * 1：加密，
         * 2，收费
         * password	否	课程加密密码。！！！当chattype==1时必填
         * fee	否	课程收费金额。         ！！！当chattype==2时必填
         * chatSeries	否	系列课程id
         * chatTypeSonId	是	直播子类型ID
         *
         * @param map
         * @return
         */
        @POST("publish/chatInfo/saveChatInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> saveChatInfo(@FieldMap HashMap<String, String> map);


        /**
         * 129.	获取直播课程邀请图片接口
         *
         * @param userCode
         * @param chatInfoId  课程ID
         * @param subjectType 主题图片类型。1至4。默认为主题1
         * @return
         */
        @POST("publish/chatInfo/getChatInfoInvitation.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ShareImgBean>> getChatInfoInvitation(
                @Field("userCode") String userCode, @Field("chatInfoId") String chatInfoId,
                @Field("subjectType") String subjectType,@Field("chatType") String chatType);


        //获取热门课程列表
        @POST("publish/chatInfo/getChatInfoAdvanceList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ChatInfosBean>>> getChatInfoAdvanceList(
                @Field("token") String token, @Field("start") String start,
                @Field("pageSize") String pageSize);


        @POST("publish/chatInfo/getChatInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ChatInfosBean>> getChatInfosBean(@Field("token") String token,
                @Field("chatInfoId") String chatInfoId);


        /**
         * 181.	删除直播间话题接口
         * @param token
         * @param chatInfoId
         * @return
         */
        @POST("publish/chatInfo/delChatInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> delChatInfo(@Field("token") String token,
                @Field("chatInfoId") String chatInfoId);


        /**
         * 197.	获取直播间系列课选择列表接口
         * @param chatRoomId
         * @return
         */
        @POST("publish/chatSeries/getChatSeriesSelList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<SeriesSelBean>>> getChatSeriesSelList(@Field("chatRoomId") String chatRoomId);


        /**
         * 282.	获取合并的话题与系列课接口
         * @param chatRoomId
         * @return
         */
        @POST("publish/chatRoom/getChatRoomByChatInfoAndChatSeries.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ChatInfosBean>>> getChatRoomByChatInfoAndChatSeries(@Field("chatRoomId") String chatRoomId, @Field("token") String token, @Field("start") String start);



        @Deprecated
        @POST("publish/chatInfo/getChatInfoWatchingList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ArrayList<ChatInfosBean>>> getSysRecommendChatInfoList(@Field("token") String token,
                @Field("start") Integer start);

        /**
         * 关注接口 如果当前是ture  再请求一次就是flase
         *
         * @param token
         * @param followType
         * @param bizId
         * @return
         */
        @POST("publish/newsFollow/userFollow.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> setUserFollow(@Field("token") String token,
                @Field("followType") String followType, @Field("bizId") String bizId);


        @POST("publish/userRefund/getSellerRefundList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<PurchaseListBean>>> getSellerRefundList(@FieldMap HashMap<String, String> map);


        /**
         * 获取直播间类型
         *
         * @return
         */
        @POST("publish/chatRoom/getChatType.do")
        Observable<ApiResponseBean<List<LiveHeadTitleBean>>> getChatType();


        /**
         * 获取直播间主页信息
         *
         * @param chatRoomId
         * @param token
         * @return
         */
        @POST("publish/chatRoom/getChatRoomInfos.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<LiveRoomBean>> getLiveRoom(
                @Field("chatRoomId") String chatRoomId, @Field("token") String token);

        /**
         * 申请成为主播
         * <p>
         * token	是	用户token
         * name	是	作者名字
         * education	否	学历
         * wechat	否	微信
         * skill	否	擅长
         * work	否	工作
         * reel	否	作品
         *
         * @param map
         * @return
         */
        @POST("publish/anchorInfo/saveAnchorInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> saveAnchorInfo(@FieldMap HashMap<String, String> map);


        /**
         * 获取主播申请信息接口
         *
         * @param token
         * @return
         */
        @POST("publish/anchorInfo/getAnchorInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<AuditBean>> getAnchorInfo(@Field("token") String token);




        @POST("publish/advert/getSlideList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ChatInfosBean>>> getSlideList(@Field("token") String token,@Field("type")String type);


        /**
         * 热门课程数据
         *
         * @param token 可以为null
         * @return
         */
        @POST("publish/chatInfo/getChatInfoRankingList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ArrayList<ChatInfosBean>>> getChatRoomHomePageInfo(@Field("token") String token,@Field("start") int start, @Field("type") String type);

        /**
         * 281.	获取话题/系列课报名人数列表 使用chat/listJoinMember.do替代
         *
         * @param token 可以为null
         * @return
         */
        @POST("publish/chatInfo/listByChatOrSeriesInBuy.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ArrayList<SeriseCountBean>>> getSeriseCount(@Field("token") String token, @Field("id") String id,@Field("start") int start, @Field("type") String type, @Field("searchKey") String searchKey);

        /**
         * 281.	获取话题/系列课报名人数列表和课程管理成员列表统一了
         *
         * @param token 可以为null
         * @return
         */
        @POST("publish/chat/listJoinMember.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ArrayList<ChatJoinBean.MemberBean>>> getlistJoinMember(@Field("token") String token, @Field("userType") String userType,@Field("start") int start, @Field("type") String type, @Field("chatId") String chatId, @Field("searchKey") String searchKey);

        /**
         * 281.	获取话题/系列课报名人数列表和课程管理成员列表统一了
         *
         * @return
         */
        @POST("publish/chat/listJoinMember.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ArrayList<ChatJoinBean.MemberBean>>> getlistJoinMember(@FieldMap HashMap<String, String> map);

        /**
         * 	修改话题/系列课的成员身份
         *
         *  chatId 课程Id
         *  type  课程类型 1课程 2系列课
         *  userCode 目标用户新媒号
         *  token 操作用户token
         *  userType 要修改的类型 1管理员， 2讲师， 0普通成员；
         */
        @POST("publish/chatJoinMember/updateJoinMemberJoinType.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> updateJoinMemberJoinType(@FieldMap HashMap<String, String> map);


        /**
         *
         *   拉黑/解除拉黑操作  禁言/解除禁言
         *  chatId 课程id
         *  chatType 1课程 2系列课
         *  userCode 目标用户新媒号
         *  token 当前操作人token
         *  type 1拉黑/解除拉黑操作  2禁言/解除禁言操作
         *  state 0解除 1拉黑/禁言
         */
        @POST("publish/chatJoinMember/doJoinMemberBlacklistOrProhibitSpeaking.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> doJoinMemberBlacklistOrProhibitSpeaking(@FieldMap HashMap<String, String> map);

        /**
         * 获取 系列课主页信息接口
         *
         * @param token
         * @param chatSeriesId 系列课id
         * @return
         */
        @POST("publish/chatSeries/getChatSeriesInfos.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<SeriesPageBean>> getChatSeriesInfo(@Field("token") String token, @Field("chatSeriesId") String chatSeriesId);


        /**
         * 获取 系列课主页信息接口
         *
         * @param token
         * @param chatSeriesId 系列课id
         * @return
         */
        @POST("publish/chatSeries/saveFreeChatSeriesBuy.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> saveFreeChatSeriesBuy(@Field("token") String token,
                @Field("chatSeriesId") String chatSeriesId);


        /**
         * 48.获取看过的直播间课程记录列表接口
         *
         * @param token 是	用户token
         * @param start 是	分页起始数量，从0开始
         * @return
         */
        @POST("publish/chatInfo/getChatUserRecordList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ChatInfosBean>>> getChatUserRecordList(
                @Field("token") String token, @Field("start") String start);


        /**
         * 46.	结束直播间课程
         *
         * @param token
         * @return
         */
        @POST("publish/chatInfo/stopChatInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<Object>>> stopChatInfo(@Field("token") String token,
                @Field("chatInfoId") String chatInfoId);

        /**
         * 41.	保存直播间介绍页信息接口
         * token	            是	用户token
         * id	                是	直播间课程ID
         * subtitle	            是	直播间课程主题
         * password	            否	课程密码
         * fee	                否	课程价格
         * facePic	            否	课程封面
         * mainSpeaker	        否	课程主讲人
         * speakerIntroduce	    否	主讲人介绍
         * desc	                否	课程介绍描述
         * showSignup	        否	是否显示报名用户统计。0，1
         * 0：显示，1：不显示
         * showShare	        否	是否显示分享榜。0，1
         * 0：显示，1：不显示
         *
         * @param map
         * @return
         */
        @POST("publish/chatInfo/saveChatInfoIntroduction.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> saveChatInfoIntroduction(
                @FieldMap HashMap<String, String> map);

        /**
         * 77.	直播间课程报名接口
         *
         * @param token
         * @param chatInfoId
         * @return
         */
        @POST("publish/chatInfo/saveChatSignup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> saveChatSignup(@Field("token") String token,
                @Field("chatInfoId") String chatInfoId);


        /**
         * 84.获取直播间列表接口
         * token	否	用户token
         * start	是	分页起始数量，从0开始
         * pageSize	否	每页数量，为空默认按系统的每页数量查询
         *
         * @param map
         * @return
         */
        @POST("publish/chatRoom/getChatRoomList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<LiveRoomBean>>> getChatRoomList(
                @FieldMap HashMap<String, String> map);


        /**
         * 162.获取关注的直播间接口
         */
        @POST("publish/anchorInfo/listMyFriendIsAnchor.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<FollowLecturerBean>>> getFollowChatRoomList(
                @FieldMap HashMap<String, String> map);


        /**
         * 90.获取直播课程排行列表接口
         * <p>
         * start	是	分页起始数量
         * pageSize	否	每页数量，为空时使用系统默认数量
         * token	否	用户token
         * type	否	0:热门课程，1：免费课程，2：精品课程
         *
         * @param map
         * @return
         */
        @Deprecated
        @POST("publish/chatInfo/getChatInfoRankingList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ChatInfosBean>>> getChatInfoRankingList(
                @FieldMap HashMap<String, String> map);

        /**
         * 80.	获取直播课程报名列表接口
         * token	是	用户token
         * chatInfoId	是	直播间课程ID
         * start	是	分页起始数量，从0开始
         * name	否	用户名字搜索，可以为空
         *
         * @param map
         * @return
         */
        @POST("publish/chatInfo/getChatSignupList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<SignUpMemberBean>>> getChatSignupList(
                @FieldMap HashMap<String, String> map);

        /**
         * 151.	获取课程的禁言等状态接口
         *
         * @param map
         * @return
         */
        @POST("publish/chatInfo/getChatInfoStatus.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ChatInFoStatusBean>> getChatInfoStatus(
                @FieldMap HashMap<String, String> map);


        /**
         * 110.	IM添加消息接口
         * token	是	用户token
         * classname	是	消息定义名称
         * content	是	IM内容
         * targetType	是	IM目标消息类型
         * targetId	是	IM对应的目标类型id
         *
         * @param map
         * @return
         */
        @POST("publish/imRecord/addMessage.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> addMessage(@FieldMap HashMap<String, String> map);

        /**
         * 156.删除历史聊天消息接口
         *
         * @param token
         * @param msgId
         * @return
         */
        @POST("publish/imRecord/delMessage.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> delMessage(@Field("token") String token,
                @Field("msgId") String msgId);


        @POST("publish/imRecord/getMessageRecordList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<RemoteMessageBean>>> getMessageRecordList(@FieldMap HashMap<String, String> map);


        /**
         * 上传im 相关的文件
         *
         * @return
         */
        @POST("publish/file/uploadIMFile.do")
        Observable<ApiResponseBean<List<String>>> uploadIMFile(@Body RequestBody body);


        /**
         * 上传课件
         *
         * @param body
         * @return
         */
        @POST("publish/chatInfo/saveChatInfoSlide.do")
        Observable<ApiResponseBean<List<UploadPPTFileBean>>> uploadCourseware(@Body RequestBody body);


        /**
         * 获取直播课件
         *
         * @param token
         * @param chatInfoId 直播间id
         * @return
         */
        @POST("publish/chatInfo/getChatInfoSlideList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<UploadPPTFileBean>>> getChatInfoSlideList(
                @Field("token") String token, @Field("chatInfoId") String chatInfoId);


        /**
         * 获取用户头像跟名字
         */
        @POST("publish/member/getSimpleUserMember.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<User>> getUserInfo(@Field("token") String token ,@Field("userCode") String userCode);

        /**
         * 获取直播话题的主持人接口(该接口替代132接口)  publish/chatInfo/getChatJoinList.do
         */
        @POST("publish/chatInfo/getChatUserList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ChatJoinBean>> getChatJoinList(
                @FieldMap HashMap<String, String> map);

        /**
         * 获取直播话题的主持人接口  publish/chatInfo/getChatJoinList.do

        @POST("publish/chatInfo/getChatJoinList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ChatJoinBean>> getChatJoinList(
                @FieldMap HashMap<String, String> map);
         */
        /**
         * 获取课程订单  publish/chat/listChatOrderDataByType.do type 1.话题；2.系列课
         */
        @POST("publish/chat/listChatOrderDataByType.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ClassOrderBean>>> getlistChatSeriesOrderData(
                @FieldMap HashMap<String, String> map);

        /**
         * 获取课程订单头部信息 publish/chat/listChatOrderHeadByType.do
         */
        @POST("publish/chat/listChatOrderHeadByType.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ClassOrderHeadBean>>> getHeadChatSeriesOrderData(
                @FieldMap HashMap<String, String> map);


        /**
         * 获取  课程管理 -> 课程订单   头部信息 publish/chat/listChatOrderHeadByType.do
         *
         * token       操作用户token
         * chatRoomId  直播间id
         */
        @POST("publish/chat/listAllIncomeChatHead.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ClassOrderHeadBean>>> getHeadLiveManageOrderData(
                @FieldMap HashMap<String, String> map);

        /**
         * 获取  课程管理 -> 课程订单   列表 publish/chat/listChatOrderHeadByType.do
         * * token       操作用户token
         * chatRoomId  直播间id
         * start
         * pageSize
         */
        @POST("publish/chat/listAllIncomeChatByUser.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ClassOrderBean>>> getLiveManageOrderList(
                @FieldMap HashMap<String, String> map);


        /**
         * 275. 根据分类获取课堂列表  取代254接口
         */
        @POST("publish/chatInfo/listByChatTypes.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<UnifyClassBean>>> getClassroomByType(@Field("chatInfoTypeId") String typeId, @Field("start") int start, @Field("clickType") String clickType);


        /**
         * 276.新的课堂首页接口  取代 86接口
         */
        @POST("publish/chatInfo/getChatWatchingList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<UnifyClassBean>>> getClassroomIndexRecomment(@Field("start") int start);


        /**
         * 277	获取用户已报名的话题列表接口 取代 153接口
         */
        @POST("publish/chatInfo/getChatInfoBuyedLists.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<UnifyClassBean>>> getBuyedClassroom(@Field("token") String token, @Field("start") int start,@Field("clickType") String  clickType);


        /**
         * 278	免费课程、人气课程、精品课程接口 取代 90接口
         */
        @POST("publish/chatInfo/getChatRankingList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<UnifyClassBean>>> getHotClassroom(@FieldMap Map<String, String> map);


        //热门直播
        @POST("publish/chatRoom/listHotChatRoom.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ClassHotBean>>> getClassHotroom(@FieldMap Map<String, String> map);

        //猜你喜欢
        @POST("publish/chat/listGuessYouLikeChat.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ClassLike>>> getClassLike(@FieldMap Map<String, String> map);


        /**
         * 获取系列课分享达人榜
         */
        @POST("publish/chatFind/listSeriesChatFindUser.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<TopicShareListBean>> getSeriesShareData(@Field("token") String token, @Field("start") int start,@Field("seriesId") String  id);

    }

    public static LiveApi.Service getInstance() {
        if (instance == null) {
            synchronized (LiveApi.class) {
                if (instance == null)
                    instance = ApiBuild.getRetrofit().create(LiveApi.Service.class);
            }
        }
        return instance;
    }
}
