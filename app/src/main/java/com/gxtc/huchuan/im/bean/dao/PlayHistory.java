package com.gxtc.huchuan.im.bean.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Gubr on 2017/4/13.
 */

@Entity
public class PlayHistory {
    @Id Long id;
    String msgId;
    String uri;

    @Generated(hash = 2121960523)
    public PlayHistory(Long id, String msgId, String uri) {
        this.id = id;
        this.msgId = msgId;
        this.uri = uri;
    }

    @Generated(hash = 2145518983)
    public PlayHistory() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsgId() {
        return this.msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }


}
