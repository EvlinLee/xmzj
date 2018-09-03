package com.gxtc.huchuan.bean.pay;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Gubr on 2017/5/4.
 */

public class AccountSet implements Serializable {
    /**
     * id : 2
     * openingBank :
     * userAccount : 544944866qq.com
     * userName :
     * withdrawCashType : 2
     */

    @SerializedName("id") private               String id;
    @SerializedName("openingBank") private      String openingBank;
    @SerializedName("userAccount") private      String userAccount;
    @SerializedName("userName") private         String userName;
    @SerializedName("withdrawCashType") private String withdrawCashType;

    public String getId() { return id;}

    public void setId(String id) { this.id = id;}

    public String getOpeningBank() { return openingBank;}

    public void setOpeningBank(String openingBank) { this.openingBank = openingBank;}

    public String getUserAccount() { return userAccount;}

    public void setUserAccount(String userAccount) { this.userAccount = userAccount;}

    public String getUserName() { return userName;}

    public void setUserName(String userName) { this.userName = userName;}

    public String getWithdrawCashType() { return withdrawCashType;}

    public void setWithdrawCashType(
            String withdrawCashType) { this.withdrawCashType = withdrawCashType;}
}
