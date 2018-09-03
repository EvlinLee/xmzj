package com.gxtc.huchuan.helper

import android.media.MediaMetadataRetriever
import android.text.TextUtils
import android.util.SparseArray
import com.gxtc.commlibrary.utils.EventBusUtil
import com.gxtc.commlibrary.utils.NetworkUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.bean.event.EventLoadBean
import com.gxtc.huchuan.im.ui.ConversationActivity
import com.gxtc.huchuan.ui.im.video.VideoMessage
import com.gxtc.huchuan.utils.ImMessageUtils
import com.gxtc.huchuan.utils.RongIMTextUtil
import com.gxtc.huchuan.utils.StringUtil
import io.rong.imkit.RongIM
import io.rong.imlib.IRongCallback
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.imlib.model.MessageContent
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.SocketTimeoutException
import java.util.*

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/2/10.
 * 分享内容到聊天工具类
 */
object ShareHelper {

    var builder: Builder ? = null
        get() {
            if(field == null){
                field = Builder()
            }
            return field
        }

    class Builder{

        var targetId: String ? = null                     //会话id

        var type: Conversation.ConversationType ? = null  //会话类型

        var videoUrl: String ? = null       //视频url

        var videoCover: String ? = null     //视频封面

        var liuyan: String ? = null     //留言

        /**
         * 分享类型
         * @see ConversationActivity.REQUEST_SHARE_IMAGE ......
         */
        var action: Int ? = null

        fun targetId(id: String): Builder {
            this.targetId = id
            return this@Builder
        }

        fun type(type: Conversation.ConversationType): Builder {
            this.type = type
            return this@Builder
        }

        fun videoUrl(url: String): Builder {
            this.videoUrl = url
            return this@Builder
        }

        fun videoCover(cover: String): Builder {
            this.videoCover = cover
            return this@Builder
        }

        fun liuyan(liuyan: String): Builder {
            this.liuyan = liuyan
            return this@Builder
        }

        fun action(action: Int): Builder {
            this.action = action
            return this@Builder
        }

        fun toShare() = share()
    }

    fun share() =
        if(RongIM.getInstance().currentConnectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED){
            when(builder?.action){
                ConversationActivity.REQUEST_SHARE_VIDEO -> getVideoInfo()

                else -> Unit
            }
        }else{
            ToastUtil.showShort(MyApplication.getInstance(), "请重新登录")
        }

    fun shareVideoMessage(width: Int, height: Int, time: Int) {
        if(TextUtils.isEmpty(builder?.videoCover)){
            ToastUtil.showShort(MyApplication.getInstance(), "发送视频失败")
            return
        }
        val vMessage = VideoMessage()
        vMessage.url = builder?.videoUrl
        vMessage.cover = builder?.videoCover
        vMessage.duration = time.toLong()
        vMessage.width = width
        vMessage.height = height

        shareMessage(builder?.targetId, builder?.type,builder?.liuyan, vMessage, "[视频]")
    }


    fun shareMessage(targetId: String?, type: Conversation.ConversationType?,liuyan:String?, messageContent: MessageContent, desc: String) =
            RongIM.getInstance().sendMessage(ImMessageUtils.obtain(targetId, type, messageContent), desc, desc, object : IRongCallback.ISendMessageCallback {
                override fun onAttached(message: Message) = Unit

                override fun onSuccess(message: Message) {
                    ToastUtil.showShort(MyApplication.getInstance(), "发送成功")
                    if(!TextUtils.isEmpty(liuyan)){
                        RongIMTextUtil.relayMessage(liuyan,targetId,type)
                    }
                }

                override fun onError(message: Message, errorCode: RongIMClient.ErrorCode) =
                        ToastUtil.showShort(MyApplication.getInstance(), "发送失败")
            })


    /**
     * 获取视频的信息
     */
    private fun getVideoInfo() {
        val targetId = builder?.targetId
        val type = builder?.type
        val url = builder?.videoUrl
        if(TextUtils.isEmpty(targetId) || type == null || TextUtils.isEmpty(url)){
            return
        }

        EventBusUtil.post(EventLoadBean(true))
        val sub = Observable.just<String>(url)
                .subscribeOn(Schedulers.io())
                .map { s ->
                    val retr = MediaMetadataRetriever()
                    retr.setDataSource(s, HashMap())
                    val bitmap = retr.getFrameAtTime(0)     //事实证明 MediaMetadataRetriever 直接获取宽高不准确默认最长的边为宽, 所以通过图片来获取
                    val width = bitmap.width.toString()
                    val height = bitmap.height.toString()
                    val time = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)       // 视频时长
                    val temp = SparseArray<String>()
                    temp.put(0, width)
                    temp.put(1, height)
                    temp.put(2, time)
                    temp
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<SparseArray<String>>() {
                    override fun onCompleted() = Unit

                    override fun onError(e: Throwable) {
                        EventBusUtil.post(EventLoadBean(false))
                        if (e is SocketTimeoutException) {
                            ToastUtil.showShort(MyApplication.getInstance(), "网络连接超时，请检查网络设置")
                        } else if (!NetworkUtil.isConnected(MyApplication.getInstance())) {
                            ToastUtil.showShort(MyApplication.getInstance(), "未发现网络连接，请检查网络设置")
                        } else {
                            ToastUtil.showShort(MyApplication.getInstance(), "发送失败")
                        }
                    }

                    override fun onNext(param: SparseArray<String>?) {
                        EventBusUtil.post(EventLoadBean(false))
                        if (param != null) {
                            val width = StringUtil.toInt(param.get(0))
                            val height = StringUtil.toInt(param.get(1))
                            val time = StringUtil.toInt(param.get(2)) / 1000
                            shareVideoMessage(width, height, time)
                        }
                    }
                })

        RxTaskHelper.getInstance().addTask(this, sub)
    }
}