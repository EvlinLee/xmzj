package com.gxtc.huchuan.bean;

import java.io.Serializable;

/**
 * Created by Gubr on 2017/3/13.
 */
public class ChatSeriesBean implements Serializable {

    private static final long serialVersionUID = -763328247875560552L;
    /**
     * buyCount : 0
     * chatRoom : 2
     * chatRoomName : 一号
     * fee : 10.0
     * headpic :
     * id : 1
     * introduce :
     * seriesname : 一号系列课
     * type : 0
     */

    private String buyCount;
    private String chatRoom;
    private String chatRoomName;
    private String fee;
    private String headpic;
    private String id;
    private String introduce;
    private String seriesname;
    private String type;
    private int chatInfoCount;

    public int getChatInfoCount() {
        return chatInfoCount;
    }

    public void setChatInfoCount(int chatInfoCount) {
        this.chatInfoCount = chatInfoCount;
    }

    public String getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(String buyCount) {
        this.buyCount = buyCount;
    }

    public String getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(String chatRoom) {
        this.chatRoom = chatRoom;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
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

    public String getSeriesname() {
        return seriesname;
    }

    public void setSeriesname(String seriesname) {
        this.seriesname = seriesname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
