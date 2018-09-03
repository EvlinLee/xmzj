package com.gxtc.huchuan.bean;

import java.util.List;

/**
 * Describe:直播间管理
 * Created by ALing on 2017/3/22 .
 */

public class LiveBgSettingBean {

    /**
     * bakpic :
     * chatInfos : []
     * chatSeries : []
     * chatSeriesTypes : []
     * creattime : 1489476451000
     * headpic :
     * id : 4
     * introduce :
     * property : 1
     * qrcode :
     * roomlink :
     * roomname : 看看
     */

    private String bakpic;
    private String creattime;
    private String headpic;
    private String id;
    private String introduce;
    private String property;
    private String qrcode;
    private String roomlink;
    private String roomname;
    private List<?> chatInfos;
    private List<?> chatSeries;
    private List<?> chatSeriesTypes;

    public LiveBgSettingBean(String bakpic) {
        this.bakpic = bakpic;
    }

    public String getBakpic() {
        return bakpic;
    }

    public void setBakpic(String bakpic) {
        this.bakpic = bakpic;
    }

    public String getCreattime() {
        return creattime;
    }

    public void setCreattime(String creattime) {
        this.creattime = creattime;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getRoomlink() {
        return roomlink;
    }

    public void setRoomlink(String roomlink) {
        this.roomlink = roomlink;
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }

    public List<?> getChatInfos() {
        return chatInfos;
    }

    public void setChatInfos(List<?> chatInfos) {
        this.chatInfos = chatInfos;
    }

    public List<?> getChatSeries() {
        return chatSeries;
    }

    public void setChatSeries(List<?> chatSeries) {
        this.chatSeries = chatSeries;
    }

    public List<?> getChatSeriesTypes() {
        return chatSeriesTypes;
    }

    public void setChatSeriesTypes(List<?> chatSeriesTypes) {
        this.chatSeriesTypes = chatSeriesTypes;
    }

    @Override
    public String toString() {
        return "LiveBgSettingBean{" + "bakpic='" + bakpic + '\'' + ", creattime='" + creattime + '\'' + ", headpic='" + headpic + '\'' + ", id='" + id + '\'' + ", introduce='" + introduce + '\'' + ", property='" + property + '\'' + ", qrcode='" + qrcode + '\'' + ", roomlink='" + roomlink + '\'' + ", roomname='" + roomname + '\'' + ", chatInfos=" + chatInfos + ", chatSeries=" + chatSeries + ", chatSeriesTypes=" + chatSeriesTypes + '}';
    }
}
