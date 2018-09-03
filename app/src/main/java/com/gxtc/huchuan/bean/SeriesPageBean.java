package com.gxtc.huchuan.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gubr on 2017/3/24.
 * 系列课主页 bean
 */

public class SeriesPageBean implements Serializable, IBuyData {


    /**
     * id	系列课ID
     * chatRoom	直播间ID
     * headpic	系列课头像
     * Seriesname	系列课名称
     * introduce	系列课介绍
     * type	系列课类型ID
     * fee	费用价格
     * fs	粉丝数量
     * chatRoomName	直播间名称
     * buyCount	购买人数
     * showDiscount	是否显示优惠码。0和1
     * 0：显示，1：不显示
     * buyUsers	参与购买人信息列表
     * chatInfos	课程列表，请参照37.5接口输出参数：
     * chatInfoCount	总课程数量
     * isSelf	是否是自己的系列课。0和1
     * 0：不是，1：是
     * isBuy	是否购买。0和1
     * 0：否，1：已购买
     * typeName	系列课类型名称
     * shareUrl	分享链接
     */

    @SerializedName("pent") private           int                   pent;            //分销比例
    @SerializedName("isCollect") private      int                   isCollect;       //是否收藏
    @SerializedName("buyCount") private       String                buyCount;
    @SerializedName("chatInfoCount") private  String                chatInfoCount;
    @SerializedName("chatRoom") private       String                chatRoom;
    @SerializedName("chatRoomName") private   String                chatRoomName;
    @SerializedName("fee") private            String                fee;
    @SerializedName("headpic") private        String                headpic;
    @SerializedName("anchorPic") private      String                anchorPic;
    @SerializedName("id") private             String                id;
    @SerializedName("introduce") private      String                introduce;
    @SerializedName("isBuy") private          String                isBuy;
    @SerializedName("isSelf") private         String                isSelf;
    @SerializedName("seriesname") private     String                seriesname;
    @SerializedName("showDiscount") private   String                showDiscount;
    @SerializedName("type") private           String                type;
    @SerializedName("typeName") private       String                typeName;
    @SerializedName("shareUrl") private       String                shareUrl;
    @SerializedName("chatInfoTypeId") private String                chatInfoTypeId;
    @SerializedName("userName") private       String                userName;
    @SerializedName("buyUsers") private       List<PersonCountBean> buyUsers;
    @SerializedName("chatInfos") private      List<ChatInfosBean>   chatInfos;
    @SerializedName("isGroupUser") private    String                isGroupUser;  // 1是圈内用户    0不是
    @SerializedName("isAuditions") private    String                isAuditions;   //0是不开启 1是开启第一节课免费   2是开启邀请免费制度  see SeriesActivity.AUDITION_INVITE_TYPE

    private    int                              userInvitationAllShareNum;                // 邀请过的用户数量
    private    int                              joinType;                 //0:普通成员（包括没报名的），1:管理员，2:讲师，3:创建者
    private    int                              userInvitationSharNum;     // 分享过的次数
    private    int                              invitationFreeNum;         // 需邀请的人数
    private    List<PersonCountBean>            invitationBySharUser;   // 邀请过的用户 头像集合
    private    List<PersonCountBean>            umVoList;               // 分享达人 头像集合

    //判断是否达成邀请次数
    public boolean isFinishInvite(){
        return userInvitationSharNum >= invitationFreeNum;
    }

    public int getJoinType() {
        return joinType;
    }

    public void setJoinType(int joinType) {
        this.joinType = joinType;
    }

    public int getUserInvitationAllShareNum() {
        return userInvitationAllShareNum;
    }

    public void setUserInvitationAllShareNum(int userInvitationAllShareNum) {
        this.userInvitationAllShareNum = userInvitationAllShareNum;
    }

    public int getInvitationFreeNum() {
        return invitationFreeNum;
    }

    public void setInvitationFreeNum(int invitationFreeNum) {
        this.invitationFreeNum = invitationFreeNum;
    }

    public int getUserInvitationSharNum() {
        return userInvitationSharNum;
    }

    public void setUserInvitationSharNum(int userInvitationSharNum) {
        this.userInvitationSharNum = userInvitationSharNum;
    }

    public List<PersonCountBean> getInvitationBySharUser() {
        return invitationBySharUser;
    }

    public void setInvitationBySharUser(List<PersonCountBean> invitationBySharUser) {
        this.invitationBySharUser = invitationBySharUser;
    }

    public List<PersonCountBean> getUmVoList() {
        return umVoList;
    }

    public void setUmVoList(List<PersonCountBean> umVoList) {
        this.umVoList = umVoList;
    }

    public String getIsAuditions() {
        return isAuditions;
    }

    public void setIsAuditions(String isAuditions) {
        this.isAuditions = isAuditions;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIsGroupUser() {
        return isGroupUser;
    }

    public void setIsGroupUser(String isGroupUser) {
        this.isGroupUser = isGroupUser;
    }

    public int getPent() {
        return pent;
    }

    public void setPent(int pent) {
        this.pent = pent;
    }

    public boolean isRemove = false;

    public String getChatInfoTypeId() {
        return chatInfoTypeId;
    }

    public void setChatInfoTypeId(String chatInfoTypeId) {
        this.chatInfoTypeId = chatInfoTypeId;
    }

    public int getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(int isCollect) {
        this.isCollect = isCollect;
    }

    public String getAnchorPic() {
        return anchorPic;
    }

    public void setAnchorPic(String anchorPic) {
        this.anchorPic = anchorPic;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getBuyCount() { return buyCount;}

    public void setBuyCount(String buyCount) { this.buyCount = buyCount;}

    public String getChatInfoCount() { return chatInfoCount;}

    public void setChatInfoCount(String chatInfoCount) { this.chatInfoCount = chatInfoCount;}

    public String getChatRoom() { return chatRoom;}

    public void setChatRoom(String chatRoom) { this.chatRoom = chatRoom;}

    public String getChatRoomName() { return chatRoomName;}

    public void setChatRoomName(String chatRoomName) { this.chatRoomName = chatRoomName;}


    public String getFee() { return fee;}

    public void setFee(String fee) { this.fee = fee;}

    public String getHeadpic() { return headpic;}

    public void setHeadpic(String headpic) { this.headpic = headpic;}

    public String getId() { return id;}

    public void setId(String id) { this.id = id;}

    public String getIntroduce() { return introduce;}

    public void setIntroduce(String introduce) { this.introduce = introduce;}

    public String getIsBuy() { return isBuy;}

    public void setIsBuy(String isBuy) { this.isBuy = isBuy;}


    public boolean bIsBuy() {
        return isBuy.equals("1");

    }

    public String getIsSelf() { return isSelf;}

    public void setIsSelf(String isSelf) { this.isSelf = isSelf;}


    public boolean bIsSelf() {
        return isSelf.equals("1");
    }

    public String getSeriesname() { return seriesname;}

    public void setSeriesname(String seriesname) { this.seriesname = seriesname;}

    public String getShowDiscount() { return showDiscount;}

    public void setShowDiscount(String showDiscount) { this.showDiscount = showDiscount;}

    public String getType() { return type;}

    public void setType(String type) { this.type = type;}

    public List<PersonCountBean> getBuyUsers() { return buyUsers;}

    public void setBuyUsers(List<PersonCountBean> buyUsers) { this.buyUsers = buyUsers;}

    public List<ChatInfosBean> getChatInfos() { return chatInfos;}

    public void setChatInfos(List<ChatInfosBean> chatInfos) { this.chatInfos = chatInfos;}


    @Override
    public String getBuyPrice() {
        return getFee();
    }

    @Override
    public String getBuyFee() {
        return getFee();
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
