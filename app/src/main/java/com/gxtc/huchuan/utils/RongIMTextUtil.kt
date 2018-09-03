package com.gxtc.huchuan.utils

import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.MyApplication
import io.rong.imkit.RongIM
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.message.TextMessage

/**
 * Created by zzg on 2018/2/23.
 * 用于发送给朋友的留言的（在转发或是分享时弹出的框里有给朋友的留言）
 */
object RongIMTextUtil {

    fun relayMessage(content: String?, targetId: String?, conversationType: Conversation.ConversationType?) {
        val pushContent = content
        val messageContent = TextMessage.obtain(content)
        val message = ImMessageUtils.obtain(targetId, conversationType, messageContent)

        RongIM.getInstance().sendMessage(message, pushContent, pushContent, object : IRongCallback.ISendMessageCallback {
            override fun onAttached(message: Message) {}

            override fun onSuccess(message: Message) {}

            override fun onError(message: Message, errorCode: RongIMClient.ErrorCode) {
                ToastUtil.showShort(MyApplication.getInstance(),RIMErrorCodeUtil.handleErrorCode(errorCode))
            }
        })
    }
}