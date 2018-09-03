package com.gxtc.huchuan.bean.event;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;

/**
 * Describe:
 * Created by ALing on 2017/5/27 .
 */

public class EventBrowseHistoryBean {
    private boolean                       isSelected;
    private int                           position;
    public BaseRecyclerAdapter.ViewHolder holder;
    private String                        delIds;

    public EventBrowseHistoryBean(String delIds,boolean isSelected,int position) {
        this.delIds = delIds;
        this.isSelected = isSelected;
        this.position = position;
    }

    public EventBrowseHistoryBean(boolean isSelected, int position,
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

    public String getDelIds() {
        return delIds;
    }

    public void setDelIds(String delIds) {
        this.delIds = delIds;
    }
}
