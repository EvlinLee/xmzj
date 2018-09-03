package com.gxtc.huchuan.bean.event;

/**
 * Created by sjr on 2017/5/23.
 * 圈子收费
 */

public class EventCircleMemberShipBean {

    private int isFree;
    private String fee;
    private String pent;

    public EventCircleMemberShipBean(int isFree, String fee, String pent) {
        this.isFree = isFree;
        this.fee = fee;
        this.pent = pent;
    }

    public int getIsFree() {
        return isFree;
    }

    public void setIsFree(int isFree) {
        this.isFree = isFree;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getPent() {
        return pent;
    }

    public void setPent(String pent) {
        this.pent = pent;
    }
}
