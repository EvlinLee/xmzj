package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Created by Steven on 16/12/26.
 */

public class ConversationTextBean implements Serializable{

    /**
     * title :  周日闲谈 | 做人呢，不要给自己太大压力
     * url : http://app.xinmei6.com/html/collect.html?id=193&from=
     */
    public String id;
    public String title;
    public String content;
    public String url;

    private String userName;
    private String createTime;
    private String userPic;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
