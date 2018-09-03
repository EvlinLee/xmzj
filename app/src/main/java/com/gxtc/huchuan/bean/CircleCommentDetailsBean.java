package com.gxtc.huchuan.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sjr on 2017/5/18.
 */

public class CircleCommentDetailsBean implements Serializable {

    private static final long serialVersionUID = -763618247835550322L;


    /**
     * content : 好啊
     * createTime : 1495105694141
     * groupPicVos : []
     * id : 101
     * targetUserCode : 273683401
     * targetUserName : 财神
     * targetUserPic : http://xmzjvip.b0.upaiyun.com/xmzj/1489585684582
     * userCode : 678046738
     * userName : 老实任
     * userPic : https://xmzjvip.b0.upaiyun.com/xmzj/45071494469955427.jpg
     */

    private String content;
    private long createTime;
    private int id;
    private String targetUserCode;
    private String targetUserName;
    private String targetUserPic;
    private String userCode;
    private String userName;
    private String userPic;
    private List<?> groupPicVos;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public List<?> getGroupPicVos() {
        return groupPicVos;
    }

    public void setGroupPicVos(List<?> groupPicVos) {
        this.groupPicVos = groupPicVos;
    }
}
