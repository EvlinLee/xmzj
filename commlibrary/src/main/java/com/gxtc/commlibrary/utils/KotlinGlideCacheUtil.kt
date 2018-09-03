package com.gxtc.commlibrary.utils

import android.content.Context
import android.os.Looper
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import rx.Observable
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.math.BigDecimal

/**
 * Created by zzg on 2017/8/12.
 */
class KotlinGlideCacheUtil constructor() {

    companion object {
        @Volatile
        var inst: KotlinGlideCacheUtil? = null

        fun getInstance(): KotlinGlideCacheUtil {
            if (inst == null)
                synchronized(KotlinGlideCacheUtil::class) {
                    if (inst == null) {
                        inst = KotlinGlideCacheUtil()
                    }
                }
            return inst!!
        }
    }

    /**
     * 清除图片磁盘缓存 只能在子线程里清除
     */
    fun clearImageDiskCache(context: Context) {
        try{
            if(Looper.getMainLooper() == Looper.myLooper()){
                Observable.create<Observable.OnSubscribe<Any>> { subscriber ->
                    Glide.get(context).clearDiskCache()
                    subscriber.onCompleted()
                }.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(object : Observer<Any>{
                            override fun onNext(t: Any?) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onCompleted() {
                                ToastUtil.showShort(context, "清除成功")
                            }

                            override fun onError(e: Throwable?) {
                                ToastUtil.showShort(context, e?.message)
                            }

                        })
            }else{
                Glide.get(context).clearDiskCache()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    /**
     * 清除图片内存缓存
     */
    fun clearImageMemoryCache(context: Context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(context).clearMemory()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun clearImageAllCache(context: Context) {
        clearImageDiskCache(context)
        clearImageMemoryCache(context)
        val ImageExternalCatchDir = context.externalCacheDir!!.toString() + ExternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR
        deleteFolderFile(ImageExternalCatchDir, true)
    }

    fun getCacheSize(context: Context): String {
        try {
            return getFormatSize(getFolderSize(File(context.cacheDir.toString() + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR)).toDouble())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            for (aFileList in fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList)
                } else {
                    size = size + aFileList.length()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return size
    }

    fun deleteFolderFile(filePath:String ,deleteThisPath:Boolean){
        if(!TextUtils.isEmpty(filePath)){
            try {
                var file=File(filePath)
                if(file.isDirectory()){
                    var files= file.listFiles()
                    for(file1 in files){
                        deleteFolderFile(file1.absolutePath,true)
                    }
                }
                if(deleteThisPath){
                    if(!file.isDirectory){
                        file.delete()
                    }else{
                        if (file.listFiles().size == 0){
                            file.delete()
                        }
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun  getFormatSize(size:Double) : String{
       var kiloByte  =size / 1024
        if (kiloByte < 1) {
            return size.toString() + "Byte"
        }
        val megaByte = kiloByte / 1024
        if(megaByte < 1){
            var result1 = BigDecimal(java.lang.Double.toString(megaByte))
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(java.lang.Double.toString(megaByte))
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB"
        }

        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(java.lang.Double.toString(gigaByte))
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)

        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
    }
}