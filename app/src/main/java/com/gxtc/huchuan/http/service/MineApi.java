package com.gxtc.huchuan.http.service;

import com.gxtc.huchuan.bean.AccountWaterBean;
import com.gxtc.huchuan.bean.ApplyInfoBean;
import com.gxtc.huchuan.bean.AuditBean;
import com.gxtc.huchuan.bean.BgPicBean;
import com.gxtc.huchuan.bean.BrowseHistoryBean;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.ChatInviteUrlBean;
import com.gxtc.huchuan.bean.ChooseClassifyBean;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.ClassHistoryBean;
import com.gxtc.huchuan.bean.ClassMyMessageBean;
import com.gxtc.huchuan.bean.CreateLiveTopicBean;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.bean.DistributionBean;
import com.gxtc.huchuan.bean.DistributionCountBean;
import com.gxtc.huchuan.bean.FreezeAccountBean;
import com.gxtc.huchuan.bean.HomePageChatInfo;
import com.gxtc.huchuan.bean.InComeAllCountBean;
import com.gxtc.huchuan.bean.LiveBgSettingBean;
import com.gxtc.huchuan.bean.LiveHeadTitleBean;
import com.gxtc.huchuan.bean.LiveManagerBean;
import com.gxtc.huchuan.bean.LiveRoomBean;
import com.gxtc.huchuan.bean.NewDistributeBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.PersonInfoBean;
import com.gxtc.huchuan.bean.PersonalDymicBean;
import com.gxtc.huchuan.bean.PersonalHomeDataBean;
import com.gxtc.huchuan.bean.PurchaseCircleRecordBean;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.bean.PurchaseSeriesAndTopicBean;
import com.gxtc.huchuan.bean.SeriesPageBean;
import com.gxtc.huchuan.bean.ShiledListBean;
import com.gxtc.huchuan.bean.TopicShareListBean;
import com.gxtc.huchuan.bean.UnifyClassBean;
import com.gxtc.huchuan.bean.UploadFileBean;
import com.gxtc.huchuan.bean.VisitorBean;
import com.gxtc.huchuan.bean.WithdrawRecordBean;
import com.gxtc.huchuan.bean.dao.User;
import com.gxtc.huchuan.bean.model.AppenGroudBean;
import com.gxtc.huchuan.bean.pay.AccountBean;
import com.gxtc.huchuan.data.SpecialBean;
import com.gxtc.huchuan.http.ApiBuild;
import com.gxtc.huchuan.http.ApiResponseBean;

import java.lang.reflect.Array;
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
 * Created by ALing on 2017/2/13 0013.
 *
 */

public class MineApi {
    private volatile static MineApi.Service instance;

    public interface Service {
        //注册绑定手机接口
        @POST("publish/member/bindingPhone.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<User>> getRegister(@FieldMap HashMap<String, String> map);

        //获取验证码接口
        @POST("publish/member/getCheckCode.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> getValidateCode(@Field("phone") String phone, @Field("type") String type);

        //登录接口
        @POST("publish/member/login.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<User>> getLogin(@Field("phone") String pohone, @Field("password") String pws);

        //第三方登录接口
        @POST("publish/member/thirdPartyLogin.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<User>> getThirdLogin(@FieldMap HashMap<String, String> map);

        //忘记密码接口
        @POST("publish/member/changePwd.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> changePws(@FieldMap HashMap<String, String> map);

        //会员信息获取接口
        @POST("publish/member/getUserMember.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<User>> getUserInfo(@Field("token") String token);

        //用户信息获取接口
        @POST("publish/member/getUserMemberByUserCode.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<User>> getUserMemberByUserCode(@Field("userCode") String userCode,@Field("token") String token);

        //会员信息绑定，修改会员信息
        @POST("publish/member/saveUserMember.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> editInfo(@FieldMap Map<String, String> map);


        //上传头像接口
        @POST("publish/member/uploadHeadPic.do")
        Observable<ApiResponseBean<User>> UploadAvatar(@Body RequestBody body);

        //上传文件
        @POST("publish/file/uploadFile.do")
        Observable<ApiResponseBean<UploadFileBean>> uploadFile(@Body RequestBody body);

        //保存文章      token , content 内容, digest 摘要, source 作者或者来源, title 标题, typeId 文章类型, cover 封面, sourceUrl 原链接  文档21  isDeploy  1草稿，0正式。默认0
        @POST("publish/news/saveNews.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> saveArticle(@FieldMap HashMap<String, String> map);

        //文章同步圈子 同步类型1、文章；2、课堂；3、文件；4系列课
        @POST("publish/group/appendToGroup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> appendToGroup(@FieldMap HashMap<String, String> map);

        @POST("publish/group/deleteToGroup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean> deleteToGroup(@FieldMap HashMap<String, String> map);

        //取消同步 同步类型1、文章；2、课堂；3、文件
        @POST("publish/group/cancelGroupAppendData.do")
        @FormUrlEncoded
        Observable<ApiResponseBean> cancelGroupAppendData(@FieldMap HashMap<String, String> map);

        @POST("publish/group/listAppendGroup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<AppenGroudBean>> listAppendGroup(@FieldMap HashMap<String, String> map);

        //227.	课堂上架/下架接口
        @POST("publish/chatInfo/repeal.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> repeal(@FieldMap HashMap<String, String> map);
        //	242.	讲师退款接口
        @POST("publish/chatInfo/refund.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> refund(@FieldMap HashMap<String, String> map);

        /**
         * 申请成为作者
         * <p>
         * token	是	用户token
         * name	是	作者名字
         * education	否	学历
         * wechat	否	微信
         * skill	否	擅长
         * work	否	工作
         * reel	否	作品
         */
        @POST("publish/authorInfo/saveAuthorInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> saveAuthorInfo(@FieldMap HashMap<String, String> map);

        /**
         * 获取作者申请信息接口
         */
        @POST("publish/authorInfo/getAuthorInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<AuditBean>> getAuthorInfo(@Field("token") String token);

        /**
         * 保存系列课分类
         * <p>
         * token	是	用户token
         * typeName	是	系类课分类名称
         * chatRoomId	是	直播间id
         * typeId      否  系列课id
         */
        @POST("publish/chatSeries/saveChatSeriesType.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ChooseClassifyBean>>> saveChatSeriesType(@FieldMap HashMap<String, String> map);

        /**
         * 删除系列课分类
         */
        @POST("publish/chatSeries/deleteChatSeriesType.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ChooseClassifyBean>>> deleteChatSeriesType(@FieldMap HashMap<String, String> map);

        /**
         * 删除系列课分类
         */
        @POST("publish/chatSeries/deleteChatSeries.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ChooseClassifyBean>>> deleteSeries(@FieldMap HashMap<String, String> map);


        /**
         * 273.	系列课同步圈子
         */
        @POST("publish/chatSeries/appendSeriesToGroup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> appendSeriesToGroup(@FieldMap HashMap<String, String> map);

        /**
         * 获取直播间课程的分类列表接口
         */
        @POST("publish/chatRoom/getChatRoomChatTypeSon.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<LiveHeadTitleBean.ChatTypeSonBean>>> getTopicTypeSon(@Field("chatRoomId") String chatRoomId);

        /**
         * 新建直播间课程接口
         */
        @POST("publish/chatInfo/saveChatInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CreateLiveTopicBean>> saveChatTopic(@FieldMap HashMap<String, String> map);

        /**
         * 新建直播系列课题接口
         */
        @POST("publish/chatSeries/saveChatSeries.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<SeriesPageBean>> saveChatSeries(@FieldMap HashMap<String, String> map);

        /**
         * 获取直播间系列课分类列表接口
         */
        @POST("publish/chatSeries/getChatSeriesTypeList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ChooseClassifyBean>>> getChatSeriesTypeList(@FieldMap HashMap<String, String> map);

        /**
         * 获取直播间管理设置信息接口
         */
        @POST("publish/chatRoom/getChatRoomInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<LiveRoomBean>> getLiveRoom(@FieldMap HashMap<String, String> map);

        /**
         * 保存直播间管理设置信息接口
         */
        @POST("publish/chatRoom/saveChatRoomSetting.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<LiveBgSettingBean>> saveChatRoomSetting(@FieldMap HashMap<String, String> map);

        /**
         * 获取图片列表接口
         */
        @POST("publish/pic/getPicList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<BgPicBean>>> getPicList(@FieldMap HashMap<String, String> map);

//        /**
//         * 获取购买的课程订单列表接口
//         *
//         * @param token
//         * @param start
//         * @return
//         */
//        @POST("publish/chatInfo/getChatOrderList.do")
//        @FormUrlEncoded
//        Observable<ApiResponseBean<List<PurchaseTopicRecordBean>>> getChatOrderList(@Field("token") String token,
//                                                                                    @Field("start") String start);
//
//        /**
//         * 获取购买的系列课订单列表接口
//         *
//         * @param token
//         * @param start
//         * @return
//         */
//        @POST("publish/chatSeries/getChatSeriesOrderList.do")
//        @FormUrlEncoded
//        Observable<ApiResponseBean<List<PurchaseSeriesRecordBean>>> getChatSeriesOrderList(@Field("token") String token,
//                                                                                           @Field("start") String start);

        /**
         * 198.获取课堂及系列课购买订单接口，合并了话题和系列课的订单列表
         */
        @POST("publish/chatSeries/getChatOrderList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<PurchaseSeriesAndTopicBean>>> getSeriedAndChatOrderList(@Field("token") String token,
                @Field("start") String start);

        /**
         * 100.获取个人交易订单集合接口
         */
        @POST("publish/tradeOrder/listByUser.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<PurchaseListBean>>> getTradeOrder(@Field("token") String token,
                                                                                  @Field("start") String start);
        /**
         *171.获取用户圈子的订单列表接口
         */
        @POST("publish/groupJoin/getOrderList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<PurchaseCircleRecordBean>>> getGroupOrder(@Field("token") String token,
                                                                                  @Field("start") String start);




        /**
         * 获取推送消息列表接口
         */
        @POST("publish/messagePush/getMessageRecordList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ClassMyMessageBean>>> getMessageRecordList(@Field("token") String token,
                                                                                   @Field("start") String start);

        @Deprecated
        @POST("/publish/chatInfo/getReportedChat.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ChatInfosBean>>> getChatInfoBuyedList(@Field("token") String token,
                                                                              @Field("start") String start);

        /**
         * 删除直播课程的观看记录接口
         */
        @POST("publish/chatInfo/deleteChatUserRecord.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<UnifyClassBean>>> deleteChatUserRecord(@Field("token") String token,
                                                                               @Field("chatInfoId") ArrayList<String> list);

        /**
         * 获取用户的课堂或圈子分销列表接口
         * @param token   用户token
         * @param start 起始页
         * @param type 显示类型。默认:1   1:课堂，2：圈子
         */
        @POST("publish/mMoney/getSaleList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<DistributionBean>>> getDistributionList(@Field("token") String token,
                                                                                @Field("start") String start,
                                                                                @Field("type") String type,
                                                                                @Field("dateType") String dateType);
        /**
         * 获取用户的课堂或圈子收益列表接口
         * @param token   用户token
         * @param start 起始页
         * @param type 显示类型。默认:1   1:课堂，2：圈子
         */
        @POST("publish/mMoney/getSaleIncomeList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<DistributionBean>>> getProfitList(@Field("token") String token,
                                                                                @Field("start") String start,
                                                                                @Field("type") String type,
                                                                                @Field("dateType") String dateType);

        /**
         * 获取用户的课堂或圈子收益列表接口(更换原来的接口)
         * @param token   用户token
         * @param start 起始页
         * @param type 显示类型。默认:1   1:课堂，2：圈子
         */
        @POST("publish/mMoney/listBusinessProfit.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<DistributionBean>>> getToatalProfitList(@Field("token") String token,
                                                                                @Field("start") String start,
                                                                                @Field("type") String type,
                                                                                @Field("dateType") String dateType);

        /**
         * 155.获取用户的课堂或圈子分销列表总数量接口
         * @param token   用户token
         * @param type 显示类型。默认:1   1:课堂，2：圈子
         */
        @POST("publish/mMoney/getSaleCount.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<DistributionCountBean>> getDistributionCount(@Field("token") String token,
                                                                                @Field("type") String type,
                                                                                @Field("dateType") String dateType);

        /**
         * 176.获取分销与收益统计（圈子和课程）接口
         */
        @POST("publish/mMoney/getIncomeStatistics.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<DistributionCountBean>> getIncomeStatistics(@FieldMap HashMap<String,String> map);


        /**
         * 286.	获取个人分销业务列表
         */
        @POST("publish/mMoney/listByDistributionBusiness.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<DistributionBean>>> getlistByDistributionBusiness(@FieldMap HashMap<String,String> map);

        /**
         * 288.	获取个人分销业务列表佣金统计
         */
        @POST("publish/mMoney/sumByDistributionBusiness.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<DistributionCountBean>> getlistByDistributionBusinessSum(@Field("token") String token,
                                                                             @Field("type") String type,
                                                                             @Field("dateType") String dateType);


        /**
         * 	获取个人课程或是圈子的收益
         */
        @POST("publish/mMoney/getCreateUserIncome.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<DistributionCountBean>> getCreateUserIncome(@Field("token") String token,
                                                                             @Field("type") String type,
                                                                             @Field("dateType") String dateType);

        /**
         * 175.获取总收益统计
         * @param token   用户token
         * @param dateType 日期类型。0：本日，1：本周，2：本月，3:本年，4：全部
         */
        @POST("publish/mMoney/getTotalIncomeStatistics.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<InComeAllCountBean>> getTotalIncomeStatistics(@Field("token") String token,
                                                                     @Field("dateType") String dateType);


        /**
         * 获取总收益统计（新接口）
         * @param token   用户token
         * @param dateType 日期类型。0：本日，1：本周，2：本月，3:本年，4：全部
         */
        @POST("publish/mMoney/getBusinessProfitDetailsByType.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<InComeAllCountBean>> getNewTotalIncomeStatistics(@Field("token") String token,
                                                                     @Field("dateType") String dateType);


        /**
         * 284.	获取话题分销的详细列表
         */
        @POST("publish/mMoney/listByInvitingSuccess.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ArrayList<NewDistributeBean>>> listByInvitingSuccess(@FieldMap HashMap<String,String> map);


        /**
         * 291.	用户收益详情列表
         */
        @POST("publish/mMoney/listBusinessProfitDetailsByTypeId.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ArrayList<NewDistributeBean>>> getProfitDetailData(@FieldMap HashMap<String,String> map);

        /**
         * 获取自己个人主页的主页列表
         */
        @POST("publish/userPage/homePageSelfList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<PersonalHomeDataBean>>> getHomePageSelfList(@Field("token") String token,
                                                                                    @Field("start") String start);

        /**
         * 获取用户个人主页的主页列表
         */
        @POST("publish/userPage/homePageUserList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<PersonalHomeDataBean>>> getHomePageUserList(@Field("userCode") String userCode,
                                                                                    @Field("start") String start);
        /**
         * 获取用户个人主页的动态列表
         */
        @POST("publish/userPage/homePageGroupInfoList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleHomeBean>>> getHomePageGroupInfoList(@Field("userCode") String userCode,
                                                                                   @Field("token") String token,
                                                                                   @Field("start") String start);

        /**
         * 获取自己写的新闻文章列表接口
         */

        @POST("publish/news/getSelfNews.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<NewsBean>>> getSelfNews(@Field("token") String token,
                                                                @Field("start") String start);

        /**
         * 获取用户写的新闻文章列表接口
         */

        @POST("publish/news/getUserNewsList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<NewsBean>>> getUserNewsList(@Field("userCode") String userCode,
                                                                    @Field("start") String start);

        /**
         * 获取自己个人主页的直播课程列表接口
         */

        @POST("publish/chatInfo/getSelfChatInfoList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<HomePageChatInfo>>> getSelfChatInfoList(@Field("token") String token,
                                                                                @Field("start") String start);

        /**
         * 获取用户个人主页的直播课程列表接口
         */

        @POST("publish/chatInfo/getUserChatInfoList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<HomePageChatInfo>>> getUserChatInfoList(@Field("userCode") String userCode,
                                                                                @Field("start") String start);

        /**
         * 获取自己个人主页的交易列表接口
         */

        @POST("publish/tradeInfo/listByUser.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<DealListBean>>> getSelfDealList(@Field("token") String token,
                                                                        @Field("start") String start);

        /**
         * 获取用户个人主页的交易列表接口
         */

        @POST("publish/tradeInfo/listByUserCode.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<DealListBean>>> getUserDealList(@Field("userCode") String userCode,
                                                                        @Field("start") String start);

        /**
         * 获取用户个人推荐列表接口
         */

        @POST("publish/userRecommend/getList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<PersonalHomeDataBean>>> getUserRecommendList(@Field("userCode") String userCode);

        /**
         * 获取自己圈子列表接口
         */

        @POST("publish/group/listByUser.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleBean>>> getCircleListByUser(@Field("token") String token,
                                                                          @Field("type") String type);

        /**
         * 获取用户圈子列表接口
         */

        @POST("publish/group/listByUserCode.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleBean>>> getCircleListByUserCode(@Field("userCode") String userCode,
                                                                              @Field("token") String token,
                                                                              @Field("type") int type,
                                                                              @Field("start") int start);


        /**
         * 获取邀请人(推荐用户，管理员，讲师)列表接口
         */

        @POST("publish/chatFind/getChatFindList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<LiveManagerBean>> getChatFindList(@FieldMap HashMap<String, String> map);

        /**
         * 意见反馈接口
         */

        @POST("publish/userFeedback/saveFeedback.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> saveFeedback(@FieldMap HashMap<String, String> map);


        /**
         * 分享榜
         * @param joinType   邀请类型。0:普通用户，1：管理员，2：讲师。
         * @param chatRoomId 直播间ID
         * @param chatInfoId 直播课程ID
         */
        @POST("publish/chatFind/getChatFindList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<TopicShareListBean>> getShareNotice(@Field("token") String token,
                                                                       @Field("joinType") String joinType,
                                                                       @Field("chatRoomId") String chatRoomId,
                                                                       @Field("chatInfoId") String chatInfoId,
                                                                       @Field("start") String start);


        /**
         * 137.	获取用户账户流水列表接口
         */
        @POST("publish/userStream/userStreamList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<AccountWaterBean>>> userStreamList(@Field("token") String token,
                                                                           @Field("start") String start,
                                                                           @Field("type") String type,
                                                                           @Field("added") String added,
                                                                           @Field("dateType") String dateType);


        /**
         * 137.	获取用户账户流水列表接口
         * type 0交易 8打赏 100全部
           dateType
           token
           start   pageSize
         */
        @POST("publish/mMoney/listBusinessIncomeRecordByType.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<AccountWaterBean>>> listBusinessIncomeRecordByType(@Field("token") String token,
                                                                           @Field("start") String start,
                                                                           @Field("type") String type,
                                                                           @Field("dateType") String dateType);

        @POST("publish/member/getNotSettleOrderList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<FreezeAccountBean>>> getNotSettleOrderList(@Field("token") String token,
                                                                                   @Field("start") String start);

        /**
         * 163.获取用户浏览记录接口
         */
        @POST("publish/userBrowse/getRecordList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<BrowseHistoryBean>>> getRecordList(@Field("token") String token,
                                                                           @Field("start") String start);
        /**
         * 169.删除用户浏览记录接口
         */
        @POST("publish/userBrowse/deleteUserBrowse.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<Object>>> deleteUserBrowse(@Field("token") String token,
                                                                    @Field("id") ArrayList<String> browseIds);

        /**
         * 170.获取用户访客接口
         *
         */
        @POST("publish/member/getUserBrowseList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<VisitorBean>>> getUserBrowseList(@Field("token") String token,
                                                                    @Field("start") String start);

        /**
         * 172.获取用户访客数量接口
         */
        @POST("publish/member/getUserBrowseCount.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<VisitorBean>> getUserBrowseCount(@Field("token") String token);

        /**
         * 43.个人主页动态信息列表接口
         *
         */
        @POST("publish/groupInfo/listDynamic.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<PersonalDymicBean>>> getListDynamic(@Field("token") String token,
                                                              @Field("start") String start);

        /**
         * 25.删除动态列表接口
         */
        @POST("publish/groupInfo/cleanDynamic.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> deleteDymicList(@Field("token") String token);

        /**
         * 189.获取直播管理员及主持人邀请链接接口
         */
        @POST("publish/chatRoom/getChatInviteUrl.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ChatInviteUrlBean>> getChatInviteUrl(@FieldMap HashMap<String,String> map);

        /**
         * 194.获取个人详细资料接口
         */
        @POST("publish/member/getUserInformation.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<PersonInfoBean>> getUserInformation(@FieldMap HashMap<String,String> map);

        /**
         * 193.保存用户联系人备注信息接口
         */
        @POST("publish/userLinkRemark/saveLinkRemark.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<PersonInfoBean>> saveLinkRemark(@FieldMap HashMap<String,String> map);

        /**
         * 57.	获取屏蔽用户列表接口
         */
        @POST("publish/userScren/list.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ShiledListBean>>> shiledList(@FieldMap HashMap<String,String> map);
        /**
         * 220.	会员设置好友权限接口
         */
        @POST("publish/member/setFriend.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> setFriend(@FieldMap HashMap<String,String> map);
        /**
         * 215.	设置用户权限接口
         */
        @POST("publish/userScren/joinList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> setUserScren(@FieldMap HashMap<String,String> map);
        /**
         * 209.	实名认证接口
         */
        @POST("publish/userRealAudit/save.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> userRealAudit(@FieldMap HashMap<String,String> map);
        /**
         * 210.	绑定用户账户接口
         */
        @POST("publish/expRecd/saveAccountSet.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> bindAccount(@FieldMap HashMap<String,String> map);
        /**
         * 212.	获取用户账户接口
         */
        @POST("publish/expRecd/getAccountSet.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<AccountBean>>> getAccountSet(@FieldMap HashMap<String,String> map);


        /**
         * 213获取申请信息接口
         */
        @POST("publish/userRealAudit/getApplyInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ApplyInfoBean>> ApplyInfo(@Field("token") String token);


        /**
         * 117. 验证手机验证码是否正确接口
         */
        @POST("publish/member/validateCheckCode.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> verifyCode(@Field("phone") String phone, @Field("checkCode") String checkCode);


        /**
         * 219.会员修改手机接口
         */
        @POST("publish/member/updatePhone.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> changePhone(@Field("token") String token, @Field("phone") String phone, @Field("password") String password);

        /**
         * 219.会员校验密码接口
         */
        @POST("publish/member/validatePassword.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> verifyPassword(@Field("token") String token, @Field("password") String password);

        /**
         *  会员修改密码接口
         */
        @POST("publish/member/updatePassword.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> changePassword(@Field("token") String token, @Field("oldPassword") String oldPwd, @Field("newPassword") String newPwd);

        /**
         * 222  绑定微信号接口
         */
        @POST("publish/member/bingWeChat.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> bindWx(@Field("token") String token, @Field("name") String name, @Field("wechat") String wechat);


        /**
         * 145 获取提现申请列表接口
         */
        @POST("publish/expRecd/getExpRecdList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<WithdrawRecordBean>>> getWithdrawList(@Field("token") String token, @Field("start") int start);


        @POST("api/v%s/newsSpecialSubscribe/listBySubscription.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<SpecialBean>>> getMySpecialList(@FieldMap HashMap<String, String> map);


    }


    public static MineApi.Service getInstance() {
        if (instance == null) {
            synchronized (MineApi.class) {
                if (instance == null)
                    instance = ApiBuild.getRetrofit().create(MineApi.Service.class);
            }
        }
        return instance;
    }
}
