package com.gxtc.huchuan.receiver

import android.annotation.TargetApi
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/1/3.
 */
object JobSchedulerManager {

    val JOB_ID = 1

    @TargetApi(21)
    fun startJobScheduler(context: Context) {
        val mJobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        // 如果JobService已经启动或API<21，返回
        if (AliveJobService.isJobServiceAlive() || isBelowLOLLIPOP()) {
            return
        }
        // 构建JobInfo对象，传递给JobSchedulerService
        val builder = JobInfo.Builder(JOB_ID, ComponentName(context, AliveJobService::class.java))
        // 设置1分钟执行一下任务
        builder.setPeriodic(2000)
        // 设置设备重启时，执行该任务
        builder.setPersisted(true)
        // 当插入充电器，执行该任务
        builder.setRequiresCharging(true)
        val info = builder.build()
        //开始定时执行该系统任务
        mJobScheduler.schedule(info)
    }

    @TargetApi(21)
    fun stopJobScheduler(context: Context) {
        if (isBelowLOLLIPOP())
            return
        val mJobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        mJobScheduler.cancelAll()
    }

    private fun isBelowLOLLIPOP(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
    }

}