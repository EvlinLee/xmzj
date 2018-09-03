package com.gxtc.huchuan.bean.event;

import android.os.Parcel;
import android.os.Parcelable;

import io.rong.common.ParcelUtils;
import io.rong.imlib.model.Conversation;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/30.
 */

public class EventSelectFriendForPostCardBean implements Parcelable{

    public String targetId;
    public String userCode;
    public String name;
    public String picHead;
    public String liuyan;
    public Conversation.ConversationType mType;

    public EventSelectFriendForPostCardBean(String targetId, String userCode, String name, String picHead, Conversation.ConversationType mType,String liuyan) {
        this.targetId = targetId;
        this.userCode = userCode;
        this.name = name;
        this.picHead = picHead;
        this.mType = mType;
        this.liuyan = liuyan;
    }

    protected EventSelectFriendForPostCardBean(Parcel in) {
        targetId = ParcelUtils.readFromParcel(in);
        userCode = ParcelUtils.readFromParcel(in);
        name = ParcelUtils.readFromParcel(in);
        picHead = ParcelUtils.readFromParcel(in);
        liuyan = ParcelUtils.readFromParcel(in);
        mType = Conversation.ConversationType.values()[ParcelUtils.readIntFromParcel(in)];
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest,targetId);
        ParcelUtils.writeToParcel(dest,userCode);
        ParcelUtils.writeToParcel(dest,name);
        ParcelUtils.writeToParcel(dest,picHead);
        ParcelUtils.writeToParcel(dest,liuyan);
        ParcelUtils.writeToParcel(dest,mType.ordinal());
    }

    public static final Creator<EventSelectFriendForPostCardBean> CREATOR = new Creator<EventSelectFriendForPostCardBean>() {
        @Override
        public EventSelectFriendForPostCardBean createFromParcel(Parcel in) {
            return new EventSelectFriendForPostCardBean(in);
        }

        @Override
        public EventSelectFriendForPostCardBean[] newArray(int size) {
            return new EventSelectFriendForPostCardBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


}
