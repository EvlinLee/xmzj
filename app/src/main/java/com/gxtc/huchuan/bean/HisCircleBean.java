package com.gxtc.huchuan.bean;

/**
 * Created by Administrator on 2017/5/2.
 */

public class HisCircleBean {


    /**
     * attention : 1
     * content : 哈哈
     * groupName : 看看
     * id : 23
     * infoNum : 0
     * isMy : 1
     * cover : 圈子封面
     */

    private int attention;
    private String content;
    private String groupName;
    private int id;
    private int infoNum;
    private int isMy;
    private String cover;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getAttention() {
        return attention;
    }

    public void setAttention(int attention) {
        this.attention = attention;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public int getInfoNum() {
        return infoNum;
    }

    public void setInfoNum(int infoNum) {
        this.infoNum = infoNum;
    }

    public int getIsMy() {
        return isMy;
    }

    public void setIsMy(int isMy) {
        this.isMy = isMy;
    }
}
