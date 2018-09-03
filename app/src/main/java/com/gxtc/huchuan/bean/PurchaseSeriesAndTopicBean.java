package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Describe:话题和系列课的订单列表
 * Created by ALing on 2017/6/2 .
 */

public class PurchaseSeriesAndTopicBean implements Serializable{

    /**
     * bizId : 21
     * chatRoomName : 一号
     * createtime : 1493815422000
     * facePic : http://xmzjvip.b0.upaiyun.com/xmzj/27731490780153333.png
     * fee : 1.0
     * headPic : http://xmzjvip.b0.upaiyun.com/xmzj/65861492152072526.jpg
     * isPay : 1
     * isSett : 1
     * midFee : 0.0
     * name : aaaaa
     * orderId : CI20170503193150161940
     * saleFee : 0.02
     * title : 二号话题
     * type : chatInfo
     * userCode : 128399864
     */

    private String bizId;
    private String chatRoomName;
    private String createtime;
    private String facePic;
    private String fee;
    private String headPic;
    private String isPay;
    private String isSett;
    private String midFee;
    private String name;
    private String orderId;
    private String saleFee;
    private String title;
    private String type;
    private String userCode;
    private String sellerCode;
    private String chatCreateMan;
    private String isRefund;//0、未申请退款，1：审核中，2：完成，3：被拒

    public String getChatCreateMan() {
        return chatCreateMan;
    }

    public void setChatCreateMan(String chatCreateMan) {
        this.chatCreateMan = chatCreateMan;
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

    public String getBizId() { return bizId;}

    public void setBizId(String bizId) { this.bizId = bizId;}

    public String getChatRoomName() { return chatRoomName;}

    public void setChatRoomName(String chatRoomName) { this.chatRoomName = chatRoomName;}

    public String getCreatetime() { return createtime;}

    public void setCreatetime(String createtime) { this.createtime = createtime;}

    public String getFacePic() { return facePic;}

    public void setFacePic(String facePic) { this.facePic = facePic;}

    public String getFee() { return fee;}

    public void setFee(String fee) { this.fee = fee;}

    public String getHeadPic() { return headPic;}

    public void setHeadPic(String headPic) { this.headPic = headPic;}

    public String getIsPay() { return isPay;}

    public void setIsPay(String isPay) { this.isPay = isPay;}

    public String getIsSett() { return isSett;}

    public void setIsSett(String isSett) { this.isSett = isSett;}

    public String getMidFee() { return midFee;}

    public void setMidFee(String midFee) { this.midFee = midFee;}

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getOrderId() { return orderId;}

    public void setOrderId(String orderId) { this.orderId = orderId;}

    public String getSaleFee() { return saleFee;}

    public void setSaleFee(String saleFee) { this.saleFee = saleFee;}

    public String getTitle() { return title;}

    public void setTitle(String title) { this.title = title;}

    public String getType() { return type;}

    public void setType(String type) { this.type = type;}

    public String getUserCode() { return userCode;}

    public void setUserCode(String userCode) { this.userCode = userCode;}
}
