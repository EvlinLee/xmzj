package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/8/31.
 */

public class WithdrawRecordBean implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * createTime : 1499220490000
     * id : 31
     * money : 50.00
     * openingBank :
     * userAccount : 1092723495@qq.com
     * userName : ，
     * withdrawCashType : 2
     * withdrawStatus : 2
     */

    private String createTime;
    private String id;
    private double money;
    private String openingBank;
    private String userAccount;
    private String userName;
    private int withdrawCashType;
    private int withdrawStatus;

    public String getCreateTime() { return createTime;}

    public void setCreateTime(String createTime) { this.createTime = createTime;}

    public String getId() { return id;}

    public void setId(String id) { this.id = id;}

    public double getMoney() { return money;}

    public void setMoney(double money) { this.money = money;}

    public String getOpeningBank() { return openingBank;}

    public void setOpeningBank(String openingBank) { this.openingBank = openingBank;}

    public String getUserAccount() { return userAccount;}

    public void setUserAccount(String userAccount) { this.userAccount = userAccount;}

    public String getUserName() { return userName;}

    public void setUserName(String userName) { this.userName = userName;}

    public int getWithdrawCashType() { return withdrawCashType;}

    public void setWithdrawCashType(int withdrawCashType) { this.withdrawCashType = withdrawCashType;}

    public int getWithdrawStatus() { return withdrawStatus;}

    public void setWithdrawStatus(int withdrawStatus) { this.withdrawStatus = withdrawStatus;}
}
