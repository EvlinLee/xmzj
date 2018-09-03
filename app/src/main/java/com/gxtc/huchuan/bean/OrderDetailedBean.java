package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Created by Steven on 17/4/6.
 */

public class OrderDetailedBean implements Serializable {


    /**
     * addr :
     * buyer : 0
     * createTime : 1491382194000
     * fbUserId : 3
     * fbUserName : 测试
     * id : 2
     * isFinish : 0
     * orderId : 20170405164953866482
     * tradeAmt : 0.1
     * tradeInfoId : 1
     * tradeInfoPic : http://xmzjvip.b0.upaiyun.com/xmzj/67181489562928458.png
     * tradeInfoTitle : 交易测试
     */

    private String addr;
    private int buyer;
    private long createTime;
    private long endTime;          //订单到期时间
    private int fbUserId;
    private String fbUserName;
    private int id;
    private int isFinish;          //订单状态。0:卖家未同意，1：卖家同意交易，2：买家支付，3：卖家交付，4：交易完成，5：交易关闭,10：卖家不同意交易
    private int isRefund;          //0：正常，1：退款中，2：已退款
    private int tradeInfoId;
    private String orderId;
    private double tradeAmt;
    private String tradeInfoPic;
    private String tradeInfoTitle;

    private String name;            //收货人
    private String phone;           //收货人联系电话
    private String unagremark;      //卖家不同意原因
    private String message;         //买家留言
    private double dbFee;           //担保费
    private double discount;        //会员折扣
    private String money;
    private String remark;
    private String buyWay;          //0：买家承担担保费；1：卖家承担担保费
    private String isAppointTr;     //是否是快速交易  1是快速交易

    public String getIsAppointTr() {
        return isAppointTr;
    }

    public void setIsAppointTr(String isAppointTr) {
        this.isAppointTr = isAppointTr;
    }

    public String getBuyWay() {
        return buyWay;
    }

    public void setBuyWay(String buyWay) {
        this.buyWay = buyWay;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public double getDbFee() {
        return dbFee;
    }

    public void setDbFee(double dbFee) {
        this.dbFee = dbFee;
    }

    private String userCode;

    public int getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(int isRefund) {
        this.isRefund = isRefund;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUnagremark() {
        return unagremark;
    }

    public void setUnagremark(String unagremark) {
        this.unagremark = unagremark;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public int getBuyer() {
        return buyer;
    }

    public void setBuyer(int buyer) {
        this.buyer = buyer;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getFbUserId() {
        return fbUserId;
    }

    public void setFbUserId(int fbUserId) {
        this.fbUserId = fbUserId;
    }

    public String getFbUserName() {
        return fbUserName;
    }

    public void setFbUserName(String fbUserName) {
        this.fbUserName = fbUserName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(int isFinish) {
        this.isFinish = isFinish;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(double tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public int getTradeInfoId() {
        return tradeInfoId;
    }

    public void setTradeInfoId(int tradeInfoId) {
        this.tradeInfoId = tradeInfoId;
    }

    public String getTradeInfoPic() {
        return tradeInfoPic;
    }

    public void setTradeInfoPic(String tradeInfoPic) {
        this.tradeInfoPic = tradeInfoPic;
    }

    public String getTradeInfoTitle() {
        return tradeInfoTitle;
    }

    public void setTradeInfoTitle(String tradeInfoTitle) {
        this.tradeInfoTitle = tradeInfoTitle;
    }
}

