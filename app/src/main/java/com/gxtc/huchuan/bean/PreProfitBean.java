package com.gxtc.huchuan.bean;

/**预收益
 * Created by Administrator on 2017/5/26.
 */

public class PreProfitBean {

    /**
     * chatRoomName : 一号
     * commission : 0.02
     * facePic : http://xmzjvip.b0.upaiyun.com/xmzj/27731490780153333.png
     * headPic : http://xmzjvip.b0.upaiyun.com/xmzj/65861492152072526.jpg
     * id : 21
     * name : aa
     * title : 二号话题
     * type : chatInfo
     * userCode : 128399864
     * createTime: 1493886543000
     * isSett: 1     0，未结算，1，已经结算
     */

    private String chatRoomName;
    private String commission;
    private String facePic;
    private String headPic;
    private String id;
    private String name;
    private String title;
    private String type;
    private String userCode;
    private String createTime;
    private String isSett;

    public String getIsSett() {
        return isSett;
    }

    public void setIsSett(String isSett) {
        this.isSett = isSett;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getFacePic() {
        return facePic;
    }

    public void setFacePic(String facePic) {
        this.facePic = facePic;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
