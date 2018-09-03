package com.gxtc.huchuan.bean;

/**
 * Created by Administrator on 2017/6/20 0020.
 */

public class DustbinListBean {
    private int id;
    private String title;
    private String time;
    private int type;//0圈子;1交易;2新闻;3课堂;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
