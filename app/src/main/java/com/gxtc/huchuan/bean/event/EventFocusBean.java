package com.gxtc.huchuan.bean.event;

/**
 * Describe:
 * Created by ALing on 2017/5/15 0015.
 */

public class EventFocusBean {
    //true  已关注   false  未关注
    public boolean isFocus;
    public String userCode;

    public EventFocusBean(boolean isFocus, String userCode) {
        this.isFocus = isFocus;
        this.userCode = userCode;
    }

    public EventFocusBean(boolean isFocus) {
        this.isFocus = isFocus;
    }

    public boolean isFocus() {
        return isFocus;
    }

    public void setFocus(boolean focus) {
        isFocus = focus;
    }
}
