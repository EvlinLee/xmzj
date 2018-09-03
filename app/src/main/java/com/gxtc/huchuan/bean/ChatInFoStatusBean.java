package com.gxtc.huchuan.bean;

/**
 * Created by Gubr on 2017/5/3.
 * 151.	获取课程的禁言等状态接口
 */

public class ChatInFoStatusBean {

    /**
     * isBanned	是否禁言。0：否，1：是
     * isBlock	是否封禁。0：否，1：是
     * isBlack	是否黑名单。0：否，1：是
     */

    private String isBlock;
    private String isBlack;
    private String isBanned;

    public String getIsBlock() { return isBlock;}

    public void setIsBlock(String isBlock) { this.isBlock = isBlock;}

    public String getIsBlack() { return isBlack;}

    public void setIsBlack(String isBlack) { this.isBlack = isBlack;}

    public String getIsBanned() { return isBanned;}

    public void setIsBanned(String isBanned) { this.isBanned = isBanned;}
}
