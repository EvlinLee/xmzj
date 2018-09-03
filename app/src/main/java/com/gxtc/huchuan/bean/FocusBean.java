package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Describe: 关注的人
 * Created by ALing on 2017/3/13 0013.
 */

public class FocusBean implements Serializable {

    private static final long serialVersionUID = -5703419112100471444L;
    /**
     * followCount : 6
     * introduction :
     * isFollow : 1
     * newsTime : 1492840976000
     * newsTitle : <视频一>《财神大咖会-微信流量与变现》2017年南宁总部线下讨论会回顾
     * selfMediaName : Wwe
     * type : 3
     * userCode : 452303476
     * userHeadPic : http://xmzjvip.b0.upaiyun.com/xmzj/27711492681090919.jpeg
     * userName : 378602
     */

    private String followCount;
    private String message;
    private String introduction;
    private String isFollow;        //是否关注，0：未关注；1：已关注
    private String newsTime;
    private String newsTitle;
    private String type;
    private String joinTime;
    private String userPic;
    private String userCode;
    private String userHeadPic;
    private String userName;
    private String isMyFriend ;// 1已添加  0未添加
    private String isGroupMember;  //0、不是；1、是

    private boolean isSelect;

    private int position;//用于记录添加新的朋友的索引

    public String getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIsMyFriend() {
        return isMyFriend;
    }

    public void setIsMyFriend(String isMyFriend) {
        this.isMyFriend = isMyFriend;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getIsGroupMember() {
        return isGroupMember;
    }

    public void setIsGroupMember(String isGroupMember) {
        this.isGroupMember = isGroupMember;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getFollowCount() {
        return followCount;
    }

    public void setFollowCount(String followCount) {
        this.followCount = followCount;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(String isFollow) {
        this.isFollow = isFollow;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public void setNewsTime(String newsTime) {
        this.newsTime = newsTime;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserHeadPic() {
        return userHeadPic;
    }

    public void setUserHeadPic(String userHeadPic) {
        this.userHeadPic = userHeadPic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FocusBean focusBean = (FocusBean) o;

        return userCode.equals(focusBean.userCode);

    }

}
