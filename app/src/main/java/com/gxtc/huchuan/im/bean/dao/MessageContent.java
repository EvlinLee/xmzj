package com.gxtc.huchuan.im.bean.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Gubr on 2017/4/21.
 */
@Entity
public class MessageContent {
    @Id
    Long megId;

    @Generated(hash = 1284305655)
    public MessageContent(Long megId) {
        this.megId = megId;
    }

    @Generated(hash = 395736529)
    public MessageContent() {
    }

    public Long getMegId() {
        return this.megId;
    }

    public void setMegId(Long megId) {
        this.megId = megId;
    }
}
