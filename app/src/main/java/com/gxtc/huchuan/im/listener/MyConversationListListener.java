package com.gxtc.huchuan.im.listener;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.model.Conversation;

public class MyConversationListListener implements RongIM.ConversationListBehaviorListener{
    /**
     * 当点击会话头像后执行。
     *
     * @param context          上下文。
     * @param conversationType 会话类型。
     * @return 如果用户自己处理了点击后的逻辑处理，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onConversationPortraitClick(Context context,
            Conversation.ConversationType conversationType, String s) {
        return false;
    }

    /**
     * 当长按会话头像后执行。
     *
     * @param context          上下文。
     * @param conversationType 会话类型。
     * @return 如果用户自己处理了点击后的逻辑处理，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onConversationPortraitLongClick(Context context,
            Conversation.ConversationType conversationType, String s) {
        return false;
    }

    /**
     * 长按会话列表中的 item 时执行。
     *
     * @param context        上下文。
     * @param view           触发点击的 View。
     * @param uiConversation 长按时的会话条目。
     * @return 如果用户自己处理了长按会话后的逻辑处理，则返回 true， 否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onConversationLongClick(Context context, View view,
            UIConversation uiConversation) {
        return false;
    }

    @Override
    public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
        //私聊
        if(uiConversation.getConversationType() == Conversation.ConversationType.PRIVATE){
            Uri uri = Uri.parse("rong://" + context.getPackageName()).buildUpon()
                         .appendPath("conversation")
                         .appendPath(Conversation.ConversationType.PRIVATE.getName().toLowerCase())
                         .appendQueryParameter("targetId", uiConversation.getConversationTargetId())
                         .appendQueryParameter("title", uiConversation.getUIConversationTitle())
                         .appendQueryParameter("flag","1").build();
            context.startActivity(new Intent("android.intent.action.VIEW", uri));
            return true;
        //群聊
        }else{
            return false;
        }
    }
}