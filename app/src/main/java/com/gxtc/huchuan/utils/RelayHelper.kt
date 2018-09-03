package com.gxtc.huchuan.utils

import android.text.TextUtils
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.MyApplication
import io.rong.imkit.RongIM
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.message.TextMessage

/** 动态这里只转发文字
 * Created by zzg on 2018/2/22.
 */
object RelayHelper {

     fun relayMessage(content: String, targetId: String, conversationType: Conversation.ConversationType,liuyan:String) {
        var pushContent = ""
        val messageContent = TextMessage.obtain(content)
        val message = ImMessageUtils.obtain(targetId, conversationType, messageContent)
        RongIM.getInstance().sendMessage(message, pushContent, pushContent, object : IRongCallback.ISendMessageCallback {
            override fun onAttached(message: Message) {}

            override fun onSuccess(message: Message) {
                ToastUtil.showShort(MyApplication.getInstance(), "转发成功")
                if(!TextUtils.isEmpty(liuyan)){
                    RongIMTextUtil.relayMessage(liuyan,targetId,conversationType)
                }
            }

            override fun onError(message: Message, errorCode: RongIMClient.ErrorCode) {
                ToastUtil.showShort(MyApplication.getInstance(),RIMErrorCodeUtil.handleErrorCode(errorCode))
            }
        })
    }
}