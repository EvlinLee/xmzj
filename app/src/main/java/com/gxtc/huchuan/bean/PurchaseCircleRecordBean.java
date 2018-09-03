package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Describe:
 * Created by ALing on 2017/5/22 .
 */

public class PurchaseCircleRecordBean implements Serializable {

    /**
     * createtime : 1493886514000
     * endtime : 1525422500000
     * fee : 599.00
     * groupCover : http://xmzjvip.b0.upaiyun.com/xmzj/67291493102468604.png
     * groupId : 1
     * groupName : 圈子测试a
     * isPay : 1
     * isSett : 1
     * midFee : 10.00
     * orderId : 123456
     * payType : 2
     * saleFee : 10.00
     * signuptime : 1493886514000
     */

    private String createtime;
    private String endtime;
    private String fee;
    private String groupCover;
    private String groupId;
    private String groupName;
    private String isPay;
    private String isSett;
    private String midFee;
    private String orderId;
    private String payType;
    private String saleFee;
    private String signuptime;
    private String sellerCode;
    private String isRefund;//0、未申请退款，1：审核中，2：完成，3：被拒
    private String sellerName;
    private String buyerCode;
    private String buyerName;

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getBuyerCode() {
        return buyerCode;
    }

    public void setBuyerCode(String buyerCode) {
        this.buyerCode = buyerCode;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getSellerCode() {
        return sellerCode;
    }

    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    public String getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(String isRefund) {
        this.isRefund = isRefund;
    }

    public String getCreatetime() { return createtime;}

    public void setCreatetime(String createtime) { this.createtime = createtime;}

    public String getEndtime() { return endtime;}

    public void setEndtime(String endtime) { this.endtime = endtime;}

    public String getFee() { return fee;}

    public void setFee(String fee) { this.fee = fee;}

    public String getGroupCover() { return groupCover;}

    public void setGroupCover(String groupCover) { this.groupCover = groupCover;}

    public String getGroupId() { return groupId;}

    public void setGroupId(String groupId) { this.groupId = groupId;}

    public String getGroupName() { return groupName;}

    public void setGroupName(String groupName) { this.groupName = groupName;}

    public String getIsPay() { return isPay;}

    public void setIsPay(String isPay) { this.isPay = isPay;}

    public String getIsSett() { return isSett;}

    public void setIsSett(String isSett) { this.isSett = isSett;}

    public String getMidFee() { return midFee;}

    public void setMidFee(String midFee) { this.midFee = midFee;}

    public String getOrderId() { return orderId;}

    public void setOrderId(String orderId) { this.orderId = orderId;}

    public String getPayType() { return payType;}

    public void setPayType(String payType) { this.payType = payType;}

    public String getSaleFee() { return saleFee;}

    public void setSaleFee(String saleFee) { this.saleFee = saleFee;}

    public String getSignuptime() { return signuptime;}

    public void setSignuptime(String signuptime) { this.signuptime = signuptime;}
}
