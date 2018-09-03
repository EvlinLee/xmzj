package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Created by Gubr on 2017/3/20.
 * 关注  人数    用这个对象
 */

public class PersonCountBean implements Serializable{
    private String name;
    private String headPic;
    private String shareCount;
    private String call; //这个是称呼

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public PersonCountBean(String name, String headPic, String shareCount, String call) {
        this.name = name;
        this.headPic = headPic;
        this.shareCount = shareCount;
        this.call = call;
    }

    public PersonCountBean(String name, String headPic) {
        this.name = name;
        this.headPic = headPic;

    }


    public String getShareCount() {
        return shareCount;
    }

    public void setShareCount(String shareCount) {
        this.shareCount = shareCount;
    }
}
