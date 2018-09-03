package com.gxtc.huchuan.bean;

/**
 * Describe:课堂背景图片
 * Created by ALing on 2017/3/24 .
 */

public class BgPicBean {

    /**
     * picUrl : http://xmzjvip.b0.upaiyun.com/xmzj/82441490254569015.jpg
     * title : 2
     */

    private String picUrl;
    private String title;
    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
