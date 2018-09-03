package com.gxtc.huchuan.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Gubr on 2017/3/23.
 */

public class AuditBean {

    /**
     * 参数名称	说明
     * education	学历
     * name	主播名字
     * reel	描述
     * skill	擅长
     * wechat	微信
     * work	工作
     * audit	审核状态：0，1，2
     * 0: 新申请，未审核，
     * 1审核通过，
     * 2审核不通过
     */

    @SerializedName("audit")
    private String audit;
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
    @SerializedName("auditType")
    private String auditType;       //0  申请主播   1 申请作者

    public AuditBean(String audit,String auditType) {
        this.audit = audit;
        this.auditType = auditType;
    }

    public String getAudit() { return audit;}

    public void setAudit(String audit) { this.audit = audit;}

    public String getEducation() { return education;}

    public void setEducation(String education) { this.education = education;}

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getReel() { return reel;}

    public void setReel(String reel) { this.reel = reel;}

    public String getSkill() { return skill;}

    public void setSkill(String skill) { this.skill = skill;}

    public String getWechat() { return wechat;}

    public void setWechat(String wechat) { this.wechat = wechat;}

    public String getWork() { return work;}

    public void setWork(String work) { this.work = work;}

    public String getAuditType() {
        return auditType;
    }

    public void setAuditType(String auditType) {
        this.auditType = auditType;
    }
}
