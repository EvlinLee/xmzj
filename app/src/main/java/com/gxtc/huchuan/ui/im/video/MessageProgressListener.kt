package com.gxtc.huchuan.ui.im.video

import cn.edu.zafu.coreprogress.listener.impl.UIProgressListener
import io.rong.imlib.model.Message

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/12/15.
 * 视频消息上传进度监听
 */
abstract class MessageProgressListener(var message: Message): UIProgressListener() {

    override fun onUIProgress(currentBytes: Long, contentLength: Long, done: Boolean, position: Int) {
        onUIProgress(currentBytes, contentLength, done, position, message)
    }

    abstract fun onUIProgress(currentBytes: Long, contentLength: Long, done: Boolean, position: Int, message: Message?)
}

