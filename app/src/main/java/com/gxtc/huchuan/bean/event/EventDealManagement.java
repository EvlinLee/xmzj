package com.gxtc.huchuan.bean.event;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;

/**
 * Describe:
 * Created by ALing on 2017/5/15 .
 */

public class EventDealManagement {
    private boolean isSelected;
    private int position;
    public BaseRecyclerAdapter.ViewHolder holder;
    private int orderId;     //订单id
    public EventDealManagement(int orderId,boolean isSelected) {
        this.orderId = orderId;
        this.isSelected = isSelected;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getChatInfoId() {
        return orderId;
    }

    public void setChatInfoId(int orderId) {
        this.orderId = orderId;
    }
}
