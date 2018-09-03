package com.gxtc.huchuan.receiver

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Message
import android.support.annotation.RequiresApi
import android.util.Log
import com.gxtc.commlibrary.utils.LogUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.service.KotlinGrayService
import com.gxtc.huchuan.utils.SystemTools

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/1/3.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class AliveJobService: JobService() {


    private val MESSAGE_ID_TASK = 0x01

    companion object {
        // 告知编译器，这个变量不能被优化
        @Volatile private var mKeepAliveService: Service? = null

        @JvmStatic
        fun isJobServiceAlive(): Boolean = mKeepAliveService != null
    }

    private val mHandler = Handler(Handler.Callback { msg ->
        // 具体任务逻辑
        if (SystemTools.isAPPLive(applicationContext, Constant.PACKAGE_NAME)) {
            //LogUtil.i("app 存活")
        } else {
            try {
                if(Build.VERSION.SDK_INT < 18){
                    startForeground(KotlinGrayService.GRAY_SERVICE_ID, Notification())  //API < 18 ，此方法能有效隐藏Notification上的图标
                }else{
                    var innerIntent=Intent(this, KotlinGrayService.Companion.GrayInnerService::class.java);
                    startService(innerIntent)
                    startForeground(KotlinGrayService.GRAY_SERVICE_ID, Notification())
                }
                //发送唤醒广播来促使挂掉的UI进程重新启动起来
                var alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                var alarmIntent = Intent()
                alarmIntent.action = KotlinWakeReceiver.GRAY_WAKE_ACTION
                var operation = PendingIntent.getBroadcast(this, KotlinGrayService.WAKE_REQUEST_CODE,alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), KotlinGrayService.ALARM_INTERVAL, operation)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        // 通知系统任务执行结束
        jobFinished(msg.obj as JobParameters, false)
        true
    })

    override fun onStartJob(params: JobParameters?): Boolean {
        //LogUtil.i("KeepAliveService----->JobService服务被启动...")
        mKeepAliveService = this
        // 返回false，系统假设这个方法返回时任务已经执行完毕；
        // 返回true，系统假定这个任务正要被执行
        val msg = Message.obtain(mHandler, MESSAGE_ID_TASK, params)
        mHandler.sendMessage(msg)
        return true
    }


    override fun onStopJob(params: JobParameters?): Boolean {
        mHandler.removeMessages(MESSAGE_ID_TASK)
        LogUtil.i("KeepAliveService----->JobService服务被关闭")
        return false
    }

}