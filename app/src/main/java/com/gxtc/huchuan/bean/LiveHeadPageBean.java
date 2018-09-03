package com.gxtc.huchuan.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gubr on 2017/3/13.
 * 首页-课堂-bean    （首页直播的bean）
 */

public class LiveHeadPageBean implements Serializable {

    @SerializedName("advanceChatInfos")
    private List<ChatInfosBean> advanceChatInfos;
    @SerializedName("chatTypes")
    private List<LiveHeadTitleBean> chatTypes;
    @SerializedName("mTypesChatRooms")
    private List<TypesChatRoom> mTypesChatRooms;
    @SerializedName("watchingChatInfos")
    private List<ChatInfosBean> watchingChatInfos;

    public List<ChatInfosBean> getAdvanceChatInfos() {
        return advanceChatInfos;
    }

    public void setAdvanceChatInfos(List<ChatInfosBean> advanceChatInfos) {
        this.advanceChatInfos = advanceChatInfos;
    }

    public List<LiveHeadTitleBean> getChatTypes() {
        return chatTypes;
    }

    public void setChatTypes(List<LiveHeadTitleBean> chatTypes) {
        this.chatTypes = chatTypes;
    }

    public List<TypesChatRoom> getTypesChatRooms() {
        return mTypesChatRooms;
    }

    public void setTypesChatRooms(List<TypesChatRoom> typesChatRooms) {
        this.mTypesChatRooms = typesChatRooms;
    }

    public List<ChatInfosBean> getWatchingChatInfos() {
        return watchingChatInfos;
    }

    public void setWatchingChatInfos(List<ChatInfosBean> watchingChatInfos) {
        this.watchingChatInfos = watchingChatInfos;
    }

public static class TypesChatRoom {

    /**
     * chatRooms : [{"bakpic":"sd","chatInfos":[],"chatSeries":[],"chatSeriesTypes":[],"chatTypeId":"1","creattime":"1488422107000","fs":"0","headpic":"sd","id":"1","introduce":"sd","isFollow":"0","isSelf":"0","property":"1","qrcode":"","roomlink":"","roomname":"dsd"},{"bakpic":"","chatInfos":[],"chatSeries":[],"chatSeriesTypes":[],"chatTypeId":"1","creattime":"1488528780000","fs":"0","headpic":"","id":"2","introduce":"","isFollow":"0","isSelf":"0","property":"0","qrcode":"","roomlink":"","roomname":"一号"}]
     * type : {"chatTypeSon":[],"id":"1","isDefault":"1","typeName":"情感"}
     */

    @SerializedName("type")
    private LiveHeadTitleBean type;
    @SerializedName("chatRooms")
    private List<LiveRoomBean> chatRooms;

    public LiveHeadTitleBean getType() {
        return type;
    }

    public void setType(LiveHeadTitleBean type) {
        this.type = type;
    }

    public List<LiveRoomBean> getChatRooms() {
        return chatRooms;
    }

    public void setChatRooms(List<LiveRoomBean> chatRooms) {
        this.chatRooms = chatRooms;
    }



}
}
