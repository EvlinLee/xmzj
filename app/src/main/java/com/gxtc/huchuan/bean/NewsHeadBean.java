package com.gxtc.huchuan.bean;

/**
 * Created by 宋家任 on 2016/11/30.
 * 资讯头部
 */

public class NewsHeadBean {
    private int iv;
    private String title;

    public NewsHeadBean(int iv, String title) {
        this.iv = iv;
        this.title = title;
    }

    public int getIv() {
        return iv;
    }

    public void setIv(int iv) {
        this.iv = iv;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
