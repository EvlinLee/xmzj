package com.gxtc.huchuan.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gubr on 2017/5/17.
 */

public class CircleCommentBean implements Serializable {

    /**
     * id	评论id
     * userCode	用户编码
     * userName	用户名
     * userPic	用户头像
     * targetUserCode	回复对象用户编码
     * targetUserName	回复对象用户名
     * targetUserPic	回复对象用户头像
     * content	评论内容
     * createTime	评论时间
     * groupPicVos	评论图片列表
     * id	评论图片id
     * picUrl	评论图片地址
     */

    @SerializedName("content") private     String  content;
    @SerializedName("createTime") private  long    createTime;
    @SerializedName("id") private          int     id;
    @SerializedName("userCode") private    String  userCode;
    @SerializedName("userName") private    String  userName;
    @SerializedName("userPic") private     String  userPic;
    @SerializedName( "targetUserCode")private String  targetUserCode;
    @SerializedName( "targetUserName")private String  targetUserName;
    @SerializedName( "targetUserPic")private  String  targetUserPic;
    @SerializedName("groupPicVos") private    List<?> groupPicVos;
    @SerializedName("replyVo") private        List<CircleCommentBean> replyVo;

    public String getContent() { return content;}

    public void setContent(String content) { this.content = content;}

    public long getCreateTime() { return createTime;}

    public void setCreateTime(long createTime) { this.createTime = createTime;}

    public int getId() { return id;}

    public void setId(int id) { this.id = id;}

    public String getUserCode() { return userCode;}

    public void setUserCode(String userCode) { this.userCode = userCode;}

    public String getUserName() { return userName;}

    public void setUserName(String userName) { this.userName = userName;}

    public String getUserPic() { return userPic;}

    public void setUserPic(String userPic) { this.userPic = userPic;}

    public List<?> getGroupPicVos() { return groupPicVos;}

    public void setGroupPicVos(List<?> groupPicVos) { this.groupPicVos = groupPicVos;}

    public List<CircleCommentBean> getReplyVo() { return replyVo;}

    public void setReplyVo(List<CircleCommentBean> replyVo) { this.replyVo = replyVo;}

    public String getTargetUserCode() {
        return targetUserCode;
    }

    public void setTargetUserCode(String targetUserCode) {
        this.targetUserCode = targetUserCode;
    }

    public String getTargetUserName() {
        return targetUserName;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUserName = targetUserName;
    }

    public String getTargetUserPic() {
        return targetUserPic;
    }

    public void setTargetUserPic(String targetUserPic) {
        this.targetUserPic = targetUserPic;
    }

    @Override
    public String toString() {
        return "CircleCommentBean{" + "content='" + content + '\'' + ", createTime=" + createTime + ", id=" + id + ", userCode='" + userCode + '\'' + ", userName='" + userName + '\'' + ", userPic='" + userPic + '\'' + ", targetUserCode='" + targetUserCode + '\'' + ", targetUserName='" + targetUserName + '\'' + ", targetUserPic='" + targetUserPic + '\'' + ", groupPicVos=" + groupPicVos + ", replyVo=" + replyVo + '}';
    }
}
