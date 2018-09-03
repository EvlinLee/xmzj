package com.gxtc.huchuan.bean.event;

/**
 * Created by Steven on 16/12/15.
 */

public class EventPayResultBean {

    public int status;
    public String orderNo;

    public EventPayResultBean(int status) {
        this.status = status;
    }

    public EventPayResultBean(int status, String orderNo) {
        this.status = status;
        this.orderNo = orderNo;
    }
}
