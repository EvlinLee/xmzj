package com.gxtc.huchuan.im.event;

import android.net.Uri;
import android.support.annotation.IntDef;

import com.gxtc.huchuan.bean.ChatInfosBean;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Gubr on 2017/3/8.
 * 这里有三种事件
 * 1.开始播放
 * 2.结束播放
 * 3.播放状态转换
 */

public class PlayEvent {


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({START_STATUS, PAUSE_STATUS, TOGGLE_STATUS, CLICK_STATUS})
    public @interface Status {
    }

    public static final int START_STATUS  = 1;
    public static final int PAUSE_STATUS  = 1 << 1;
    public static final int TOGGLE_STATUS = 1 << 2;
    public static final int CLICK_STATUS  = 1 << 3;

    private int mStaus = PAUSE_STATUS;


    ChatInfosBean mChatInfosBean;


    /**
     * 课程的id
     */
    public int topicId;

    /**
     * 课程的头像
     */
    public Uri mUri;


    public PlayEvent(ChatInfosBean chatInfosBean, @Status int flag) {
        mChatInfosBean = chatInfosBean;
        mStaus = flag;
    }

    public ChatInfosBean getChatInfosBean() {
        return mChatInfosBean;
    }

    public String getUri() {
        return mChatInfosBean.getChatRoomHeadPic();
    }

    public String getChatInfosId() {
        return mChatInfosBean == null ? "" : mChatInfosBean.getId();
    }

    public void setstatus(@Status int flag) {
        mStaus = flag;
    }

    public int getStaus() {
        return mStaus;
    }

}
