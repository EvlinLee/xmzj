package com.gxtc.huchuan.bean.event;

/**
 * Describe:
 * Created by ALing on 2017/3/30 .
 */

public class EventPopBubleBean {
    private String edit;            //编辑
    private String del;             //删除
    private String position;        //列表位置

    public EventPopBubleBean(String position) {
        this.position = position;
    }

    public EventPopBubleBean() {
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEdit() {
        return edit;
    }

    public void setEdit(String edit) {
        this.edit = edit;
    }

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }
}
