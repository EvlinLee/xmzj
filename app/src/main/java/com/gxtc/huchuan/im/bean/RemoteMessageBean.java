package com.gxtc.huchuan.im.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Gubr on 2017/4/8.
 *
 *  从自己的服务器获取聊天记录
 */
@Entity
public class RemoteMessageBean {


    private String content;
    private String dataTime;
    private String fromUserId;
    private String name;
    private String objectName;
    private long   targetId;
    private String targetType;
    private String userCode;
    private String showType;
    private long msgId;


    @Id
    private long   id;
    @Generated(hash = 424901051)
    public RemoteMessageBean(String content, String dataTime, String fromUserId,
            String name, String objectName, long targetId, String targetType,
            String userCode, String showType, long msgId, long id) {
        this.content = content;
        this.dataTime = dataTime;
        this.fromUserId = fromUserId;
        this.name = name;
        this.objectName = objectName;
        this.targetId = targetId;
        this.targetType = targetType;
        this.userCode = userCode;
        this.showType = showType;
        this.msgId = msgId;
        this.id = id;
    }
    @Generated(hash = 1566014635)
    public RemoteMessageBean() {
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
    public long getMsgId() {
        return this.msgId;
    }
    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }
    public String getShowType() {
        return this.showType;
    }
    public void setShowType(String showType) {
        this.showType = showType;
    }

    @Override
    public String toString() {
        return "RemoteMessageBean{" + "content='" + content + '\'' + ", dataTime='" + dataTime + '\'' + ", fromUserId='" + fromUserId + '\'' + ", name='" + name + '\'' + ", objectName='" + objectName + '\'' + ", targetId=" + targetId + ", targetType='" + targetType + '\'' + ", userCode='" + userCode + '\'' + ", showType='" + showType + '\'' + ", msgId=" + msgId + ", id=" + id + '}';
    }
}