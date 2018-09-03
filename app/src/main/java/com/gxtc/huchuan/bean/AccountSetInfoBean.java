package com.gxtc.huchuan.bean;

import com.google.gson.annotations.SerializedName;
import com.gxtc.huchuan.bean.pay.AccountSet;

import java.util.List;

/**
 * Created by Gubr on 2017/5/4.
 */

public class AccountSetInfoBean {
    /**
     * userPercent : 0
     * accountSet : []
     */

    @SerializedName("userPercent") private String           userPercent;
    @SerializedName("accountSet") private  List<AccountSet> accountSet;

    public String getUserPercent() { return userPercent;}

    public void setUserPercent(String userPercent) { this.userPercent = userPercent;}

    public List<AccountSet> getAccountSet() { return accountSet;}

    public void setAccountSet(List<AccountSet> accountSet) { this.accountSet = accountSet;}
}
