package com.gxtc.huchuan.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Gubr on 2017/5/17.
 */

public class ThumbsupVosBean implements Serializable {

    /**
     * groupInfoId : 44
     * id : 874
     * userCode : 571904840
     * userName : 472823
     */

    @SerializedName("groupInfoId") private int groupInfoId;
    @SerializedName("id") private       int    id;
    @SerializedName("userCode") private String userCode;
    @SerializedName("userName") private String userName;
    @SerializedName("headPic") private String headPic;

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public int getGroupInfoId() { return groupInfoId;}

    public void setGroupInfoId(int groupInfoId) { this.groupInfoId = groupInfoId;}

    public int getId() { return id;}

    public void setId(int id) { this.id = id;}

    public String getUserCode() { return userCode;}

    public void setUserCode(String userCode) { this.userCode = userCode;}

    public String getUserName() { return userName;}

    public void setUserName(String userName) { this.userName = userName;}

    @Override
    public String toString() {
        return "ThumbsupVosBean{" + "groupInfoId=" + groupInfoId + ", id=" + id + ", userCode='" + userCode + '\'' + ", userName='" + userName + '\'' + '}';
    }
}
