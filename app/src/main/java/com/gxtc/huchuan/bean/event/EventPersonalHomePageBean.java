package com.gxtc.huchuan.bean.event;

/**
 * Describe:
 * Created by ALing on 2017/4/10 .
 */

public class EventPersonalHomePageBean {
    private String userCode;

    public EventPersonalHomePageBean(String userCode) {
        this.userCode = userCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
