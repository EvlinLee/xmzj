package com.gxtc.huchuan.dialog

import android.Manifest
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseDialogFragment
import com.gxtc.commlibrary.helper.PermissionsHelper
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.UpdataBean
import com.gxtc.huchuan.service.DownloadService
import com.gxtc.huchuan.utils.DialogUtil
import com.gxtc.huchuan.utils.JumpPermissionManagement
import com.luck.picture.lib.model.FunctionConfig
import com.luck.picture.lib.model.FunctionOptions
import com.luck.picture.lib.model.PictureConfig

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/3/1.
 * 版本更新弹窗
 */
class UpdataDialog: BaseDialogFragment() {

    private var tvSubTitle: TextView ?= null
    private var tvContent: TextView ?= null
    private var tvCancel: ImageView ?= null
    private var tvDownload: TextView ?= null

    private var isForce = false
    private var updataInfo: UpdataBean ?= null

    var onClickListener: View.OnClickListener ?= null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        width = (WindowUtil.getScreenWidth(context) - resources.getDimension(R.dimen.margin_large) * 2).toInt();
        return super.onCreateDialog(savedInstanceState)
    }

    override fun initView(): View {
        val view = View.inflate(context, R.layout.dialog_updata, null)
        tvSubTitle = view.findViewById(R.id.tv_update_title)
        tvContent = view.findViewById(R.id.tv_update_content)
        tvCancel = view.findViewById(R.id.tv_cancel)
        tvDownload = view.findViewById(R.id.tv_update)

        tvCancel?.setOnClickListener{ v ->
            dismiss()
            onClickListener?.onClick(v)
        }

        tvDownload?.setOnClickListener{ v ->
            downloadNow()
            dismiss()
            onClickListener?.onClick(v)
        }


        if(updataInfo != null){
            tvContent?.text = updataInfo?.updateContent
            canOutside = updataInfo?.isForce == 0
        }else{
            tvSubTitle?.visibility = View.GONE
            tvDownload?.visibility = View.GONE
            tvContent?.text = "当前已是最新版本"
        }

        return view
    }

    private fun downloadNow() {
        if(updataInfo != null && !updataInfo?.updateUrl.isNullOrEmpty()){
            DownloadService.startDownload(context!!, updataInfo?.updateUrl, DownloadService.TYPE_APK)

        }
    }


    fun setUpdataInfo(info: UpdataBean){
        this.updataInfo = info
    }

}