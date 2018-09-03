package com.gxtc.huchuan.bean;

import java.io.Serializable;

import io.rong.imlib.model.Conversation;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/6.
 */

public class RedPacketBean implements Serializable{

    private static final long serialVersionUID = 1L;

    private int    totalNum;        //红包个数
    private int    isFinish;        //是否已领完。 0、未完成；1、已完成
    private int    num;             //剩余数量
    private int    isSnatch;        //当前用户是否已领取。 0、未领取；1、已领取
    private int    type;            //0：个人，1：群组
    private int    allotType;       //分配类型。0、随机；1、平均
    private long   createTime;      //发布时间
    private long   finishTime;      //领完时间
    private double amt;             //当前用户领取金额
    private double totalAmt;        //红包总金额
    private String targetId;
    private String    id;
    private String message;         //留言
    private String userCode;        //发红包用户编码
    private String userName;        //发红包用户名称
    private String userPic;         //发红包用户头像
    private Conversation.ConversationType conversationType;

    /*
     * 红包领取列表
     */
    private int isMax;              //1是最大
    private double rewardAmt;       //领取金额

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAllotType() {
        return allotType;
    }

    public void setAllotType(int allotType) {
        this.allotType = allotType;
    }

    public int getIsMax() {
        return isMax;
    }

    public void setIsMax(int isMax) {
        this.isMax = isMax;
    }

    public double getRewardAmt() {
        return rewardAmt;
    }

    public void setRewardAmt(double rewardAmt) {
        this.rewardAmt = rewardAmt;
    }


    public double getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(double totalAmt) {
        this.totalAmt = totalAmt;
    }

    public Conversation.ConversationType getConversationType() {
        return conversationType;
    }

    public void setConversationType(Conversation.ConversationType conversationType) {
        this.conversationType = conversationType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(int isFinish) {
        this.isFinish = isFinish;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getIsSnatch() {
        return isSnatch;
    }

    public void setIsSnatch(int isSnatch) {
        this.isSnatch = isSnatch;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public double getAmt() {
        return amt;
    }

    public void setAmt(double amt) {
        this.amt = amt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }
}
