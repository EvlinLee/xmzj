package com.gxtc.huchuan.bean.event;

import android.view.View;

import com.gxtc.huchuan.bean.CopywritingBean;

/**
 * Created by Steven on 17/3/17.
 */

public class EventCopyBean {

    public CopywritingBean bean;

    public View view;

    public EventCopyBean(CopywritingBean bean, View view) {
        this.bean = bean;
        this.view = view;
    }
}
