package com.gxtc.huchuan.http.service;

import com.gxtc.huchuan.bean.ApplyForBean;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.bean.CircleErweimaBean;
import com.gxtc.huchuan.bean.CircleFileBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CircleInfoBean;
import com.gxtc.huchuan.bean.CircleMemberBean;
import com.gxtc.huchuan.bean.CircleRankBean;
import com.gxtc.huchuan.bean.CircleShareInviteBean;
import com.gxtc.huchuan.bean.CircleSignBean;
import com.gxtc.huchuan.bean.ClassAndSeriseBean;
import com.gxtc.huchuan.bean.FolderBean;
import com.gxtc.huchuan.bean.GroupRuleBean;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.bean.RecentBean;
import com.gxtc.huchuan.bean.RedPacketBean;
import com.gxtc.huchuan.bean.SearchChatBean;
import com.gxtc.huchuan.bean.StatisticBean;
import com.gxtc.huchuan.bean.StatisticDetailBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.bean.VisitorBean;
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


public class CircleApi {
    private static CircleApi.Service instance;


    public interface Service {
        //获取发现圈子列表2
        @POST("publish/group/list.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleBean>>> getFindCirCle(@Field("token") String token,
                                                                    @Field("start") int start,
                                                                    @Field("isFee") String isFee,
                                                                    @Field("orderType") String orderType,
                                                                    @Field("keyWord") String keyWord,
                                                                    @Field("typeId") Integer typeId,
                                                                    @Field("pageSize") int pageSize);

        //34.删除圈子公告详情接口
        @POST("publish/groupNotice/delete.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> deleteNotice(@Field("id") Integer id);

        //.89.	获取圈子分类接口
        @POST("publish/group/getGroupTypes.do")
        Observable<ApiResponseBean<List<CircleBean>>> groupType();

        //删除圈子文件接口
        @POST("publish/groupFile/delete.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> deleteCircleFile(
                @Field("token") String token, @Field("groupId") int groupId,
                @Field("fileId") String fileId);


        //58.	创建（修改）文件夹接口
        @POST("publish/groupFile/createFolder.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<FolderBean>> createFolder(
                @Field("token") String token, @Field("groupId") int groupId,
                @Field("folderName") String folderName, @Field("folderId") String folderId);

        //59.	获取文件夹列表接口
        @POST("publish/groupFile/listFolder.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<FolderBean>>> listFolder(
                @Field("token") String token,
                @Field("groupId") int groupId,
                @Field("start") int start, @Field("pageSize") Integer pageSize);

        //60.	获取文件夹的文件列表接口
        @POST("publish/groupFile/listFileByFolder.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleFileBean>>> listFileByFolder(
                @Field("groupId") int groupId,
                @Field("token") String token,
                @Field("folderId") int folderId,
                @Field("start") int start, @Field("pageSize") String pageSize);

        //61.	删除文件夹接口
        @POST("publish/groupFile/deleteFolder.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> deleteFolder(
                @Field("token") String token,
                @Field("groupId") int groupId, @Field("folderId") int folderId);

        //62.	修改文件名称接口
        @POST("publish/groupFile/update.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> update(
                @Field("token") String token,
                @Field("fileId") int fileId, @Field("fileName") String fileName, @Field("folderId") Integer folderId);

        //66.	圈子文件搜索接口
        @POST("publish/groupFile/query.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleFileBean>>> fileQuery(
                @Field("token") String token,
                @Field("keyWord") String keyWord,
                @Field("groupId") Integer groupId,
                @Field("folderId") Integer folderId, @Field("start") Integer start, @Field("pageSize") Integer pageSize);


        //加入免费圈子接口
        @POST("publish/groupJoin/join.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> joinFreeCirCle(@Field("token") String token,
                                                           @Field("groupId") int groupId,
                                                           @Field("message") String message);


        @POST("publish/group/getGroupData.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> getGroupData(@Field("token") String token,
                                                         @Field("groupId") int groupId);

        //获取圈子主页信息
        @POST("publish/group/getGroup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CircleBean>> getCircleMainInfo(@Field("token") String token,
                                                                  @Field("groupId") int groupId);

        //获取圈子介绍信息接口
        @POST("publish/group/getInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CircleBean>> getInfo(@Field("token") String token,
                                                        @Field("groupId") int groupId);

        //获取圈子资料信息
        @POST("publish/group/getGroupData.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CircleBean>> getCircleInfo(@Field("token") String token,
                                                              @Field("groupId") int groupId);

        //圈子文件列表接口
        @POST("publish/groupFile/listByGroup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleFileBean>>> getCircleFile(@Field("token") String token,
                                                                        @Field("pageSize") Integer pageSize,
                                                                        @Field("groupId") int groupId,
                                                                        @Field("start") int start,
                                                                        @Field("audit") Integer audit);

        //获取圈子动态
        @POST("publish/groupInfo/listByGroup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleHomeBean>>> getCircleDynamic(@Field("token") String token,
                                                                           @Field("groupId") int groupId,
                                                                           @Field("start") int start);

        //发布圈子动态
        @POST("publish/groupInfo/save.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> issueDynamic(@FieldMap HashMap<String, String> map);

        //获取用户动态
        @POST("publish/userPage/homePageGroupInfoList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleHomeBean>>> listByUserCode(@FieldMap HashMap<String, String> map);


        //70.	审核文件接口
        @POST("publish/groupFile/auditFile.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> auditFile(@Field("token") String token, @Field("fileId") String fileId, @Field("audit") Integer audit, @Field("groupId") Integer groupId);


        //因为分一群和二群获取圈子成员列表
        @POST("publish/group/getGroupMemberUserCode.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleMemberBean>>> getListMember1(@Field("chatId") String chatId, @Field("start") int start, @Field("pageSize") int pageSize);

        //因为分一群和二群获取圈子成员列表加loadTime
        @POST("publish/group/getGroupMemberUserCode.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleMemberBean>>> getListMember1(@Field("chatId") String chatId, @Field("start") int start, @Field("pageSize") int pageSize, @Field("loadTime") long loadTime);

        //搜索圈子成员列表
        @POST("publish/search/searchGroupMember.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleMemberBean>>> searchListMember(@FieldMap HashMap<String, String> map);


        //圈子成员定时禁言
        @POST("publish/group/setGroupTaskInTiming.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> setGroupTaskInTiming(@FieldMap HashMap<String, String> map);


        //搜索@成员列表
        @POST("publish/search/searchFriend.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<SearchChatBean>>> searchMentionedMember(@FieldMap HashMap<String, String> map);


        //获取圈子成员列表
        @POST("publish/group/listMember.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleMemberBean>>> getListMember(@Field("groupId") int groupId,
                                                                          @Field("type") int type,
                                                                          @Field("start") int start,
                                                                          @Field("pageSize") int pageSize,
                                                                          @Field("onlyLook") String onlyLook);

        //获取圈子成员列表
        @POST("publish/group/getGroupMemberUserCode.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleMemberBean>>> getListMember(@Field("chatId") String groupId);

        //41.修改圈子成员类型接口
        @POST("publish/group/changeMember.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> changeMember(@Field("token") String token,
                                                       @Field("groupId") Integer groupId, @Field("userCode") String userCode,
                                                       @Field("type") Integer type);

        //36.转让圈子接口
        @POST("publish/group/transfer.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> transafer(@Field("token") String token,
                                                    @Field("groupId") Integer groupId,
                                                    @Field("userCode") Integer userId);


        //37.清理圈子成员接口
        @POST("publish/group/clean.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> clean(@Field("token") String token,
                                                @Field("groupId") Integer groupId,
                                                @Field("userCode") Integer userId);


        /**
         * 获取新闻列表 个人中心展示
         *
         * @return
         */
        @POST("publish/news/getUserNewsList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<NewNewsBean.DataBean>>> getNewsInMine(
                @Field("userCode") String userCode, @Field("start") Integer start,
                @Field("groupId") Integer groupId);


        @POST("publish/search/searchNewsListByGroupId.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<NewNewsBean.DataBean>>> searchNewsListByGroupId(@FieldMap HashMap<String, String> map);


        /**
         * 15.评论动态接口
         */
        @POST("publish/groupInfo/comment.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CircleCommentBean>> commentReply(
                @FieldMap HashMap<String, String> map);


        /**
         * 39.	动态评论列表接口
         */
        @POST("publish/groupInfo/commentList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleCommentBean>>> commentList(
                @Field("infoId") Integer infoId, @Field("start") Integer start);


        /**
         * 16.	动态点赞接口
         *
         * @param token
         * @param infoId 动态ID
         * @return
         */
        @POST("publish/groupInfo/support.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ThumbsupVosBean>> support(@Field("token") String token,
                                                             @Field("infoId") Integer infoId);


        //直播课程列表接口
        @POST("publish/chatInfo/getUserChatInfoList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ChatInfosBean>>> getUserChatInfoList(
                @FieldMap HashMap<String, String> map);

        //97.获取圈子同步相关业务
        @POST("publish/groupInfo/getUnauditGroupInfoList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ClassAndSeriseBean>>> getUnauditGroupInfoList(
                @FieldMap HashMap<String, String> map);


        //搜索圈子同步课堂接口
        @POST("publish/search/searchChatOfGroup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ClassAndSeriseBean>>> searchClass(@FieldMap HashMap<String, String> map);


        //74.	获取圈子未审核课堂列表接口
        @POST("publish/chatInfo/getUnauditChatInfoList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ChatInfosBean>>> getUnauditChatInfoList(
                @Field("userCode") String userCode, @Field("start") Integer start, @Field("pageSize") Integer pageSize, @Field("groupId") Integer groupId);


        /*74.	获取圈子未审核系列课列表接口
         token   审核人token
        chatId  要审核的课程id
        groupId 要审核课程的圈子id
        auditType 审核类型    0不通过  1通过
        chatType 审核的课程类型  2课程  6系列课*/
        @POST("publish/group/auditChatOfGroup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> auditChatOfGroup(
                @Field("token") String token, @Field("chatId") String chatId,
                @Field("auditType") Integer audit, @Field("chatType") String chatType,
                @Field("groupId") Integer groupId);

        //74.获取圈子未审核课堂列表接口
        @POST("publish/group/auditNC.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> auditNC(
                @Field("token") String token, @Field("linkId") String linkId,
                @Field("audit") Integer audit, @Field("type") Integer type,
                @Field("groupId") Integer groupId);


        /**
         * 保存圈子资料(编辑圈子资料)
         */
        @POST("publish/group/save.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> editCircle(@FieldMap HashMap<String, Object> map);

        /**
         * 退出圈子
         */
        @POST("publish/groupJoin/quit.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> quitCircle(@FieldMap HashMap<String, Object> map);

        /**
         * 删除圈子
         */
        @POST("publish/group/delete.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> deleteCircle(@FieldMap HashMap<String, Object> map);

        /**
         * 删除动态
         */
        @POST("publish/groupInfo/deleteComment.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> deleteComment(@Field("id") Integer id, @Field("token") String token);


        //获取圈子介绍详情接口
        @POST("publish/groupDesc/getByGroup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CircleInfoBean>> getGroupDesc(@Field("groupId") int groupId);


        //101.	圈子数据统计点击付费加入或免费加入列表接口
        @POST("publish/group/getPersonByFeeOrFree.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ArrayList<CircleSignBean>>> getPersonByFeeOrFree(@Field("groupId") int groupId, @Field("start") int start, @Field("joinType") String joinType, @Field("userName") String userName, @Field("loadTime") long loadTime, @Field("Token") String token);


        //101.	圈子收入
        @POST("publish/group/listGroupStatisticsDataByDate.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<StatisticBean>>> listGroupStatisticsDataByDate(@Field("groupId") int groupId, @Field("start") int start, @Field("type") int type, @Field("token") String token, @Field("dateType") int dateType);

        //101.	圈子收益明细
        @POST("publish/group/listGroupStatisticsDataByDateDetail.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<StatisticDetailBean>>> listGroupStatisticsDataByDateDetail(@Field("groupId") int groupId, @Field("start") int start, @Field("timeSection") String timeSection);


        /*
           日访客
           dateType 时间类型 0每天 2月
           groupId 圈子Id
           checkType  0圈子主页  18介绍页
         */
        @POST("publish/group/listGroupVisitDataByDate.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<VisitorBean>>> listGroupVisitDataByDate(@Field("token") String token, @Field("groupId") int groupId, @Field("start") int start, @Field("checkType") String checkType, @Field("dateType") String dateType);

        /**
         * 日访客详情页
         *
         * @param groupId
         * @param start
         * @param checkType
         * @param timeSection
         * @return
         */
        @POST("publish/group/listGroupVisitDataByDateDetail.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<VisitorBean>>> listGroupVisitDataByDateDetail(@Field("groupId") int groupId, @Field("start") int start, @Field("checkType") String checkType, @Field("timeSection") String timeSection);


        //保存圈子介绍详情接口
        @POST("publish/groupDesc/save.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> saveGroupDesc(@FieldMap HashMap<String, Object> map);


        //发布群公告
        @POST("publish/groupNotice/save.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> saveGroupNotice(@Field("groupId") int groupId,
                                                          @Field("token") String token, @Field("title") String title,
                                                          @Field("context") String context);

        //发表文章      token , content 内容, digest 摘要, source 作者或者来源, title 标题, typeId 文章类型, cover 封面, sourceUrl 原链接  文档21
        @POST("publish/news/saveNews.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> issueArticle(@FieldMap HashMap<String, String> map);

        /**
         * 获取圈子资料
         *
         * @param token
         * @param groupId
         * @return
         */
        @POST("publish/group/getGroupData.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CircleBean>> circleData(@Field("token") String token,
                                                           @Field("groupId") int groupId);

        /**
         * 68.	圈子申请加入列表接口
         *
         * @param groupId
         * @param start
         * @return
         */
        @POST("publish/groupJoin/listJoin.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ApplyForBean>>> listJoin(@Field("groupId") int groupId,
                                                                 @Field("start") String start, @Field("token") String token);

        /**
         * 69.	圈子申请加入审核接口
         *
         * @param groupId
         * @param token
         * @param id      申请记录ID
         * @param audit
         * @return
         */
        @POST("publish/groupJoin/auditJoin.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> auditJoin(@Field("groupId") int groupId,
                                                    @Field("token") String token,
                                                    @Field("id") int id,
                                                    @Field("audit") int audit);


        /**
         * 47.	允许私聊接口
         *
         * @param token
         * @param groupId
         * @param privateChat 0、允许；1、不允许
         * @return
         */
        @POST("publish/group/privateChat.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> privateChat(@Field("token") String token,
                                                      @Field("groupId") Integer groupId, @Field("privateChat") String privateChat);


        /**
         * 46.	开放圈子成员接口
         *
         * @param token
         * @param groupId
         * @param dispark
         * @return
         */
        @POST("publish/group/dispark.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> dispark(@Field("token") String token,
                                                  @Field("groupId") Integer groupId, @Field("dispark") String dispark);

        /**
         * @param token
         * @param groupId
         * @param onlyReal 0、不需要；1、需要
         * @return
         */

        @POST("publish/group/setOnlyReal.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> setOnlyReal(@Field("token") String token,
                                                      @Field("groupId") Integer groupId, @Field("onlyReal") String onlyReal);


        @POST("publish/group/setGroupTaskInTiming.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> setCbIsUserCanJion(@Field("token") String token,
                                                             @Field("groupId") Integer groupId, @Field("type") String type, @Field("clickType") String clickType);


        /**
         * 71.	圈子成员加入是否审核接口
         *
         * @param token
         * @param groupId
         * @param audit   0、不审核；1、审核
         * @return
         */
        @POST("publish/group/isAuditMember.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> isAuditMember(@Field("token") String token,
                                                        @Field("groupId") Integer groupId, @Field("audit") Integer audit, @Field("prompt") String prompt);

        /**
         * 40.	圈子收费修改接口
         *
         * @param token
         * @param groupId
         * @param feeType
         * @param fee
         * @param pent
         * @return
         */
        @POST("publish/group/changeFee.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> changeFee(@Field("token") String token,
                                                    @Field("groupId") Integer groupId, @Field("feeType") String feeType,
                                                    @Field("fee") String fee, @Field("pent") String pent);


        /**
         * 免费邀请
         *
         * @param token
         * @param groupId
         * @return
         */
        @POST("publish/groupQr/getGroupFreeInviteUrl.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CircleShareInviteBean>> getGroupFreeInviteUrl(
                @Field("token") String token, @Field("groupId") Integer groupId);

        /**
         * 二维码分享
         *
         * @param token
         * @param groupId
         * @return
         */
        @POST("publish/groupQr/getGroupQr.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CircleErweimaBean>> getGroupQr(@Field("token") String token,
                                                                  @Field("groupId") Integer groupId);

        /**
         * 获取统计图标数据
         */
        @POST("publish/group/count.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CircleBean>> getChartData(@Field("groupId") int groupId,
                                                             @Field("token") String token);


        /**
         * 获取活跃用户数据
         */
        @POST("publish/group/active.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleBean>>> getActiveData(@Field("groupId") int groupId, @Field("start") int start);

        /**
         * 获取公告列表
         */
        @POST("publish/groupNotice/listNotice.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleBean>>> getNoticeData(@Field("groupId") int groupId, @Field("start") int start);

        /**
         * 获取公告详情
         */
        @POST("publish/groupNotice/getNotice.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CircleBean>> getNoticeDetailed(@Field("id") int id);

        /**
         * 根据群聊ID获取群组信息
         */
        @POST("publish/group/getGroupInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CircleBean>> getGroupInfo(@Field("token") String token, @Field("chatId") String chatId);


        /**
         * 64.	圈子设置精华动态接口
         *
         * @param groupId
         * @param token
         * @param groupInfoId
         * @return
         */
        @POST("publish/groupInfo/setGood.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> setGood(@Field("groupId") Integer groupId,
                                                  @Field("token") String token,
                                                  @Field("groupInfoId") Integer groupInfoId);

        /**
         * 保存支付密码
         */
        @POST("publish/member/savePayPwd.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> savePayPwd(@Field("token") String token, @Field("payPwd") String payPwd);

        /**
         * 224.	验证支付密码接口
         */
        @POST("publish/member/validatePayPwd.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> validatePayPwd(@Field("token") String token, @Field("payPwd") String payPwd);

        /**
         * 发红包接口  圈子文档53
         */
        @POST("publish/redBag/save.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<RedPacketBean>> issueRedPacket(@FieldMap HashMap<String, String> map);

        /**
         * 获取红包信息接口  圈子文档55
         */
        @POST("publish/redBag/getInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<RedPacketBean>> getRedPacketInfo(@Field("token") String token, @Field("redBagId") String redBagId);

        /**
         * 领取红包接口  圈子文档54
         */
        @POST("publish/redBag/snatch.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<RedPacketBean>> receiveRedPacket(@Field("ciphertext") String ciphertext);

        /**
         * 1.	获取红包领取信息列表接口 圈子文档56
         */
        @POST("publish/redBag/listRecord.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<RedPacketBean>>> redPacketList(@Field("redBagId") String redBagId, @Field("start") int start, @Field("loadTime") String loadTime);

        /**
         * 72.圈子群规保存（修改）接口
         */
        @POST("publish/group/saveRole.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<GroupRuleBean>> saveRole(@FieldMap HashMap<String, Object> map);

        /**
         * 73.获取圈子群规接口
         */
        @POST("publish/group/getRole.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<GroupRuleBean>> getRole(@Field("token") String token, @Field("groupId") int groupId);

        /**
         * 42.接受圈子动态接口
         */
        @POST("publish/group/receive.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> receiveDynamic(@Field("token") String token, @Field("groupId") int groupId, @Field("type") int type);

        /**
         * 14.动态详情接口
         */
        @POST("publish/groupInfo/getInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CircleHomeBean>> getDynamicData(@Field("token") String token, @Field("infoId") String infoId);

        /**
         * 225 根据圈子群聊id获取圈子成员数量接口
         */
        @POST("api/v%s/group/countGroupMember.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CircleBean>> getChatMemberCount(@Field("chatId") String chatId, @Field("token") String token);

        /**
         * 87.	根据群聊ID获取圈子角色接口
         */
        @POST("publish/group/getMemberByChat.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CircleBean>> getMemberByChat(@Field("token") String token, @Field("chatId") String chatId);

        /**
         * 88.	根据群聊ID禁言全体人员 0：禁言；1：解禁
         */
        @POST("publish/group/shutup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CircleBean>> shutup(@Field("token") String token, @Field("chatId") String chatId, @Field("type") String type);

        /**
         * 86 获取圈子群聊列表接口接口
         */
        @POST("publish/group/listGroupChat.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleBean>>> getChatList(@Field("token") String token, @Field("groupId") Integer groupId);

        /**
         * 94  圈子自定义排序接口
         */
        @POST("publish/group/orderGroup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> sortCircleList(@Field("token") String token, @Field("groupIds[]") List<String> groupIds);

        /**
         * 96  验证圈子创建邀请验证码
         */
        @POST("publish/group/checkCreatGroupCode.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> verifyInviteCode(@Field("token") String token, @Field("code") String code);


        /**
         * 96  获取圈子最近30天的新增用户、动态、活跃量数据
         */
        @POST("publish/group/listGroupStatisticsDynamicDate.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<RecentBean>>> getRecentNewData(@FieldMap Map<String, String> map);


        /**
         * 获取圈子邀请排行榜
         */
        @POST("publish/groupJoin/listGroupIncomeRanking.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleRankBean>>> getInviteTop(@FieldMap Map<String, String> map);

    }

    public static CircleApi.Service getInstance() {
        if (instance == null) {
            synchronized (CircleApi.class) {
                if (instance == null)
                    instance = ApiBuild.getRetrofit().create(CircleApi.Service.class);
            }
        }
        return instance;
    }
}
