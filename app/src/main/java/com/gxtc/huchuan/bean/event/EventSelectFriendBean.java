package com.gxtc.huchuan.bean.event;

import android.os.Parcel;
import android.os.Parcelable;

import io.rong.common.ParcelUtils;
import io.rong.imlib.model.Conversation;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/30.
 */

public class EventSelectFriendBean implements Parcelable{

    public String targetId;
    public String liuyan;
    public Conversation.ConversationType mType;

    public EventSelectFriendBean(String targetId, Conversation.ConversationType type,String liuyan) {
        this.targetId = targetId;
        this.liuyan = liuyan;
        mType = type;
    }

    protected EventSelectFriendBean(Parcel in) {
        targetId = ParcelUtils.readFromParcel(in);
        liuyan = ParcelUtils.readFromParcel(in);
        mType = Conversation.ConversationType.values()[ParcelUtils.readIntFromParcel(in)];
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest,targetId);
        ParcelUtils.writeToParcel(dest,liuyan);
        ParcelUtils.writeToParcel(dest,mType.ordinal());
    }

    public static final Creator<EventSelectFriendBean> CREATOR = new Creator<EventSelectFriendBean>() {
        @Override
        public EventSelectFriendBean createFromParcel(Parcel in) {
            return new EventSelectFriendBean(in);
        }

        @Override
        public EventSelectFriendBean[] newArray(int size) {
            return new EventSelectFriendBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


}
