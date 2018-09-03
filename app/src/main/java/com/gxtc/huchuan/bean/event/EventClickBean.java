package com.gxtc.huchuan.bean.event;

import com.gxtc.huchuan.bean.PurchaseListBean;

/**
 * Created by Steven on 17/4/6.
 */

public class EventClickBean {

    public String action ;
    public Object bean;

    public EventClickBean(String action, Object bean) {
        this.action = action;
        this.bean = bean;
    }
}
