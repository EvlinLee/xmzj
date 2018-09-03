package com.gxtc.huchuan.utils

import android.content.Context
import android.util.Log
import com.gxtc.commlibrary.utils.LogUtil
import com.gxtc.commlibrary.utils.SpUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.ui.mine.setting.MessageSettingActivity
import com.gxtc.huchuan.ui.mine.setting.SettingActivity
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient

/**
 * Created by zzg on 2018/3/7.
 * 设置融云会话提示的声音是否打开
 */
object RIMSoundHandler {

    fun setRongIMSounds(applicationContext: Context, isOpen:Boolean) = if (isOpen) {
        /**
         * 移除消息免打扰时间
         */
        RongIM.getInstance().removeNotificationQuietHours(object : RongIMClient.OperationCallback() {
            override fun onSuccess() {
                SpUtil.putBoolean(applicationContext, SettingActivity.SP_SOUND(), true)
            }

            override fun onError(errorCode: RongIMClient.ErrorCode) {
                ToastUtil.showShort(applicationContext,RIMErrorCodeUtil.handleErrorCode(errorCode))
            }
        })
    } else {
        /**
         * 设置消息通知免打扰
         */
        RongIM.getInstance().setNotificationQuietHours("00:00:00", MessageSettingActivity.MAXNUM, object : RongIMClient.OperationCallback() {
            override fun onSuccess() {
                SpUtil.putBoolean(applicationContext, SettingActivity.SP_SOUND(), false)
            }

            override fun onError(errorCode: RongIMClient.ErrorCode) {
                ToastUtil.showShort(applicationContext,RIMErrorCodeUtil.handleErrorCode(errorCode))
            }
        })
    }
}