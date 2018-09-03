package com.gxtc.huchuan.http.service;

import com.gxtc.huchuan.bean.BannedOrBlackUserBean;
import com.gxtc.huchuan.bean.ChannelBean;
import com.gxtc.huchuan.bean.ChatInfosBean;
import com.gxtc.huchuan.bean.CheckBean;
import com.gxtc.huchuan.bean.CircleCommentBean;
import com.gxtc.huchuan.bean.CircleErweimaBean;
import com.gxtc.huchuan.bean.CircleHomeBean;
import com.gxtc.huchuan.bean.CircleNewsBean;
import com.gxtc.huchuan.bean.CollectionBean;
import com.gxtc.huchuan.bean.CreateCircleBean;
import com.gxtc.huchuan.bean.DustbinListBean;
import com.gxtc.huchuan.bean.FocusBean;
import com.gxtc.huchuan.bean.MineCircleBean;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.bean.NewsAdsBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.NewsCommentBean;
import com.gxtc.huchuan.bean.PreProfitBean;
import com.gxtc.huchuan.bean.SearchBean;
import com.gxtc.huchuan.bean.SearchChatBean;
import com.gxtc.huchuan.bean.ShareMakeMoneyBean;
import com.gxtc.huchuan.bean.ThumbsupVosBean;
import com.gxtc.huchuan.bean.UpdataBean;
import com.gxtc.huchuan.bean.UpyunBean;
import com.gxtc.huchuan.http.ApiBuild;
import com.gxtc.huchuan.http.ApiResponseBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.edu.zafu.coreprogress.progress.ProgressResponseBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by sjr on 2017/2/13.
 * 尽量不要把接口放这边重新自己写，因为多人修改版本管理有些麻烦
 */

public class AllApi {
    private static AllApi.Service instance;

    public interface Service {

        /**
         * 获取新闻频道
         *
         * @param str
         * @return
         */
        @POST("publish/news/getNewsType.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<ChannelBean>> getNewsChannels(@Field("") String str);

        /**
         * 获取新闻列表 资讯首页展示
         *
         * @param token   非必须
         * @param typeId  频道类型
         * @param start   从0开始
         * @param isVideo 媒体类型。不填默认为空，查询所有0：咨询，1：视频
         * @return
         */
        @POST("publish/news/getNews.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<NewsBean>>> getNews(@Field("token") String token,
                                                            @Field("typeId") String typeId,
                                                            @Field("start") String start,
                                                            @Field("clickType") String clickType,
                                                            @Field("isVideo") String isVideo);

        /**
         * 最新的获取资讯的列表
         * @param token
         * @param typeId
         * @param start
         * @param typeId
         * @return
         */
        @POST("api/v%s/news/getNewsList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<NewNewsBean>>> getNewsList(@Field("token") String token, @Field("typeId") String typeId,@Field("start") String start, @Field("pageSize") String pageSize, @Field("isVideo") int isVideo);

        /**
         * 获取新闻列表 个人中心展示/圈子已通过文章
         * @param map
         * @return
         */
        @POST("publish/news/getUserNewsList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<NewsBean>>> getNewsInMine(@FieldMap HashMap<String, String> map);

        /**
         * 圈子已通过文章
         * @param map
         * @return
         */
        @POST("publish/news/getUserNewsList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleNewsBean>>> getNewsInCircle(
                        @FieldMap HashMap<String, String> map);

        @POST("publish/news/getUnauditNewsList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleNewsBean>>> getNewsInCircleAudit(
                @FieldMap HashMap<String, String> map);

        /**
         * 点赞或取消点赞
         *
         * @param token
         * @param newsId
         * @return
         */
        @POST("publish/newsThumbsup/saveThumbsup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> getThumbsup(@Field("token") String token,
                                                        @Field("newsId") String newsId);

        /**
         * 收藏或取消收藏
         *
         * @param token
         * @param newsId
         * @return
         */
        @Deprecated
        @POST("publish/newsCollection/saveCollection.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> getCollect(@Field("token") String token,
                                                       @Field("newsId") ArrayList<String> newsId);

        /**
         * 收藏或取消收藏
         * <p>
         * token	是	用户token
         * bizType	否	业务类型：
         * 1：新闻文章，2：话题，3：交易信息，4：圈子动态，5：自定义
         * bizId	否	业务id，业务类型不等于5时必传
         * content	否	收藏内容，业务类型等于5时必传
         * title	否	收藏标题，业务类型等于5时必传
         * delIds	否	删除的收藏id，不为空时删除，为空时处理成保存
         *
         * @return
         */
        @POST("publish/userCollection/saveCollection.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> saveCollection(@FieldMap Map<String, String> map);


        @POST("publish/group/createGroupChat.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> createGroupChat(@Field("token") String token, @Field("groupId") int groupId);


        @POST("publish/groupInfo/setGood.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> setGood(@FieldMap Map<String, String> map);

        @POST("publish/groupInfo/setTop.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> setTop(@FieldMap Map<String, String> map);

        /**
         * 91.	圈子成员禁止/解除发动态接口
         * @param map
         * @return
         */
        @POST("publish/group/setGroupUserDynamicStart.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> setGroupUserDynamicStart(@FieldMap Map<String, String> map);


        /**
         * 删除自己的文章
         *
         * @param token
         * @param newsId
         * @return
         */
        @POST("publish/news/deleteNews.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> deleteNews(@Field("token") String token, @Field("newsId") String newsId);

        /**
         * 获取收藏列表
         * ps:收藏列表实体和新闻列表实体是一样的
         *
         * @param token
         * @param start
         * @return
         */
        @POST("publish/newsCollection/getNewsCollectionList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<NewsBean>>> getCollectList(@Field("token") String token,
                                                                   @Field("start") String start);

        /**
         * 183.	获取用户的收藏列表接口
         *
         * @param token
         * @param start
         * @return
         */
        @POST("publish/userCollection/getCollectionList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CollectionBean>>> getCollectionList(@Field("token") String token,
                                                                            @Field("start") String start);

        /**
         * 获取自己写的新闻文章列表接口
         *
         * @param token
         * @param start
         * @param audit 审核标记。0,1,2。如果不填写默认查全部0：未审核；1：审核通过；2：审核不通过
         * @return
         */
        @POST("publish/news/getSelfNews.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<NewsBean>>> getMineArticle(@Field("token") String token,
                                                                   @Field("start") String start, @Field("audit") String audit);

        /**
         * 获取广告集合接口
         *
         * @param type 广告类型(01：APP封面；02：资讯首页；03：app启动广告)
         * @return
         */
//        @POST("publish/advert/listByRand.do")
        @POST("api/v%s/advert/listByRands.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<NewsAdsBean>>> getNewsAds(@Field("type") String type);

        /**
         * 投诉
         *
         * @param token
         * @param type       投诉类型。1 文章评论，2 直播间 3 直播评论4，	交易信息，5，	圈子内容
         * @param reportedId 对应的投诉ID。文章ID，文章评论ID，直播间ID，直播评论ID
         * @param content    投诉内容
         * @return
         */
        @POST("publish/userReported/saveUserReported.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> complaintArticle(@Field("token") String token,
                                                           @Field("type") String type, @Field("reportedId") String reportedId,
                                                           @Field("content") String content);

        @POST("publish/userReported/saveUserReported.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> complaintArticle( @FieldMap Map<String, String>  map);

        /**
         * 获取关注列表
         *
         * @param userCode 用户编码
         * @param type     关注列表类型。1，2，3，4，5  1：我关注的用户列表，2：关注我的用户列表，3：互相关注，4：推荐 ，5 关注我的用户列表（不包含我关注的）,6: 申请加我为好友的用户列表（不包含我已接受的） 7:保留列表内已经加过的人
         * @param start    分页
         * @return
         */
        @POST("publish/newsFollow/getFollowAuthorList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<FocusBean>>> getFocus(@Field("userCode") String userCode,
                                                              @Field("type") String type, @Field("start") String start,@Field("groupChatId") String groupChatId);

        /**
         * 关注或取消关注 如果当前是ture  再请求一次就是flase
         *
         * @param token
         * @param followType 关注类型。1，2，3 1：新闻，2:直播间，3：个人主页
         * @param bizId      关注的业务 ID。新闻ID，直播间ID，用户编码userCode
         * @return
         */
        @POST("publish/newsFollow/userFollow.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> setUserFollow(@Field("token") String token,
                                                          @Field("followType") String followType, @Field("bizId") String bizId);

        /**
         * 区分上面的关注 这个用于好友申请
         *
         * @param followType 关注类型。1，2，3 1：新闻，2:直播间，3：个人主页
         * @param bizId      关注的业务 ID。新闻ID，直播间ID，用户编码userCode
         * @param message   申请好友时验证
         */
        @POST("publish/newsFollow/userPFollow.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> applyFriends(@Field("token") String token, @Field("followType") String followType, @Field("bizId") String bizId, @Field("message") String message);


        /**
         * 获取直播课程的（黑名单或封禁）用户列表接口
         *
         * @param token
         * @param chatRoomId 直播间ID
         * @param type       用户处理类型。1和2 1：黑名单，2：封禁
         * @return
         */
        @POST("publish/chatRoom/getChatRoomUserMngList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<BannedOrBlackUserBean>>> bannedOrBalck(
                @Field("token") String token, @Field("chatRoomId") String chatRoomId,
                @Field("type") String type);

        /**
         * 获取直播课程的（黑名单或封禁）用户列表接口(新)
         *
         * type 1拉黑，2禁言
         * chatId 课程ID/系列课id
         * chatType 1课程  2系列课
         * start
         * pageSize
         * token
         */
        @POST("publish/chatJoinMember/listByChatUserMemberStatus.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<BannedOrBlackUserBean>>> bannedOrBalck(@FieldMap HashMap<String,String> map);


        /**
         * 直播间（黑名单，封禁）用户操作接口
         *
         * @param token      是	用户token
         * @param chatRoomId 是	直播间ID
         * @param type       是	用户处理类型。1和2 1：黑名单，2：封禁
         * @param userCode   是	用户编码
         * @return
         */
        @POST("publish/chatRoom/saveChatRoomUserMng.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<BannedOrBlackUserBean>>> cancelBannedOrBalck(
                @Field("token") String token, @Field("chatRoomId") String chatRoomId,
                @Field("type") String type, @Field("userCode") String userCode);

        /**
         * 直播间（黑名单，封禁）用户操作接口
         *
         * @param token
         * @param chatRoomId
         * @param type
         * @param userCode
         * @return
         */
        @POST("publish/chatRoom/saveChatRoomUserMng.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<BannedOrBlackUserBean>>> saveChatRoomUserMng(
                @Field("token") String token, @Field("chatRoomId") String chatRoomId,
                @Field("type") String type, @Field("userCode") String userCode,
                @Field("handleType") String handleType);



        /**
         * token     否	用户token
         * type      是	搜索类型。1，2，3，4
         *   1：文章，
             2：直播间话题，
             3：直播间，
             4：担保交易，
             5：自媒体，
             6：圈子，
             7：朋友圈动态
         * start     是	分页起始数量，从0开始
         * searchKey 是	搜索关键字
         *
         * @return
         */
        @POST("publish/search/searchList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<SearchBean>>> searchList(@FieldMap HashMap<String, String> map);

        /**
         * 创建圈子接口
         *
         * @param token
         * @param id      圈子ID。创建为空、修改不为空
         * @param groupName 圈子名称
         * @param content   圈子描述
         * @param cover     封面图片地址
         * @param isFee     是否收费。0：免费；1：收费
         * @param fee       费用/年
         * @param pent      分销比例
         * @param isPublic      0、公开，1、私密
         * @param groupType      圈子类型
         * @return
         */
        @POST("publish/group/save.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CreateCircleBean>> saveCircle(@Field("token") String token,@Field("id") String id,@Field("isPublic") int isPublic,@Field("groupType") int groupType,
                                                                 @Field("groupName") String groupName, @Field("content") String content,
                                                                 @Field("cover") String cover, @Field("isFee") Integer isFee,
                                                                 @Field("fee") double fee, @Field("pent") String pent,
                                                                 @Field("chatId") String chatId,@Field("createGroupChat") int createGroupChat);

        /**
         * 获取用户个人圈子
         *
         * @param token
         * @param type  0：全部数据、1：创建的、2：关注的 3：管理的
         * @return
         */
        @POST("publish/group/listByUser.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<MineCircleBean>>> listByUser(@Field("token") String token,
                                                                     @Field("type") String type);

        /**
         * 获取	圈子首页 推荐圈子接口
         */
        @POST("publish/group/listRecommend.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<MineCircleBean>>> getListRecommend(@Field("token") String token,
                                                                     @Field("start") int start);

        /**
         * 11.	圈子首页
         *
         * @param token
         * @param start
         * @return
         */
        @POST("publish/groupInfo/listHome.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleHomeBean>>> circleHomelist(@Field("token") String token, @Field("start") String start);

        /**
         * 	获取推荐动态
         *
         * @param type = 1
         * @param start
         * @return
         */
        @POST("api/v%s/groupInfo/listGroupInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleHomeBean>>> recommenddynamic(@Field("token") String token , @Field("type") String type ,@Field("start") String start);

        /**
         * 获取点赞列表
         * @return
         */
        @POST("publish/groupInfo/listThumbs.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ThumbsupVosBean>>> getlistThumbs( @FieldMap HashMap<String, String> map);

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


        /**
         * 23.	圈子名称校验接口
         *
         * @param name
         * @return
         */
        @POST("publish/group/verifyName.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> checkOutCircleName(@Field("name") String name);

        /**
         * 删除圈子动态
         *
         * @param token
         * @param infoId
         * @return
         */
        @POST("publish/groupInfo/delete.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> deleteDT(@Field("token") String token,
                                                   @Field("infoId") Integer infoId);


        /**
         *
         *
         * @param token
         * @param targetId 对象id。(动态Id或用户code)
         * @param type     类型。0、屏蔽用户；1、屏蔽动态
         * @return
         */
        /**
         * 52.	圈子动态屏蔽接口
         * token
         * targetId 对象id。(动态Id或用户code)
         * type     类型。0、屏蔽用户；1、屏蔽动态
         *
         * @param map
         * @return
         */
        @POST("publish/groupInfo/shield.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> shield(@FieldMap HashMap<String, String> map);

        @POST("publish/userScren/deleteByUserCode.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> deleteByUserCode(@FieldMap HashMap<String, String> map);

        /**
         * 资讯评论
         *
         * @param token
         * @return
         */
        @POST("publish/newsComment/saveComment.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<NewsCommentBean>> saveComment(@Field("token") String token,
                                                                 @Field("newsId") String newsId, @Field("comment") String comment);


        /**
         * 资讯评论别人的回复
         *
         * @param token
         * @param newsId
         * @param comment
         * @return
         */
        @POST("publish/newsComment/saveCommentReply.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<NewsCommentBean>> saveCommentReply(@Field("token") String token,
                                                                      @Field("newsCommentId") String newsId, @Field("comment") String comment,@Field("userCode") String targetUserCode,@Field("id") String targetId);

        /**
         * 圈子回复跟二级回复
         *
         * @param token
         * @param groupInfoId
         * @param comment
         * @param userCode
         * @return
         */
        @POST("publish/groupInfo/comment.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CircleCommentBean>> comment(@Field("token") String token,
                                                               @Field("groupInfoId") Integer groupInfoId, @Field("comment") String comment,
                                                               @Field("userCode") String userCode);

        /**
         * //         * 圈子动态评论
         * //         *
         * //         * @param token
         * //         * @param groupInfoId
         * //         * @param comment
         * //         * @return
         * //
         */
        @POST("publish/groupInfo/comment.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CircleCommentBean>> commentCircle(@Field("token") String token,
                                                                     @Field("groupInfoId") Integer groupInfoId, @Field("comment") String comment, @Field("groupId") Integer groupId);


        /**
         * 28.	保存圈子文件接口
         *
         * @param groupId   圈子id
         * @param token
         * @param fileUrls
         * @param fileNames
         * @param fileType  文件类型。0、word；1、excel；2、ppt；3、pdf；4、图片；5、视频；6、exe；7、压缩文件；8、其他
         * @return
         */
        @POST("publish/groupFile/save.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> saveCircleFile(@Field("groupIds") Integer groupId,
                                                         @Field("token") String token, @Field("fileUrls") String fileUrls,
                                                         @Field("fileNames") String fileNames,
                                                         @Field("fileType") Integer fileType,
                                                         @Field("folderId") Integer folderId);

        /**
         * 圈子动态列表
         *
         * @param groupId
         * @param token
         * @param start
         * @param pageSize
         * @return
         */
        @POST("publish/groupInfo/listByGroup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleHomeBean>>> listByGroup(
                @Field("groupId") Integer groupId, @Field("token") String token,
                @Field("start") Integer start, @Field("pageSize") Integer pageSize, @Field("loadTime") long loadTime);

        /**
         * 删除动态
         *
         * @param token
         * @param infoId
         * @return
         */
        @POST("publish/groupInfo/delete.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> deleteTopic(@Field("token") String token,
                                                      @Field("infoId") ArrayList<Integer> infoId);
        /**
         * 删除动态
         *
         * @param token
         * @param infoId
         * @return
         */
        @POST("publish/groupInfo/deleteList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> deletedynimic(@Field("token") String token,
                                                        @Field("groupId") int groupId,
                                                      @Field("infoIds") ArrayList<Integer> infoId);

        /**
         * 多文件上传
         *
         * @param body
         * @return
         */
        @POST("publish/file/uploadFiles.do")
        Observable<ApiResponseBean<List<String>>> uploadFiles(@Body RequestBody body);

        /**
         * 圈子动态
         *
         * @return
         */
        @POST("publish/groupInfo/listByGroup.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleHomeBean>>> listByGroup(
                @Field("groupId") Integer groupId, @Field("token") String token,
                @Field("start") String start,@Field("loadTime") long loadTime);

        /**
         * 150.	获取分销赚钱邀请列表接口
         *
         * @param token
         * @param start
         * @return
         */
        @POST("publish/mMoney/listForMakeMoneyByType.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ShareMakeMoneyBean>>> shareMakeMoney(@Field("token") String token, @Field("type") String type, @Field("orderByType") String orderByType, @Field("start") String start, @Field("seachParam") String searchParm);

        /**
         * 180.获取用户的分销邀请列表(预收益)接口
         */
        @POST("publish/mMoney/getSaleInviteList.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<PreProfitBean>>> getSaleInviteList(
                @Field("token") String token, @Field("start") String start);


        /**
         * @param token
         * @param type  评论类型。1：主评论，2：评论回复
         * @param id    评论id
         * @return
         */
        @POST("publish/newsComment/delComment.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> delComment(@Field("token") String token,
                                                     @Field("type") int type,
                                                     @Field("id") int id);

        /**
         * 精华圈子动态列表
         *
         * @return
         */
        @POST("publish/groupInfo/listByGood.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<CircleHomeBean>>> getEssenceList(@Field("groupId") Integer groupId,
                                                                         @Field("token") String token,
                                                                         @Field("start") String start);

        /**
         * 回收站列表
         *
         * @return
         */
        @POST("publish/recycle/list.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<DustbinListBean>>>getDustbinList(@Field("token") String token,
                                                                         @Field("start") int start);
        /**
         * 恢复回收站列表
         *
         * @return
         */
        @POST("publish/recycle/recycle.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>>HuiFuDustbin(@Field("token") String token,
                                                                               @Field("linkId") int linkId,
                                                                               @Field("type") int type);



        @POST("publish/group/notalk.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Void>> setMeamberNotSay(@Field("groupId") Integer groupId,
                                                           @Field("token") String token,
                                                           @Field("minute") Integer minute,
                                                           @Field("ntUserId") String userCode);
        //233接口 获取审核失败原因接口
        @POST("publish/auditInfo/getInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CheckBean>> getInfo(@Field("token") String token,
                                                       @Field("linkId") String linkId,
                                                       @Field("linkType") String linkType);


        //233接口 获取统计
        @POST("publish/appUserStatistics/saveClickRecord.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> saveClickRecord(@FieldMap Map<String,String> map);


        @POST("publish/baseQrurl/getBaseUrl.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> getBaseUrl(@Field("token") String token, @Field("type") int type, @Field("objId") int objId);


        @POST("publish/baseQrurl/getUrl.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> getUrl(@Field("type") int type);


        @POST("publish/news/getNewsInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<NewsBean>> getNewsInfo(@Field("token") String token, @Field("newsId") String newsId);

        @POST("publish/news/getNewsInfo.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<NewsAdsBean.NewsRespVoBean>> getNewsInfo(@FieldMap HashMap<String,String> map);


        /**
         * 搜索查询接口
         */
        @POST("publish/search/searchChat.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<SearchChatBean>>> searchChat(@Field("token") String token, @Field("start") int start, @Field("searchKey") String searchKey);

        /**
         * 获取课程分类接口
         */
        @POST("publish/chatInfoType/listType.do")
        Observable<ApiResponseBean<List<ChatInfosBean>>> getLiveType();


        /**
         * 根据课程分类获取课程数据  原来的接口 publish/chatInfo/listByChatType.do
         */
        @Deprecated
        @POST("publish/chatInfo/listByChatTypeNew.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<List<ChatInfosBean>>> getLiveByType(@Field("token") String token ,@Field("chatInfoTypeId") String typeId, @Field("start") int start);

        /**
         * 获取其他业务二维码
         */
        @POST("publish/baseQrurl/getBaseUrl.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<CircleErweimaBean>> getOrderQrcode(@FieldMap Map<String,String> map);


        /**
         * 屏蔽文章接口
         */
        @POST("publish/userNewsScreen/screenNewsInfoByType.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<Object>> shieldArticle(@FieldMap Map<String,String> map);


        /**
         * 获取最新版本接口
         * type 0 = 安卓
         */
        @POST("publish/version/versionVerification.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<UpdataBean>> getAppVersion(@Field("type") int type);


        /**
         * 获取获取upyun 表单上传参数
         */
        @POST("/publish/file/getPolicyInAuthorizationNew.do")
        @FormUrlEncoded
        Observable<ApiResponseBean<UpyunBean>> getUpyunSign(@Field("type") int type, @Field("token") String token, @Field("name") String name);

    }


    public static AllApi.Service getInstance() {
        if (instance == null) {
            instance = ApiBuild.getRetrofit().create(AllApi.Service.class);
        }
        return instance;
    }
}
