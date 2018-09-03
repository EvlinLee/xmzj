package com.gxtc.huchuan.bean.event;

/**
 * Describe:
 * Created by ALing on 2017/6/5.
 */

public class EventRemarkPersonalInfo {
    private String des;
    private String remarkName;

    public EventRemarkPersonalInfo() {
    }
    public EventRemarkPersonalInfo(String des, String remarkName) {
        this.des = des;
        this.remarkName = remarkName;
    }

    public String getDes() {

        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getRemarkName() {
        return remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }
}
