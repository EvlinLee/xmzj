package com.gxtc.huchuan.utils

import android.app.Activity
import android.support.v7.app.AlertDialog
import android.view.View
import com.gxtc.huchuan.R

/**
 * Created by zzg on 2018/5/14
 */
class DynamicUtil {
    companion object {
        var mAlertDialog: AlertDialog? = null
        @JvmStatic
        fun showDoalogConfiromRemove(activity:Activity,listener:View.OnClickListener){
            mAlertDialog = DialogUtil.showDeportDialog(activity, false, null, "确定删除动态?",
                    View.OnClickListener { v ->
                        if (v.id == R.id.tv_dialog_confirm) {
                            listener?.let {
                                it.onClick(v)
                            }
                        }
                        mAlertDialog?.dismiss()
                    })
        }
    }
}