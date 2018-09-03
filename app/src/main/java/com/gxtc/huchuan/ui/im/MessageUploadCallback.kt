package com.gxtc.huchuan.ui.im

import com.gxtc.huchuan.bean.UploadResult
import com.gxtc.huchuan.http.LoadHelper
import io.rong.imlib.model.Message

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/3/13.
 */
abstract class MessageUploadCallback(var message: Message): LoadHelper.UploadCallback {

    override fun onUploadSuccess(result: UploadResult?) = onUploadSuccess(result, message)

    override fun onUploadFailed(errorCode: String?, msg: String?) = onUploadFailed(errorCode, msg, message)

    abstract fun onUploadSuccess(data: Any?, message: Message)

    abstract fun onUploadFailed(errorCode: String?, errorMsg: String?, message: Message)
}