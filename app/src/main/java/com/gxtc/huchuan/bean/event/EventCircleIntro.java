package com.gxtc.huchuan.bean.event;

/**
 * Describe:
 * Created by ALing on 2017/5/9 .
 */

public class EventCircleIntro {
    public static final int EXIT = 0;      //退出圈子
    public static final int MEMBER = 1;     //查看圈子成员
    public static final int MANAGER = 2;    //查看圈子管理员
    public static final int ALLMEMBER = 3;  //管理圈子成员
    public static final int DELETE = 4;      //删除圈子
    public static final int CREATE = 5;      //圈子创建群聊
    public static final int ENTER = 6;      //加入圈子

    public int status;
    private String intro;
    private  int groupId;
    public EventCircleIntro(int status) {
        this.status = status;
    }
    public EventCircleIntro(int status,int groupId) {
        this.status = status;
        this.groupId = groupId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public EventCircleIntro(String intro) {
        this.intro = intro;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }
}
