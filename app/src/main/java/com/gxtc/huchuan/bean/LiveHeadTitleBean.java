package com.gxtc.huchuan.bean;

import com.google.gson.annotations.SerializedName;
import com.gxtc.huchuan.base.ITabTitle;

import java.io.Serializable;
import java.util.List;

/**
 * 直播首页 头部的图标
 * Created by Gubr on 2017/2/28.
 *
 */

public class LiveHeadTitleBean implements Serializable {
    /**
     * chatTypeSon : [{"chatTypeId":"1","id":"1","typeName":"婚恋"},{"chatTypeId":"1","id":"2","typeName":"心理"},{"chatTypeId":"1","id":"3","typeName":"两性"}]
     * id : 1
     * isDefault : 1
     * typeName : 情感
     */

    @SerializedName("id")
    private String id;
    @SerializedName("isDefault")
    private String isDefault;
    @SerializedName("typeName")
    private String typeName;
    @SerializedName("chatTypeSon")
    private List<ChatTypeSonBean> chatTypeSon;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<ChatTypeSonBean> getChatTypeSon() {
        return chatTypeSon;
    }

    public void setChatTypeSon(List<ChatTypeSonBean> chatTypeSon) {
        this.chatTypeSon = chatTypeSon;
    }

    public static class LiveHeadTitleListBean {
        public String title;
        public String id;
    }


    public static class ChatTypeSonBean  implements Serializable ,ITabTitle{
        /**
         * chatTypeId : 1
         * id : 1
         * typeName : 婚恋
         */

        @SerializedName("chatTypeId")
        private String chatTypeId;
        @SerializedName("id")
        private String id;
        @SerializedName("typeName")
        private String typeName;

        public String getChatTypeId() {
            return chatTypeId;
        }

        public void setChatTypeId(String chatTypeId) {
            this.chatTypeId = chatTypeId;
        }

        public String getId() {
            return id;
        }


        public void setId(String id) {
            this.id = id;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        @Override
        public String getTitle() {
            return typeName;
        }
    }
}


