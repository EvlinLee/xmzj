package com.gxtc.huchuan.service

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import cn.edu.zafu.coreprogress.progress.ProgressResponseBody
import com.gxtc.commlibrary.utils.FileStorage
import com.gxtc.commlibrary.utils.LogUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.http.DownLoadManager
import com.gxtc.huchuan.http.DownloadProgressListener
import com.gxtc.huchuan.http.service.DownloadApi
import com.gxtc.huchuan.ui.MainActivity
import com.gxtc.huchuan.utils.SystemTools
import okhttp3.ResponseBody
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import java.io.File


/**
 * 来自 伍玉南 的装逼小尾巴 on 18/2/8.
 * 后台下载服务
 */
class DownloadService() : IntentService(ACTION) {

    private var downloadQueue: LinkedHashMap<String, String>? = null

    companion object {

        @JvmField
        val ACTION = "com.gxtc.huchuan.service.downloadService"

        @JvmField
        val TYPE_VIDEO = 0X00

        @JvmField
        val TYPE_APK = 0X01

        @JvmStatic
        fun startDownload(context: Context, url: String?, type: Int = 0) {
            val intent = Intent(context, DownloadService::class.java)
            intent.putExtra(Constant.INTENT_DATA, url)
            intent.putExtra("type", type)
            context.startService(intent)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            if (downloadQueue == null) {
                downloadQueue = LinkedHashMap()
            }
            val url = intent.getStringExtra(Constant.INTENT_DATA)
            val type = intent.getIntExtra("type", TYPE_VIDEO)

            url?.let {
                download(it, type)
            }
        }
    }

    private fun download(url: String, type: Int) {
        if (!TextUtils.isEmpty(downloadQueue?.get(url))) {
            ToastUtil.showShort(baseContext, "当前正在下载，请稍后再试")
            return
        }

        LogUtil.i("download url : " + url)

        if (url.contains(".")) {
            val suffix = url.substring(url.lastIndexOf("."), url.length)
            val fileName = FileStorage.getCurTime("MM-dd-HH-mm-ss") + suffix
            var fileDir = ""
            when (type) {
                TYPE_VIDEO -> fileDir = FileStorage.getVideoCacheFile().absolutePath

                TYPE_APK -> fileDir = FileStorage.getAppPath()!!.absolutePath
            }

            if (fileDir.isNotEmpty()) {
                val filePath = fileDir + "/" + fileName
                downloadQueue?.put(url, filePath)

                DownloadApi.getInstance()
                        .downloadFile(url)
                        .map(Func1<ResponseBody, Boolean> { body ->
                            val progressBody = ProgressResponseBody(body, DownloadListener(baseContext, url))
                            return@Func1 DownLoadManager.writeFileToSDCard(progressBody, filePath)
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : Subscriber<Boolean>() {
                            override fun onNext(b: Boolean?) = if (b!!) {
                                downloadFinish(url, type)
                            } else {
                                downloadFailure(url)
                            }

                            override fun onCompleted() = Unit

                            override fun onError(e: Throwable?) {
                                e?.printStackTrace()
                                downloadFailure(url)
                            }
                        })
                ToastUtil.showShort(baseContext, "下载文件失败")

            } else {
                ToastUtil.showShort(baseContext, "UFO出现了，好紧张~~")
            }

        } else {
            ToastUtil.showShort(baseContext, "不支持下载该文件")
        }
    }


    private fun downloadFinish(url: String, type: Int) {
        val path = downloadQueue?.remove(url)

        when (type) {
            TYPE_VIDEO -> ToastUtil.showLong(baseContext, "已保存至: " + path)

            TYPE_APK -> {
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                intent.addCategory("android.intent.category.DEFAULT")
                intent.setDataAndType(Uri.fromFile(File(path)), "application/vnd.android.package-archive")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                //直接跳转安装界面
                baseContext.startActivity(intent)

                val pi = PendingIntent.getActivity(baseContext, 0, intent, 0)
                val builder = NotificationCompat.Builder(baseContext)
                builder.setSmallIcon(R.mipmap.ic_launcher)
                builder.setLargeIcon(BitmapFactory.decodeResource(baseContext?.resources, R.mipmap.ic_launcher))
                builder.setContentIntent(pi)
                builder.setContentText("100%")
                builder.setProgress(100, 100, false)
                builder.setContentTitle("下载完成")
                val notice = builder.build()
                    val manager = baseContext?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                manager?.notify(1, notice)


            }
        }
    }


    private fun downloadFailure(url: String) {
        downloadQueue?.remove(url)
        ToastUtil.showShort(baseContext, "下载失败")
    }


    private class DownloadListener(var context: Context?, url: String?) : DownloadProgressListener(url) {

        override fun onProgress(url: String?, currentBytes: Long, contentLength: Long, done: Boolean) {
            if (context != null) {
                val pi = PendingIntent.getActivity(context, 0, Intent(), 0)
                val builder = NotificationCompat.Builder(context)
                builder.setSmallIcon(R.mipmap.ic_launcher)
                builder.setLargeIcon(BitmapFactory.decodeResource(context?.resources, R.mipmap.ic_launcher))
                builder.setContentIntent(pi)
                val progress = (currentBytes * 100 / contentLength).toInt()
                if (progress >= 0) {
                    // 当progress大于或等于0时才需显示下载进度
                    builder.setContentText(progress.toString() + "%")
                    builder.setProgress(100, progress, false)
                }
                builder.setContentTitle(if (progress == 100) "下载完成" else "文件下载中")
                val notice = builder.build()
                val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                manager?.notify(1, notice)
            }
        }
    }

}