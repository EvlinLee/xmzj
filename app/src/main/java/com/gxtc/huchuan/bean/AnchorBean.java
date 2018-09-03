package com.gxtc.huchuan.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Gubr on 2017/3/6.
 * 28.获取主播申请信息接口  的bean
 */

public class AnchorBean {

    /**
     * education :
     * name : 旭旭
     * reel :
     * skill : 解惑
     * wechat : weixin
     * work :
     */

    @SerializedName("education")
    private String education;
    @SerializedName("name")
    private String name;
    @SerializedName("reel")
    private String reel;
    @SerializedName("skill")
    private String skill;
    @SerializedName("wechat")
    private String wechat;
    @SerializedName("work")
    private String work;

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReel() {
        return reel;
    }

    public void setReel(String reel) {
        this.reel = reel;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }
}
