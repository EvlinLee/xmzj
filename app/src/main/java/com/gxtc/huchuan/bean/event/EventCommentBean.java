package com.gxtc.huchuan.bean.event;

/**
 * Created by Steven on 17/3/30.
 */

public class EventCommentBean {

    public String id;
    public String name;
    public String isSelf;      //是否可回复(0、不可回复；1、可回复)
    public String targetUserId;

    public EventCommentBean(String id, String name, String isSelf,String targetUserId) {
        this.id = id;
        this.name = name;
        this.isSelf = isSelf;
        this.targetUserId = targetUserId;
    }
}

