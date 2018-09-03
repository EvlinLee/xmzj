package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Created by Gubr on 2017/4/3.
 */

public class SignUpMemberBean implements Serializable{

    /**
     * headPic : http://xmzjvip.b0.upaiyun.com/xmzj/44001489545493826.png
     * name : xmzj
     * userCode : 202366388
     */

    private String headPic;
    private String name;
    private String userCode;
    private String isBlack;
    private String isBlock;
    private String signTime;
    private String joinType;

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public String getIsBlack() {
        return isBlack;
    }

    public void setIsBlack(String isBlack) {
        this.isBlack = isBlack;
    }


    public void changeIsBlack() {
        isBlack = "1".equals(isBlack) ? "0" : "1";
    }

    public void changeIsBlock(){
        isBlock="1".equals(isBlock)?"0":"1";
    }

    public String getIsBlock() {
        return isBlock;
    }

    public void setIsBlock(String isBlock) {
        this.isBlock = isBlock;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }


    public boolean bisBlack() {
        return "1".equals(isBlack);
    }


    public boolean bisBlock() {
        return "0".equals(isBlock);
    }


    @Override
    public String toString() {
        return "SignUpMemberBean{" +
                "headPic='" + headPic + '\'' +
                ", name='" + name + '\'' +
                ", userCode='" + userCode + '\'' +
                ", isBlack='" + isBlack + '\'' +
                ", isBlock='" + isBlock + '\'' +
                '}';
    }
}
