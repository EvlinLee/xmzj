package com.gxtc.huchuan.bean.event

import android.content.Intent
import com.gxtc.huchuan.bean.ChatInfosBean
import com.gxtc.huchuan.bean.UpdataBean
import io.rong.imlib.model.Message

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/12/26.
 */

//重新上传小视频
data class EventUploadVideoBean(var message: Message ? = null)


//视频详情页面操作菜单
data class EventHandleVideoBean(var requestCode: Int, var resultCode: Int, var data: Intent?){

    companion object {
        @JvmField val ACTION_SAVE = 0               //保存视频
        @JvmField val ACTION_SHARE_FRIENDS = 1      //分享好友
        @JvmField val ACTION_COLLECT = 2            //收藏视频
    }
}


data class EventUpdataBean(var updata: UpdataBean?)

data class EventConverListBean(var count: Int = 0)

class EventSeriesInviteBean()

data class EventLiveRefreshBean(var infoBean: ChatInfosBean ?)