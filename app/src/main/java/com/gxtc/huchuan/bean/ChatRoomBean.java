package com.gxtc.huchuan.bean;

/**
 * Created by Steven on 17/4/18.
 */

public class ChatRoomBean {

    private String tradeInfoId;         //交易信息ID
    private String tradeInfoTitle;      //交易标题
    private String fbUserCode;          //卖家编码
    private String fbUserName;          //卖家名称
    private String fbUserPic;           //卖家头像
    private String yxUserCode;          //买家编码
    private String yxUserName;          //买家名称
    private String yxUserPic;           //买家头像
    private String ptUserCode;          //客服编码
    private String ptUserName;          //客服名称
    private String ptUserPic;           //客服头像
    private String chatId;              //聊天室ID


    public ChatRoomBean() {
    }

    public String getTradeInfoId() {
        return tradeInfoId;
    }

    public void setTradeInfoId(String tradeInfoId) {
        this.tradeInfoId = tradeInfoId;
    }

    public String getTradeInfoTitle() {
        return tradeInfoTitle;
    }

    public void setTradeInfoTitle(String tradeInfoTitle) {
        this.tradeInfoTitle = tradeInfoTitle;
    }

    public String getFbUserCode() {
        return fbUserCode;
    }

    public void setFbUserCode(String fbUserCode) {
        this.fbUserCode = fbUserCode;
    }

    public String getFbUserName() {
        return fbUserName;
    }

    public void setFbUserName(String fbUserName) {
        this.fbUserName = fbUserName;
    }

    public String getFbUserPic() {
        return fbUserPic;
    }

    public void setFbUserPic(String fbUserPic) {
        this.fbUserPic = fbUserPic;
    }

    public String getYxUserCode() {
        return yxUserCode;
    }

    public void setYxUserCode(String yxUserCode) {
        this.yxUserCode = yxUserCode;
    }

    public String getYxUserName() {
        return yxUserName;
    }

    public void setYxUserName(String yxUserName) {
        this.yxUserName = yxUserName;
    }

    public String getYxUserPic() {
        return yxUserPic;
    }

    public void setYxUserPic(String yxUserPic) {
        this.yxUserPic = yxUserPic;
    }

    public String getPtUserCode() {
        return ptUserCode;
    }

    public void setPtUserCode(String ptUserCode) {
        this.ptUserCode = ptUserCode;
    }

    public String getPtUserName() {
        return ptUserName;
    }

    public void setPtUserName(String ptUserName) {
        this.ptUserName = ptUserName;
    }

    public String getPtUserPic() {
        return ptUserPic;
    }

    public void setPtUserPic(String ptUserPic) {
        this.ptUserPic = ptUserPic;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
