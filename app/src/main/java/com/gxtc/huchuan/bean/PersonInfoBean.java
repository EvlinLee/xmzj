package com.gxtc.huchuan.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/6/1 .
 */

public class PersonInfoBean implements Serializable {

    /**
     * city : 滁州
     * headPic : http://xmzjvip.b0.upaiyun.com/xmzj/65861492152072526.jpg
     * introduction :
     * name : aaaaa
     * photos : ["https://xmzjvip.b0.upaiyun.com/xmzj/33871495475279695.jpg","https://xmzjvip.b0.upaiyun.com/xmzj/75751495475204403.jpg","https://xmzjvip.b0.upaiyun.com/xmzj/78171495475204591.jpg","https://xmzjvip.b0.upaiyun.com/xmzj/24071495475139212.jpg"]
     * remarkDesc :
     * remarkLabel :
     * remarkName :
     * remarkPhone :
     * sex : 2
     * userCode : 128399864
     */

    private int          isFollow;
    private int          id;
    private int          isFans;        //是否已被关注，0、不是；1、是
    private String       city;
    private String       headPic;
    private String       introduction;
    private String       name;
    private String       remarkDesc;
    private String       remarkLabel;
    private String       remarkName;
    private String       remarkPhone;
    private String       sex;
    private String       userCode;
    private String       qRCode;        //用户二维码
    private List<String> photos;
    private String       isChat;//是否可发消息。0、可发；1、不可发
    private String       unLook;//不看他的动态。0、未设置；1、已设置
    private String       withhold;//不给看动态。0、未设置；1、已设置
    private String       isBlackList;//黑名单。0、未设置；1、已设置
    private String       isRealAudit;//是否实名 0、否；1、是;2、实名审核中
    private String       phones;
    private int          chatStatus = -1 ;    //0申请好友,  1通过好友  2等待验证
    private int          isSaler = 0;              //：0=默认正常用户， 1 = 黑名单

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsSaler() {
        return isSaler;
    }

    public void setIsSaler(int isSaler) {
        this.isSaler = isSaler;
    }

    public String getPhones() {
        return phones;
    }

    public void setPhones(String phones) {
        this.phones = phones;
    }

    public int getChatStatus() {
        return chatStatus;
    }

    public void setChatStatus(int chatStatus) {
        this.chatStatus = chatStatus;
    }

    public int getIsFans() {
        return isFans;
    }

    public void setIsFans(int isFans) {
        this.isFans = isFans;
    }

    public String getIsRealAudit() {
        return isRealAudit;
    }

    public void setIsRealAudit(String isRealAudit) {
        this.isRealAudit = isRealAudit;
    }

    public String getIsChat() {
        return isChat;
    }

    public void setIsChat(String isChat) {
        this.isChat = isChat;
    }

    public String getUnLook() {
        return unLook;
    }

    public void setUnLook(String unLook) {
        this.unLook = unLook;
    }

    public String getWithhold() {
        return withhold;
    }

    public void setWithhold(String withhold) {
        this.withhold = withhold;
    }

    public String getIsBlackList() {
        return isBlackList;
    }

    public void setIsBlackList(String isBlackList) {
        this.isBlackList = isBlackList;
    }

    public String getqRCode() {
        return qRCode;
    }

    public void setqRCode(String qRCode) {
        this.qRCode = qRCode;
    }

    public int getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(int isFollow) {
        this.isFollow = isFollow;
    }

    public String getCity() { return city;}

    public void setCity(String city) { this.city = city;}

    public String getHeadPic() { return headPic;}

    public void setHeadPic(String headPic) { this.headPic = headPic;}

    public String getIntroduction() { return introduction;}

    public void setIntroduction(String introduction) { this.introduction = introduction;}

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getRemarkDesc() { return remarkDesc;}

    public void setRemarkDesc(String remarkDesc) { this.remarkDesc = remarkDesc;}

    public String getRemarkLabel() { return remarkLabel;}

    public void setRemarkLabel(String remarkLabel) { this.remarkLabel = remarkLabel;}

    public String getRemarkName() { return remarkName;}

    public void setRemarkName(String remarkName) { this.remarkName = remarkName;}

    public String getRemarkPhone() { return remarkPhone;}

    public void setRemarkPhone(String remarkPhone) { this.remarkPhone = remarkPhone;}

    public String getSex() { return sex;}

    public void setSex(String sex) { this.sex = sex;}

    public String getUserCode() { return userCode;}

    public void setUserCode(String userCode) { this.userCode = userCode;}

    public List<String> getPhotos() { return photos;}

    public void setPhotos(List<String> photos) { this.photos = photos;}


}
