package com.gxtc.huchuan.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gubr on 2017/3/13.
 */
public class ChatInfosBean implements Serializable {

    //1：讲师，2：普通学员，3：课程创建者，4：管理员
    public static String ROLE_TEACHER = "1";
    public static String ROLE_STUDENT = "2";
    public static String ROLE_CREATER = "3";
    public static String ROLE_MANAGER = "4";


    /**
     * 以文档为准 37.	获取直播间课程列表接口
     * chatRoom	直播间ID
     * chatRoomName	直播间名称
     * chatSeries	系列课ID
     * chatTypeSonId	直播分类ID
     * chattype	直播类型 0：公开，1：加密，2，收费
     * chatway	直播形式 0：讲座，1：幻灯片
     * endtime	结束时间，时间戳
     * fee	费用价格
     * freetime	是否免费试听，0否，1免费试听
     * id	课程ID
     * isfree	是否收费（0：免费，1：收费）
     * joinCount	参与数量
     * password	密码
     * pent	邀请人获得比例
     * showinfo	结束标识。 0：正常，1：结束
     * starttime	开始时间
     * subtitle	直播课程主题
     * facePic	封面图片
     * chatRoomHeadPic	直播间头像图片
     * chatInfoCount	直播间课程数量
     * chatSeriesCount	直播间系列课数量
     * mainSpeaker	主讲人
     * speakerIntroduce	主讲人介绍
     * desc	直播描述
     * showSignup	是否显示报名用户统计。0，1
     * 0：显示，1：不显示
     * showShare	是否显示分享榜。0，1
     * 0：显示，1：不显示
     * signupUsers	报名人信息列表
     * shareUsers	分享人信息列表
     * followCount	关注数量
     * isSignup	是否报名成功。0和1
     * 0：为报名，1：已报名
     * isFollow	是否关注。0和1
     * 0：未关注，
     * 1：已关注
     * isSelf	是否是自己的系列课。0和1
     * 0：不是，1：是
     * status	直播状态。1：预告，2：直播中，3：结束
     * shareUrl	分享推荐url
     * isBanned	是否禁言。0：否，1：禁言
     * roleType	直播身份类型。     1：讲师，2：普通学员，3：课程创建者，4：管理员
     * groupId	圈子id
     * isForGrop
     * 0:不是圈子专属，
     * 1：圈子专属
     * joinGroup	是否加入圈子。
     * 0：未加入；1：已加入
     * name	直播间创建人用户名字，自媒体名字
     * headPic	直播间创建人用户头像
     * userCode	直播间创建人用户编码
     */

    /**
     * 课堂分类
     */
    private String typeCode;        //分类编码
    private String typeName;        //分类名称

    private static final long serialVersionUID = -763618247875560522L;

    private String chatInfoCount;
    private String chatRoom;
    private String chatRoomHeadPic;
    private String chatRoomName;
    private String chatSeries;
    private String chatSeriesCount;
    private String chatTypeSonId;
    private String chattype;
    private String chatway;
    private String desc;
    private String endtime;
    private String fee;
    private String freetime;
    private String id;
    private String isfree;
    private String joinCount;
    private String mainSpeaker;
    private String password;
    private String pent;
    private String showShare;
    private String showSignup;
    private String isSignup;
    private String isFollow;
    private String showinfo;
    private String starttime;
    private String followCount;
    private String subtitle;
    private String isSelf;
    private String facePic;
    private long groupId;
    private String isForGrop;
    private String joinGroup;
    private String speakerIntroduce;
    private String shareUrl;
    private String isBanned;        //是否禁言。0：否，1：禁言
    private String status;          //直播状态。1：预告，2：直播中，3：结束
    private List<SignUpMemberBean> signupUsers;
    private List<PersonCountBean> shareUsers;
    private String roleType;
    private String name;
    private String headPic;
    private String userCode;
    private String isUpdate;        //0、不可修改；1、可以修改
    private int isBlack;         //是否在黑名单   0 不在   1 在
    private int messageNum;      //消息数量      这个字段暂时不用了
    private int chatCommentCount;      //课程讨论数量
    private int isGroup;         //课堂是否已同步圈子isGroup 0、未同步；1、已同步
    private int spreadPent;     //平台分成比例
    private String isRefund;        //0、不可退款；1、可以退款 2,申请中 3，申请退款通过 4，申请不通过
    private String refundRemark;
    private String audit;
    private SeriesPageBean chatSeriesData;

    //系列课与课程合并
    private int type; //业务类型，1=话题，2=系列课
    private int isFee;
    private String cover;
    private String title;
    private String time;
    private String userName;
    private String showInfo;
    private int isFree;//是否免费,0=免费，1=收费
    private String isPublish;
    private String videoPic; //封面
    private String videoText; //路径

    public int getChatCommentCount() {
        return chatCommentCount;
    }

    public void setChatCommentCount(int chatCommentCount) {
        this.chatCommentCount = chatCommentCount;
    }

    public String getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(String isPublish) {
        this.isPublish = isPublish;
    }

    public int getIsFree() {
        return isFree;
    }

    public void setIsFree(int isFree) {
        this.isFree = isFree;
    }

    public String getShowInfo() {
        return showInfo;
    }

    public void setShowInfo(String showInfo) {
        this.showInfo = showInfo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIsFee() {
        return isFee;
    }

    public void setIsFee(int isFee) {
        this.isFee = isFee;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public int getSpreadPent() {
        return spreadPent;
    }

    public void setSpreadPent(int spreadPent) {
        this.spreadPent = spreadPent;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public SeriesPageBean getChatSeriesData() {
        return chatSeriesData;
    }

    public void setChatSeriesData(SeriesPageBean chatSeriesData) {
        this.chatSeriesData = chatSeriesData;
    }

    public String getAudit() {
        return audit;
    }

    public void setAudit(String audit) {
        this.audit = audit;
    }

    public String getRefundRemark() {
        return refundRemark;
    }

    public void setRefundRemark(String refundRemark) {
        this.refundRemark = refundRemark;
    }

    public int getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(int isGroup) {
        this.isGroup = isGroup;
    }

    private String isCollect;//是否收藏，（0：未收藏；1：已收藏）

    public String getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(String isRefund) {
        this.isRefund = isRefund;
    }

    public int getMessageNum() {
        return messageNum;
    }

    public void setMessageNum(int messageNum) {
        this.messageNum = messageNum;
    }

    public String getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(String isCollect) {
        this.isCollect = isCollect;
    }

    public String getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(String isUpdate) {
        this.isUpdate = isUpdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getIsForGrop() {
        return isForGrop;
    }

    public void setIsForGrop(String isForGrop) {
        this.isForGrop = isForGrop;
    }

    public String getJoinGroup() {
        return joinGroup;
    }

    public void setJoinGroup(String joinGroup) {
        this.joinGroup = joinGroup;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(String isBanned) {
        this.isBanned = isBanned;
    }

    public String getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(String isSelf) {
        this.isSelf = isSelf;
    }

    public boolean isSelff() {
        return "1".equals(isSelf);
    }

    public String getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(String isFollow) {
        this.isFollow = isFollow;
    }

    public boolean isFolow() {
        return "1".equals(isFollow);
    }


    public boolean toggleFollow() {
        if (isFolow()) {
            isFollow = "0";
        } else {
            isFollow = "1";
        }
        return isFolow();
    }


    public String getIsSignup() {
        return isSignup;
    }

    public void setIsSignup(String isSignup) {
        this.isSignup = isSignup;
    }

    public String getFollowCount() {
        return followCount;
    }

    public void setFollowCount(String followCount) {
        this.followCount = followCount;
    }


    public String getChatInfoCount() {
        return chatInfoCount;
    }

    public void setChatInfoCount(String chatInfoCount) {
        this.chatInfoCount = chatInfoCount;
    }

    public String getChatSeriesCount() {
        return chatSeriesCount;
    }

    public void setChatSeriesCount(String chatSeriesCount) {
        this.chatSeriesCount = chatSeriesCount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMainSpeaker() {
        return mainSpeaker;
    }

    public void setMainSpeaker(String mainSpeaker) {
        this.mainSpeaker = mainSpeaker;
    }

    public String getShowShare() {
        return showShare;
    }

    public void setShowShare(String showShare) {
        this.showShare = showShare;
    }

    public String getShowSignup() {
        return showSignup;
    }

    public void setShowSignup(String showSignup) {
        this.showSignup = showSignup;
    }

    public String getSpeakerIntroduce() {
        return speakerIntroduce;
    }

    public void setSpeakerIntroduce(String speakerIntroduce) {
        this.speakerIntroduce = speakerIntroduce;
    }


    public List<SignUpMemberBean> getSignupUsers() {
        return signupUsers;
    }

    public void setSignupUsers(List<SignUpMemberBean> signupUsers) {
        this.signupUsers = signupUsers;
    }

    public List<PersonCountBean> getShareUsers() {
        return shareUsers;
    }

    public void setShareUsers(List<PersonCountBean> shareUsers) {
        this.shareUsers = shareUsers;
    }

    public String getChatRoomHeadPic() {
        return chatRoomHeadPic;
    }

    public void setChatRoomHeadPic(String chatRoomHeadPic) {
        this.chatRoomHeadPic = chatRoomHeadPic;
    }

    public String getFacePic() {
        return facePic;
    }

    public void setFacePic(String facePic) {
        this.facePic = facePic;
    }

    public String getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(String chatRoom) {
        this.chatRoom = chatRoom;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public String getChatSeries() {
        return chatSeries;
    }

    public void setChatSeries(String chatSeries) {
        this.chatSeries = chatSeries;
    }

    public String getChatTypeSonId() {
        return chatTypeSonId;
    }

    public void setChatTypeSonId(String chatTypeSonId) {
        this.chatTypeSonId = chatTypeSonId;
    }

    public String getChattype() {
        return chattype;
    }

    public void setChattype(String chattype) {
        this.chattype = chattype;
    }

    public String getChatway() {
        return chatway;
    }

    public void setChatway(String chatway) {
        this.chatway = chatway;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getFee() {
        return fee;
    }


    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getFreetime() {
        return freetime;
    }

    public void setFreetime(String freetime) {
        this.freetime = freetime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsfree() {
        return isfree;
    }

    public void setIsfree(String isfree) {
        this.isfree = isfree;
    }

    public String getJoinCount() {
        return joinCount;
    }

    public void setJoinCount(String joinCount) {
        this.joinCount = joinCount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPent() {
        return pent;
    }

    public void setPent(String pent) {
        this.pent = pent;
    }

    public String getShowinfo() {
        return showinfo;
    }

    public void setShowinfo(String showinfo) {
        this.showinfo = showinfo;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public boolean isFree() {

        //        return isfree.equals("0");
        return "0".equals(isfree);
    }

    /**
     * @return
     */
    public boolean isFreeTime() {
        return freetime.endsWith("1");
    }

    /**
     * 是否已经报名  如果是收费的 也就是要收费后才能报名成功  有报名就是收费了
     */
    public boolean isSingUp() {
        return "1".equals(isSignup);
    }


    /**
     * true 显示要显示
     *
     * @return
     */
    public boolean isShowShare() {
        return showShare.equals("0");
    }

    /**
     * true 显示要显示
     *
     * @return
     */
    public boolean isShowSignUp() {
        return showSignup.endsWith("0");

    }

    public String getVideoPic() {
        return videoPic;
    }

    public void setVideoPic(String videoPic) {
        this.videoPic = videoPic;
    }

    public String getVideoText() {
        return videoText;
    }

    public void setVideoText(String videoText) {
        this.videoText = videoText;
    }

    @Override
    public String toString() {
        return "ChatInfosBean{" + "chatInfoCount='" + chatInfoCount + '\'' + ", chatRoom='" + chatRoom + '\'' + ", chatRoomHeadPic='" + chatRoomHeadPic + '\'' + ", chatRoomName='" + chatRoomName + '\'' + ", chatSeries='" + chatSeries + '\'' + ", chatSeriesCount='" + chatSeriesCount + '\'' + ", chatTypeSonId='" + chatTypeSonId + '\'' + ", chattype='" + chattype + '\'' + ", chatway='" + chatway + '\'' + ", desc='" + desc + '\'' + ", endtime='" + endtime + '\'' + ", fee='" + fee + '\'' + ", freetime='" + freetime + '\'' + ", id='" + id + '\'' + ", isfree='" + isfree + '\'' + ", joinCount='" + joinCount + '\'' + ", mainSpeaker='" + mainSpeaker + '\'' + ", password='" + password + '\'' + ", pent='" + pent + '\'' + ", showShare='" + showShare + '\'' + ", showSignup='" + showSignup + '\'' + ", isSignup='" + isSignup + '\'' + ", isFollow='" + isFollow + '\'' + ", showinfo='" + showinfo + '\'' + ", starttime='" + starttime + '\'' + ", followCount='" + followCount + '\'' + ", subtitle='" + subtitle + '\'' + ", isSelf='" + isSelf + '\'' + ", facePic='" + facePic + '\'' + ", groupId=" + groupId + ", isForGrop='" + isForGrop + '\'' + ", joinGroup='" + joinGroup + '\'' + ", speakerIntroduce='" + speakerIntroduce + '\'' + ", shareUrl='" + shareUrl + '\'' + ", isBanned='" + isBanned + '\'' + ", status='" + status + '\'' + ", signupUsers=" + signupUsers + ", shareUsers=" + shareUsers + ", roleType='" + roleType + '\'' + '}';
    }
}
