package com.gxtc.huchuan.im.bean.dao;

import com.gxtc.huchuan.im.bean.RemoteMessageBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;


/**
 * Created by Gubr on 2017/4/8.
 */
@Entity
public class MessageHistoryBean {
    private     String content;
    private     String dataTime;
    private     String fromUserId;
    private     String name;
    private     String objectName;
    private     long   targetId;
    private     String targetType;
    private     String userCode;
    @Id private long   id;


    public MessageHistoryBean(RemoteMessageBean messageBean) {
        this.content = messageBean.getContent();
        this.dataTime = messageBean.getDataTime();
        this.fromUserId = messageBean.getFromUserId();
        this.name = messageBean.getName();
        this.objectName = messageBean.getObjectName();
        this.targetId = Long.valueOf(messageBean.getTargetId());
        this.targetType = messageBean.getTargetType();
        this.userCode = messageBean.getUserCode();
        this.id = Long.valueOf(messageBean.getId());
    }



    @Generated(hash = 171102149)
    public MessageHistoryBean(String content, String dataTime, String fromUserId,
            String name, String objectName, long targetId, String targetType,
            String userCode, long id) {
        this.content = content;
        this.dataTime = dataTime;
        this.fromUserId = fromUserId;
        this.name = name;
        this.objectName = objectName;
        this.targetId = targetId;
        this.targetType = targetType;
        this.userCode = userCode;
        this.id = id;
    }
    @Generated(hash = 830674591)
    public MessageHistoryBean() {
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getDataTime() {
        return this.dataTime;
    }
    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }
    public String getFromUserId() {
        return this.fromUserId;
    }
    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getObjectName() {
        return this.objectName;
    }
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
    public long getTargetId() {
        return this.targetId;
    }
    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }
    public String getTargetType() {
        return this.targetType;
    }
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
    public String getUserCode() {
        return this.userCode;
    }
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }




}