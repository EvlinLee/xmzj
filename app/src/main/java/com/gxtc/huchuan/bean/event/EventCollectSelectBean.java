package com.gxtc.huchuan.bean.event;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;

/**
 * Created by sjr on 2017/3/3.
 * 手藏文章是否选中
 */

public class EventCollectSelectBean {
    private boolean isSelected;
    private int position;
    public BaseRecyclerAdapter.ViewHolder holder;

    public EventCollectSelectBean(boolean isSelected, int position,
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
}
