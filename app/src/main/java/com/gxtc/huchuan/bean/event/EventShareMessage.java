package com.gxtc.huchuan.bean.event;

import java.io.Serializable;

import io.rong.imlib.model.Conversation;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/7/17.
 */

public class EventShareMessage implements Serializable{

    private static final long serialVersionUID = 1L;

    public int shareFlag;
    public String Liuyan;
    public Object mObject;
    public Conversation.ConversationType mType;

    public EventShareMessage(int shareFlag ,Object object, Conversation.ConversationType type) {
        this.shareFlag = shareFlag;
        this.mObject = object;
        this.mType = type;
    }

    public EventShareMessage(int shareFlag ,Object object, Conversation.ConversationType type,String Liuyan) {
        this.shareFlag = shareFlag;
        this.mObject = object;
        this.mType = type;
        this.Liuyan = Liuyan;
    }



}
