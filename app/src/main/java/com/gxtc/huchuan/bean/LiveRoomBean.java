package com.gxtc.huchuan.bean;


import java.io.Serializable;
import java.util.List;

/**
 * 课堂页面的接口  （包含 课程  系列课 介绍）
 */
public class LiveRoomBean implements Serializable {

    private static final long serialVersionUID = -763618247875560322L;
    /**
     * id	系列课ID
     * chatRoom	直播间ID
     * headpic	系列课头像
     * Seriesname	系列课名称
     * introduce	系列课介绍
     * type	系列课类型ID
     * fee	费用价格
     * fs	粉丝数量
     * chatRoomName	直播间名称
     * buyCount	购买人数
     * showDiscount	是否显示优惠码。0和1
     * 0：显示，1：不显示
     * buyUsers	参与购买人信息列表
     * chatInfos	课程列表，请参照37.5接口输出参数：
     * chatInfoCount	总课程数量
     * isSelf	是否是自己的系列课。0和1
     * 0：不是，1：是
     * isBuy	是否购买。0和1
     * 0：否，1：已购买
     * typeName	系列课类型名称
     * shareUrl	分享推荐urlname	直播间创建人用户名字，自媒体名字
     * headPic	直播间创建人用户头像
     * userCode	直播间创建人用户编码
     */
    private String bakpic;
    private String chatTypeId;
    private String creattime;
    private String fs;
    private String headpic;
    private String id;
    private String introduce;
    private String isSelf;
    private String property;
    private String qrcode;
    private String roomlink;
    private String roomname;
    private String typeName;
    private String shareUrl;
    private String name;
    private String headPic;
    private String userCode;
    private String isCollect;
    private String chatInfoCount = "0";



    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getChatInfoCount() {
        return chatInfoCount;
    }

    public void setChatInfoCount(String chatInfoCount) {
        this.chatInfoCount = chatInfoCount;
    }

    public String getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(String isCollect) {
        this.isCollect = isCollect;
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

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    private String                    isFollow;
    private List<ChatInfosBean>       chatInfos;
    private List<ChatSeriesBean>      chatSeries;
    private List<ChatSeriesTypesBean> chatSeriesTypes;


    public String getBakpic() {
        return bakpic;
    }

    public void setBakpic(String bakpic) {
        this.bakpic = bakpic;
    }

    public String getChatTypeId() {
        return chatTypeId;
    }

    public void setChatTypeId(String chatTypeId) {
        this.chatTypeId = chatTypeId;
    }

    public String getCreattime() {
        return creattime;
    }

    public void setCreattime(String creattime) {
        this.creattime = creattime;
    }

    public String getFs() {
        return fs;
    }

    public void setFs(String fs) {
        this.fs = fs;
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


    public boolean bIsSelf() {
        return isSelf.equals("1");
    }

    public String getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(String isSelf) {
        this.isSelf = isSelf;
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

    public List<ChatInfosBean> getChatInfos() {
        return chatInfos;
    }

    public void setChatInfos(List<ChatInfosBean> chatInfos) {
        this.chatInfos = chatInfos;
    }

    public List<ChatSeriesBean> getChatSeries() {
        return chatSeries;
    }

    public void setChatSeries(List<ChatSeriesBean> chatSeries) {
        this.chatSeries = chatSeries;
    }

    public List<ChatSeriesTypesBean> getChatSeriesTypes() {
        return chatSeriesTypes;
    }

    public void setChatSeriesTypes(List<ChatSeriesTypesBean> chatSeriesTypes) {
        this.chatSeriesTypes = chatSeriesTypes;
    }


    public String getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(String isFollow) {
        this.isFollow = isFollow;
    }

    public boolean isFolow() {
        return isFollow.equals("1");
    }


    public boolean toggleFollow() {
        if (isFolow()) {
            isFollow = "0";
        } else {
            isFollow = "1";
        }
        return isFolow();
    }

}
