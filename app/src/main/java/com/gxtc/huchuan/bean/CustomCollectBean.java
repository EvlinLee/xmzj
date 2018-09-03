package com.gxtc.huchuan.bean;

import java.io.Serializable;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;

/**
 * Created by Gubr on 2017/6/5.
 *
 */

public class CustomCollectBean implements Serializable {

    /**
     * title :  周日闲谈 | 做人呢，不要给自己太大压力
     * url : http://app.xinmei6.com/html/collect.html?id=193&from=
     */
    private String id;
    private String title;
    private String url;
    private String content;


    private boolean flag;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getStrContent() {
        return strContent;
    }

    public void setStrContent(String strContent) {
        this.strContent = strContent;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    private String strContent;

    private String imgUrl;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() { return title;}

    public void setTitle(String title) { this.title = title;}

    public String getUrl() { return url + "android";}

    public void setUrl(String url) { this.url = url;}

    public String getId() {return id;}

    public void setId(String id) {this.id = id;}
}

