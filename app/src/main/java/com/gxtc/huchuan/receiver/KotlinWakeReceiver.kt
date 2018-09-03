package com.gxtc.huchuan.receiver

import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log

/**
 * Created by Administrator on 2017/8/10.
 */
class KotlinWakeReceiver : BroadcastReceiver() {
    companion object{
        val TAG = "KotlinWakeReceiver"
        val WAKE_SERVICE_ID = -1111
        val GRAY_WAKE_ACTION = "com.wake.gray"

        class WakeNotifyService : Service(){
            override fun onBind(intent: Intent?): IBinder = throw UnsupportedOperationException("Not yet implemented")


            override fun onCreate() {
                super.onCreate()
            }
            override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
                if (Build.VERSION.SDK_INT < 18) {
                    startForeground(WAKE_SERVICE_ID,  Notification());//API < 18 ，此方法能有效隐藏Notification上的图标
                } else {
                    try{
                        var innerIntent =  Intent(this, WakeGrayInnerService::class.java);
                        startService(innerIntent);
                        startForeground(WAKE_SERVICE_ID,  Notification());
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
                return START_STICKY;
            }
        }

        class  WakeGrayInnerService : Service(){
            override fun onBind(intent: Intent?): IBinder = throw UnsupportedOperationException("Not yet implemented")

            override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
                startForeground(WAKE_SERVICE_ID, Notification())
                stopSelf()
                return super.onStartCommand(intent, flags, startId)
            }

            override fun onDestroy() {
                super.onDestroy()
                Log.d(TAG, "InnerService -> onDestroy")
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            val action = intent?.getAction();
            if (GRAY_WAKE_ACTION.equals(action)) {
                Log.d(TAG, "wake !! wake !! ");
                val wakeIntent =  Intent(context, WakeNotifyService::class.java);
                context?.startService(wakeIntent);
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }
}