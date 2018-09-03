package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Created by Steven on 17/2/16.
 */

public class DealListBean implements Serializable{

    private static final long serialVersionUID = 1L;

    private String id;              //交易信息ID
    private String userId;          //发布人ID
    private String userName;        //发布人名称
    private String userPic;         //发布人头像
    private String liuYan;          //留言数量
    private String tradeTypeSonId;  //交易分类ID
    private String tradeTypeSonName;//交易分类
    private String tradeType;       //0：出售，1：求购(必选)
    private String title;
    private String createTime;      //发布时间
    private String isTop;           //是否置顶(0、否；1、是)
    private String isfinish;        //0：未完成，1，交易中，2：完成
    private String read;            //阅读量
    private String anonymous = "";      //是否匿名
    private String picUrl ;         //封面图片
    private String pattern ;         //模式。 0、交易；1、论坛
    private String workOff ;         //已售数量
    private String remainNum ;         //剩余数量
    private String isRecommendEntry ; //0、普通推荐；1、优质推荐

    public String getIsRecommendEntry() {
        return isRecommendEntry;
    }

    public void setIsRecommendEntry(String isRecommendEntry) {
        this.isRecommendEntry = isRecommendEntry;
    }

    public String getWorkOff() {
        return workOff;
    }

    public void setWorkOff(String workOff) {
        this.workOff = workOff;
    }

    public String getRemainNum() {
        return remainNum;
    }

    public void setRemainNum(String remainNum) {
        this.remainNum = remainNum;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public DealListBean() {
    }

    public String getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(String anonymous) {
        this.anonymous = anonymous;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

    public String getLiuYan() {
        return liuYan;
    }

    public void setLiuYan(String liuYan) {
        this.liuYan = liuYan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTradeTypeSonId() {
        return tradeTypeSonId;
    }

    public void setTradeTypeSonId(String tradeTypeSonId) {
        this.tradeTypeSonId = tradeTypeSonId;
    }

    public String getTradeTypeSonName() {
        return tradeTypeSonName;
    }

    public void setTradeTypeSonName(String tradeTypeSonName) {
        this.tradeTypeSonName = tradeTypeSonName;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getIsTop() {
        return isTop;
    }

    public void setIsTop(String isTop) {
        this.isTop = isTop;
    }

    public String getIsfinish() {
        return isfinish;
    }

    public void setIsfinish(String isfinish) {
        this.isfinish = isfinish;
    }
}
