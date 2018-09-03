package com.gxtc.huchuan.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by 宋家任 on 2017/4/26.
 * 圈子实体
 */

public class MineCircleBean implements Parcelable {


    /**
     * attention : 1
     * content : 流量大王帮助你
     * cover : https://xmzjvip.b0.upaiyun.com/xmzj/20551494574130104.png
     * fee : 0
     * groupName : 流量大王
     * id : 44
     * infoNum : 6
     * isFee : 0
     * isJoin : 1
     * isMy : 1
     * isShow : 0
     * joinUrl : http://app.xinmei6.com/html/circleIntro.html?groupId=44
     * memberType : 2
     */

    private int attention;
    private String content;
    private String cover;
    private double fee;
    private String groupName;
    private String userName;
    private int id;
    private int infoNum;
    private int isFee;
    private int isJoin;
    private int isMy;               //是否是圈主。0:否；1：是
    private int isShow;             //审核状态。0、未审核；1、已审核；2、审核不通
    private String joinUrl;
    private int memberType;         //成员类型。0:普通用户，1：管理员，2：圈主
    private boolean isCheck;        //是否选中
    private boolean noCheck;        //不可被点击
    private boolean isFirst;        //是否显示在圈子首页
    private double pent;            //分销

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public double getPent() {
        return pent;
    }

    public void setPent(double pent) {
        this.pent = pent;
    }

    public boolean isNoCheck() {
        return noCheck;
    }

    public void setNoCheck(boolean noCheck) {
        this.noCheck = noCheck;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public int getAttention() {
        return attention;
    }

    public void setAttention(int attention) {
        this.attention = attention;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInfoNum() {
        return infoNum;
    }

    public void setInfoNum(int infoNum) {
        this.infoNum = infoNum;
    }

    public int getIsFee() {
        return isFee;
    }

    public void setIsFee(int isFee) {
        this.isFee = isFee;
    }

    public int getIsJoin() {
        return isJoin;
    }

    public void setIsJoin(int isJoin) {
        this.isJoin = isJoin;
    }

    public int getIsMy() {
        return isMy;
    }

    public void setIsMy(int isMy) {
        this.isMy = isMy;
    }

    public int getIsShow() {
        return isShow;
    }

    public void setIsShow(int isShow) {
        this.isShow = isShow;
    }

    public String getJoinUrl() {
        return joinUrl;
    }

    public void setJoinUrl(String joinUrl) {
        this.joinUrl = joinUrl;
    }

    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MineCircleBean that = (MineCircleBean) o;

        return id == that.id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.attention);
        dest.writeString(this.content);
        dest.writeString(this.cover);
        dest.writeDouble(this.fee);
        dest.writeString(this.groupName);
        dest.writeInt(this.id);
        dest.writeInt(this.infoNum);
        dest.writeInt(this.isFee);
        dest.writeInt(this.isJoin);
        dest.writeInt(this.isMy);
        dest.writeInt(this.isShow);
        dest.writeString(this.joinUrl);
        dest.writeInt(this.memberType);
    }

    public MineCircleBean() {
    }

    protected MineCircleBean(Parcel in) {
        this.attention = in.readInt();
        this.content = in.readString();
        this.cover = in.readString();
        this.fee = in.readDouble();
        this.groupName = in.readString();
        this.id = in.readInt();
        this.infoNum = in.readInt();
        this.isFee = in.readInt();
        this.isJoin = in.readInt();
        this.isMy = in.readInt();
        this.isShow = in.readInt();
        this.joinUrl = in.readString();
        this.memberType = in.readInt();
    }

    public static final Creator<MineCircleBean> CREATOR = new Creator<MineCircleBean>() {
        @Override
        public MineCircleBean createFromParcel(Parcel source) {
            return new MineCircleBean(source);
        }

        @Override
        public MineCircleBean[] newArray(int size) {
            return new MineCircleBean[size];
        }
    };
}
