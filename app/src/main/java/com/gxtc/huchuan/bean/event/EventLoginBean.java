package com.gxtc.huchuan.bean.event;

import java.util.Map;

public class EventLoginBean {

    public static final int EXIT = 0;      //退出登录事件
    public static final int LOGIN = 1;          //登录事件
    public static final int THIRDLOGIN = 2;     //第三方登录
    public static final int REGISTE = 3;
    public static final int TOKEN_OVERDUCE = 4; //token过期，多设备登录
    public Map<String,String> datas;               //保存第三方的用户数据以便通过接口保存到我们的服务器（在用第三方注册时用）
    private String thirdType = null;     //三方登录类型  1：qq      2.weixin        3.sina
    private String uniqueKey =null;     //第三方登录唯一标识码

    public int status;

    public EventLoginBean(int status,String thirdType,String uniqueKey){
        this.thirdType = thirdType;
        this.status = status;
        this.uniqueKey = uniqueKey;

    }

    public EventLoginBean(int status,String thirdType,String uniqueKey,Map<String,String> datas){
        this.thirdType = thirdType;
        this.status = status;
        this.uniqueKey = uniqueKey;
        this.datas = datas;
    }

    public EventLoginBean(int status) {
        this.status = status;
    }

    public String getThirdType() {
        return thirdType;
    }

    public void setThirdType(String thirdType) {
        this.thirdType = thirdType;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}