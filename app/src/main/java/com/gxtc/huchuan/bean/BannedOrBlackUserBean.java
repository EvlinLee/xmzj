package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Created by sjr on 2017/3/21.
 * 课堂禁言和黑名单实体bean
 */

public class BannedOrBlackUserBean implements Serializable {

    private static final long serialVersionUID = -763328247878760552L;


    /**
     * chatRoomId : 8
     * createTime : 1491010322000
     * dr : 0
     * headPic :
     * name : 962776
     * type : 1
     * userCode : 430813565
     */

    private String chatRoomId;
    private String createTime;
    private String dr;
    private String headPic;
    private String name;
    private String type;
    private String userCode;

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDr() {
        return dr;
    }

    public void setDr(String dr) {
        this.dr = dr;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
