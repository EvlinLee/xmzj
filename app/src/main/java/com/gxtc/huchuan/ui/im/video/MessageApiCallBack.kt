package com.gxtc.huchuan.ui.im.video

import com.gxtc.huchuan.http.ApiCallBack
import io.rong.imlib.model.Message

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/12/15.
 */
abstract class MessageApiCallBack<T>(var message: Message): ApiCallBack<T>() {

    override fun onSuccess(data: T?) {
        onSuccess(data, message)
    }

    override fun onError(errorCode: String?, message: String?) {
        onError(errorCode, message, this.message)
    }

    abstract fun onSuccess(data: Any?, message: Message)

    abstract fun onError(errorCode: String?, errorMsg: String?, message: Message)
}