package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Describe:
 * Created by ALing on 2017/5/18 .
 */

public class VisitorBean  {

    /**
     * browseTime : 1494930234000
     * dateStr : 2017-05-16
     * headPic : http://xmzjvip.b0.upaiyun.com/xmzj/75911493710590119.jpeg
     * name : Wwe
     * userCode : 452303476
     */

    private long       browseTime;
    private String       dateStr;
    private String       headPic;
    private String       name;
    private String       userCode;
    private boolean       isHead = false;

    private String totalCount;
    private String todayCount;

    //数据统计那边每日访客的
    private String dateName;
    private String timeSection;
    private int count;
    private String createtime;

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getDateName() {
        return dateName;
    }

    public void setDateName(String dateName) {
        this.dateName = dateName;
    }

    public String getTimeSection() {
        return timeSection;
    }

    public void setTimeSection(String timeSection) {
        this.timeSection = timeSection;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public VisitorBean(boolean isHead, String dateStr){
        this.isHead = isHead;
        this.dateStr = dateStr;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setHead(boolean head) {
        isHead = head;
    }

    public VisitorBean() {
    }


    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getTodayCount() {
        return todayCount;
    }

    public void setTodayCount(String todayCount) {
        this.todayCount = todayCount;
    }

    public long getBrowseTime() { return browseTime;}

    public void setBrowseTime(long browseTime) { this.browseTime = browseTime;}

    public String getDateStr() { return dateStr;}

    public void setDateStr(String dateStr) { this.dateStr = dateStr;}

    public String getHeadPic() { return headPic;}

    public void setHeadPic(String headPic) { this.headPic = headPic;}

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getUserCode() { return userCode;}

    public void setUserCode(String userCode) { this.userCode = userCode;}
}
