package com.gxtc.huchuan.bean;

/**
 * 来自 苏修伟 on 2018/4/26.
 */
public class FollowLecturerBean {

   private String userCode;
   private String name;
   private String headPic;
   private int num;                    //课程数量
   private int fsCount;               //粉丝数量
   private String  chatRoomId;        //直播间id

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
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

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getFsCount() {
        return fsCount;
    }

    public void setFsCount(int fsCount) {
        this.fsCount = fsCount;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
}
