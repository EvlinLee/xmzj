package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Created by sjr on 2017/4/20.
 * 账户流水
 */

public class AccountWaterBean implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * balance : 0.00
     * createTime : 1492657523000
     * orderId :
     * streamMoney : 10.00
     * title : 担保交易流水，金额10.00
     * type : 0
     */

    private String balance;
    private String createTime;
    private String orderId;
    private String streamMoney;
    private String title;
    private String type;
    private String businessName;
    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStreamMoney() {
        return streamMoney;
    }

    public void setStreamMoney(String streamMoney) {
        this.streamMoney = streamMoney;
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
