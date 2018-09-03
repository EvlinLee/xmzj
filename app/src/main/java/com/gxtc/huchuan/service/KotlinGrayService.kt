package com.gxtc.huchuan.service

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.gxtc.huchuan.receiver.KotlinWakeReceiver

/**
 * Created by zzg on 2017/8/10.
 */
class KotlinGrayService : Service() {
    /**定义静态变量*/
    companion object{
         val TAG = "KotlinGrayService"
         val ALARM_INTERVAL : Long = 5 * 60 * 1000
         val WAKE_REQUEST_CODE = 6666
         val GRAY_SERVICE_ID = -1001
          class GrayInnerService : Service(){
            override fun onBind(intent: Intent?): IBinder =  throw UnsupportedOperationException("Not yet implemented")


            override fun onCreate() {
                super.onCreate()
                Log.d(TAG, "InnerService -> onCreate")
            }

            override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
                Log.d(TAG, "InnerService -> onStartCommand")
                startForeground(GRAY_SERVICE_ID, Notification())
                stopSelf()
                return super.onStartCommand(intent, flags, startId)
            }

            override fun onDestroy() = super.onDestroy()
        }
    }
    override fun onBind(intent: Intent?): IBinder = throw UnsupportedOperationException("Not yet implemented")

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            if(Build.VERSION.SDK_INT < 18){
                startForeground(GRAY_SERVICE_ID, Notification())//API < 18 ，此方法能有效隐藏Notification上的图标
            }else{
                Log.d(TAG, "KotlinGrayService -> onStartCommand")
                var innerIntent=Intent(this,GrayInnerService::class.java);
                startService(innerIntent)
                startForeground(GRAY_SERVICE_ID,Notification())
            }
            //发送唤醒广播来促使挂掉的UI进程重新启动起来
            var alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            var alarmIntent = Intent()
            alarmIntent.action = KotlinWakeReceiver.GRAY_WAKE_ACTION
            var operation = PendingIntent.getBroadcast(this, WAKE_REQUEST_CODE,alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ALARM_INTERVAL, operation)
        }catch (e:Exception){
            e.printStackTrace()
        }

        return START_STICKY
    }
}