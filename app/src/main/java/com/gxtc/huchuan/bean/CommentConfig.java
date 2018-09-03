package com.gxtc.huchuan.bean;

import android.view.View;

/**
 * Created by yiwei on 16/3/2.
 *
 */
public class CommentConfig {
    public int commentId;

    public static enum Type{
        PUBLIC("public"), REPLY("reply");

        private String value;

        private Type(String value){
            this.value = value;
        }
    }

    public String targetUserName;

    public int groupInfoId;
    public int circlePosition;
    public int commentPosition;
    public Type commentType;
    public String targetUserCode;
    public int commentCount;
    public Integer groupId;
    public View item;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        String replyUserStr = "";
        return "circlePosition = " + circlePosition
                + "; commentPosition = " + commentPosition
                + "; commentType ＝ " + commentType
                + "; replyUser = " + replyUserStr;
    }
}
