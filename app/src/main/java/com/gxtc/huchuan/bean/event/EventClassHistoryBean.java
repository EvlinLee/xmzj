package com.gxtc.huchuan.bean.event;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;

/**
 * Describe:
 * Created by ALing on 2017/4/6 .
 */

public class EventClassHistoryBean {
    private boolean isSelected;
    private int position;
    public BaseRecyclerAdapter.ViewHolder holder;
    private String chatInfoId;

    public EventClassHistoryBean(String chatInfoId,boolean isSelected) {
        this.chatInfoId = chatInfoId;
        this.isSelected = isSelected;
    }

    public EventClassHistoryBean(boolean isSelected, int position,
                                 BaseRecyclerAdapter.ViewHolder holder) {
        this.isSelected = isSelected;
        this.position = position;
        this.holder = holder;
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

    public String getChatInfoId() {
        return chatInfoId;
    }

    public void setChatInfoId(String chatInfoId) {
        this.chatInfoId = chatInfoId;
    }
}
