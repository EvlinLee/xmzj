package com.gxtc.huchuan.bean.event;

/**
 * Describe:
 * Created by ALing on 2017/5/9 .
 */

public class EventGroupBean {
    public String targetId;
    public boolean isDelete;

    public EventGroupBean(String targetId, boolean isDelete) {
        this.targetId = targetId;
        this.isDelete = isDelete;
    }
}
