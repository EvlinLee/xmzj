package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Created by zzg on 2017/8/25.
 */

public class CheckBean implements Serializable {

    String content;
    String createTime;
    String linkId;
    String linkType;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }
}
