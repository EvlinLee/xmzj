package com.gxtc.huchuan.bean.event;

/**
 * Created by ALing on 2017/2/24 0024.
 */

public class EventEditInfoBean {
    public static final int UPLOADAVATAR = 0;      //上传头像
    public static final int CHANGENAME = 1;      //修改名称
    public static final int ISANCHOR = 2;       //是否是主播  0：否  1：是
    public static final int CREATELIVE = 3;     //一键创建直播间
    public static final int INTRO = 4;     //简介
    public static final int UPDATSEXSTATUS= 5;      //更改性别
    public int status;

    public EventEditInfoBean(int status) {
        this.status = status;
    }
}
