package com.gxtc.huchuan.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.gxtc.commlibrary.utils.LogUtil
import com.gxtc.commlibrary.utils.SpUtil
import com.gxtc.huchuan.background.OnepxActivity

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/1/3.
 */
class BootCompletedReceiver: BroadcastReceiver() {

    val action  = Intent.ACTION_BOOT_COMPLETED

    companion object {
        @JvmField
        val KEY_BOOT = "BOOT_COMPLETED"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == action){
            LogUtil.i("==========监听到系统开机广播==============")
            SpUtil.putBoolean(context, KEY_BOOT, true)
            val intent = Intent(context, OnepxActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(intent)
        }
    }
}


/**
 * 系统关机广播
 */
class BootOffReceiver: BroadcastReceiver(){

    val action  = Intent.ACTION_SHUTDOWN

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == action){
            LogUtil.i("==========监听到系统关机广播==============")
            SpUtil.putBoolean(context, BootCompletedReceiver.KEY_BOOT, false)
        }
    }

}