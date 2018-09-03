package com.gxtc.huchuan.utils

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.MyApplication

/**
 * Created by zzg on 2018/5/7
 * 因为要复制所有订单号，所以抽出统一处理
 */
class ClipboardUtil {
    companion object {
        @JvmStatic
        fun copyText(content:String?){
            val cmb = MyApplication.getInstance().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cmb.text = content
            ToastUtil.showShort(MyApplication.getInstance(), "订单号已复制")
        }
    }
}