package com.gxtc.huchuan.bean;

/**
 * Describe:
 * Created by ALing on 2017/5/9 .
 */

public class CircleInfoBean {

    /**
     * content : aa
     * createTime : 1494309922000
     * fee : 0
     * groupId : 26
     * groupName : 流量高手
     * id : 4
     * isFee : 0
     */

    private String content;
    private long createTime;
    private double fee;
    private int groupId;
    private String groupName;
    private int id;
    private int isFee;
    private String title;
    private String cover;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

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

    public double getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsFee() {
        return isFee;
    }

    public void setIsFee(int isFee) {
        this.isFee = isFee;
    }
}
