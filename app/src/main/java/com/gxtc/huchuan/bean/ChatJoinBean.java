package com.gxtc.huchuan.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Gubr on 2017/5/10.
 */

public class ChatJoinBean {

     //0普通成员，1管理员，2讲师，3创建者
     public static String ROLE_ORDINARY = "0";
    public static String ROLE_MANAGER = "1";
    public static String ROLE_TEACHER = "2";
    public static String ROLE_HOST = "3";

    @SerializedName("hisList") private List<MemberBean>           hisList;
    @SerializedName("currList") private List<MemberBean> currList;

    public List<MemberBean> getHisList() { return hisList;}

    public void setHisList(List<MemberBean> hisList) { this.hisList = hisList;}

    public List<MemberBean> getCurrList() { return currList;}

    public void setCurrList(List<MemberBean> currList) { this.currList = currList;}

    public static class MemberBean {

        /**
         * chatInfoId : 20
         * chatRoomId : 3
         * headPic : http://xmzjvip.b0.upaiyun.com/xmzj/96541493803989807.jpeg
         * id : 0
         * joinType : 1
         * name : 472823
         * userCode : 571904840
         * blacklist   是否已加入黑名单，0：否，1：是
         * prohibitSpeaking  是否已禁言，0：否，1：是
         */

        @SerializedName("chatInfoId") private String chatInfoId;
        @SerializedName("chatRoomId") private String chatRoomId;
        @SerializedName("headPic") private    String headPic;
        @SerializedName("id") private         String id;
        @SerializedName("joinType") private   String joinType;
        @SerializedName("name") private       String name;
        @SerializedName("userCode") private   String userCode;
        @SerializedName("blacklist") private   String blacklist;
        @SerializedName("prohibitSpeaking") private   String prohibitSpeaking;


        public void setBlacklist(String blacklist) {
            this.blacklist = blacklist;
        }


        public void setProhibitSpeaking(String prohibitSpeaking) {
            this.prohibitSpeaking = prohibitSpeaking;
        }

        public boolean isBlacklist(){
            return blacklist.equals("1");
        }

        public boolean isProhibitSpeaking(){
            return prohibitSpeaking.equals("1");
        }

        public String getChatInfoId() { return chatInfoId;}

        public void setChatInfoId(String chatInfoId) { this.chatInfoId = chatInfoId;}

        public String getChatRoomId() { return chatRoomId;}

        public void setChatRoomId(String chatRoomId) { this.chatRoomId = chatRoomId;}

        public String getHeadPic() { return headPic;}

        public void setHeadPic(String headPic) { this.headPic = headPic;}

        public String getId() { return id;}

        public void setId(String id) { this.id = id;}

        public String getJoinType() { return joinType;}

        public void setJoinType(String joinType) { this.joinType = joinType;}

        public String getName() { return name;}

        public void setName(String name) { this.name = name;}

        public String getUserCode() { return userCode;}

        public void setUserCode(String userCode) { this.userCode = userCode;}
    }
}
