package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Created by sjr on 2017/4/10.
 */

public class UploadPPTFileBean implements Serializable {
    private static final long serialVersionUID = 2L;


    /**
     * chatInfoId : 9999
     * createTime : 1491807186336
     * picUrl : /storage/emulated/0/DCIM/100ANDRO/DSC_0080.JPG
     */

    private String chatInfoId;
    private String createTime;
    private String picUrl;
    private String isClick;

    public String getIsClick() {
        return isClick;
    }

    public void setIsClick(String isClick) {
        this.isClick = isClick;
    }

    public String getChatInfoId() {
        return chatInfoId;
    }

    public void setChatInfoId(String chatInfoId) {
        this.chatInfoId = chatInfoId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
