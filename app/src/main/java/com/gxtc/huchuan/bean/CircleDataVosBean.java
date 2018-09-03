package com.gxtc.huchuan.bean;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/23.
 */

public class CircleDataVosBean {

    private long time;

    private int joinNum;          //新增成员数量

    private int infoNum;          //发帖数量

    private int checkNum;         //圈子查看数

    private int preViewNum;       //圈子预览数

    private int dayActiveUser;    // 每日活跃用户数

    public CircleDataVosBean() {
    }

    public int getDayActiveUser() {
        return dayActiveUser;
    }

    public void setDayActiveUser(int dayActiveUser) {
        this.dayActiveUser = dayActiveUser;
    }

    public int getPreViewNum() {
        return preViewNum;
    }

    public void setPreViewNum(int preViewNum) {
        this.preViewNum = preViewNum;
    }

    public int getCheckNum() {
        return checkNum;
    }

    public void setCheckNum(int checkNum) {
        this.checkNum = checkNum;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getJoinNum() {
        return joinNum;
    }

    public void setJoinNum(int joinNum) {
        this.joinNum = joinNum;
    }

    public int getInfoNum() {
        return infoNum;
    }

    public void setInfoNum(int infoNum) {
        this.infoNum = infoNum;
    }
}
