package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Describe:分销
 * Created by ALing on 2017/5/4 .
 */

public class DistributionBean implements Serializable {

    /**
     * buyCount : 1
     * chatRoomName : 一号
     * commission : 0.10
     * commissionIncome : 0.00
     * facePic : http://xmzjvip.b0.upaiyun.com/xmzj/27731490780153333.png
     * fee : 1.0
     * id : 21      课程或圈子ID
     * joinCount : 14
     * pent : 10
     * recommendCount : 1
     * shareUrl : http://app.xinmei6.com/html/classroom.html?chatInfoId=21&type=1&userCode=128399864
     * title : 二号课程
     * type : chatInfo
     */

    private String buyCount;
    private String chatRoomName;
    private String commission;
    private String commissionIncome;
    private String facePic;
    private String fee;
    private String id;
    private String joinCount;
    private String pent;
    private String recommendCount;
    private String shareUrl;
    private String title;
    private String type;

    private String saleFee;
    private String sumFaleFee;
    private String sumFee;
    private String isDr;

    public String getSaleFee() {
        return saleFee;
    }

    public void setSaleFee(String saleFee) {
        this.saleFee = saleFee;
    }

    public String getSumFaleFee() {
        return sumFaleFee;
    }

    public void setSumFaleFee(String sumFaleFee) {
        this.sumFaleFee = sumFaleFee;
    }

    public String getSumFee() {
        return sumFee;
    }

    public void setSumFee(String sumFee) {
        this.sumFee = sumFee;
    }

    public String getIsDr() {
        return isDr;
    }

    public void setIsDr(String isDr) {
        this.isDr = isDr;
    }

    public String getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(String buyCount) {
        this.buyCount = buyCount;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getCommissionIncome() {
        return commissionIncome;
    }

    public void setCommissionIncome(String commissionIncome) {
        this.commissionIncome = commissionIncome;
    }

    public String getFacePic() {
        return facePic;
    }

    public void setFacePic(String facePic) {
        this.facePic = facePic;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJoinCount() {
        return joinCount;
    }

    public void setJoinCount(String joinCount) {
        this.joinCount = joinCount;
    }

    public String getPent() {
        return pent;
    }

    public void setPent(String pent) {
        this.pent = pent;
    }

    public String getRecommendCount() {
        return recommendCount;
    }

    public void setRecommendCount(String recommendCount) {
        this.recommendCount = recommendCount;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
