package com.gxtc.huchuan.bean.event;

import java.io.Serializable;

/**
 * Created by Steven on 17/5/3.
 */

public class EventJPushMessgeBean implements Serializable {

    public String userPic ;
    public String userName;
    public String unReadNum;
    public String userCode;

    public EventJPushMessgeBean(String userPic, String userName, String unReadNum, String userCode) {
        this.userPic = userPic;
        this.userName = userName;
        this.unReadNum = unReadNum;
        this.userCode = userCode;
    }
}
