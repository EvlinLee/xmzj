package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Created by 宋家任 on 2017/5/17.
 * 分享赚钱
 */

public class ShareMakeMoneyBean implements Serializable {

    private static final long serialVersionUID = -763618247835550322L;


    /**
     * chatRoomName : 财神大咖
     * commission : 3.00
     * facePic :
     * fee : 10.0
     * groupUrl :
     * id : 331
     * isForGrop : 0
     * joinCount : 2
     * joinGroup : 0
     * pent : 30
     * shareUrl : http://app.xinmei6.com/html/classroom.html?chatInfoId=331&type=1&userCode=678046738
     * title : 是啊我们是在做着同样的事情
     * type : chatInfo
     * groupDesc : 百家号交流对接群
     * groupInfoCount : 0
     */

    private String chatRoomName;
    private String commission;
    private String facePic;
    private String fee;
    private String groupUrl;
    private String id;
    private String isForGrop;
    private String joinCount;
    private String joinGroup;
    private String pent;
    private String shareUrl;
    private String title;
    private String type;
    private String groupDesc;
    private String groupInfoCount;

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getFacePic() {
        return facePic;
    }

    public void setFacePic(String facePic) {
        this.facePic = facePic;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getGroupUrl() {
        return groupUrl;
    }

    public void setGroupUrl(String groupUrl) {
        this.groupUrl = groupUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsForGrop() {
        return isForGrop;
    }

    public void setIsForGrop(String isForGrop) {
        this.isForGrop = isForGrop;
    }

    public String getJoinCount() {
        return joinCount;
    }

    public void setJoinCount(String joinCount) {
        this.joinCount = joinCount;
    }

    public String getJoinGroup() {
        return joinGroup;
    }

    public void setJoinGroup(String joinGroup) {
        this.joinGroup = joinGroup;
    }

    public String getPent() {
        return pent;
    }

    public void setPent(String pent) {
        this.pent = pent;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    public String getGroupInfoCount() {
        return groupInfoCount;
    }

    public void setGroupInfoCount(String groupInfoCount) {
        this.groupInfoCount = groupInfoCount;
    }
}
