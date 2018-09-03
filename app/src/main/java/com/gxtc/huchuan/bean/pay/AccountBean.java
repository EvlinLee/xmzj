package com.gxtc.huchuan.bean.pay;

import java.io.Serializable;

/**
 * Created by sjr on 2017/4/20.
 * 账户流水
 */

public class AccountBean implements Serializable{


    private String id;
    private String openingBank;
    private String userAccount;
    private String userName;
    private String withdrawCashType; //账户类型1：微信；2：支付宝；3：银行卡

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpeningBank() {
        return openingBank;
    }

    public void setOpeningBank(String openingBank) {
        this.openingBank = openingBank;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWithdrawCashType() {
        return withdrawCashType;
    }

    public void setWithdrawCashType(String withdrawCashType) {
        this.withdrawCashType = withdrawCashType;
    }
}
