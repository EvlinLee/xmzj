package com.gxtc.huchuan.ui.im.video

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import com.gxtc.commlibrary.helper.PermissionsHelper
import com.gxtc.commlibrary.utils.EventBusUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.event.EventClickBean
import com.gxtc.huchuan.im.ui.ConversationActivity
import com.gxtc.huchuan.utils.DialogUtil
import com.gxtc.huchuan.utils.JumpPermissionManagement
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.model.FunctionConfig
import com.luck.picture.lib.model.FunctionOptions
import com.luck.picture.lib.model.PictureConfig
import io.rong.imkit.RongExtension
import io.rong.imkit.plugin.IPluginModule

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/12/13.
 */
class VideoPlugin: IPluginModule, PictureConfig.OnSelectResultCallback {

    private var mActivity: Activity ? = null
    private var mAlertDialog: AlertDialog ? = null

    override fun onClick(fragment: Fragment?, p1: RongExtension?) {
        if(fragment != null){
            mActivity = fragment.activity

            val pers = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            PermissionsHelper.getInstance(mActivity).performRequestPermissions(fragment.getString(R.string.txt_card_permission), pers, 10010,
                    object : PermissionsHelper.PermissionsResultListener{
                        override fun onPermissionGranted() {
                            val options = FunctionOptions.Builder()
                                    .setType(FunctionConfig.TYPE_VIDEO)
                                    .setRecordVideoDefinition(1)
                                    .setSelectMode(FunctionConfig.MODE_SINGLE)
                                    .setShowCamera(true)
                                    .setImageSpanCount(3)
                                    .setPreviewVideo(true)
                                    .create()
                            PictureConfig.getInstance().init(options).openPhoto(mActivity, this@VideoPlugin)
                        }

                        override fun onPermissionDenied() {
                            mAlertDialog = DialogUtil.showDeportDialog(fragment.activity, false, null, fragment.getString(R.string.pre_storage_notice_msg)) { v ->
                                if (v.id == R.id.tv_dialog_confirm) {
                                    JumpPermissionManagement.GoToSetting(fragment.activity)
                                }
                                mAlertDialog?.dismiss()
                            }
                        }
                    })


        }
    }

    override fun obtainDrawable(p0: Context?): Drawable = p0!!.resources.getDrawable(R.drawable.plugin_video_selector)

    override fun obtainTitle(p0: Context?): String = "小视频"

    override fun onActivityResult(p0: Int, p1: Int, p2: Intent?) = Unit

    override fun onSelectSuccess(resultList: MutableList<LocalMedia>?) = Unit

    override fun onSelectSuccess(media: LocalMedia?) {
        media?.let {
            EventBusUtil.post(EventClickBean(ConversationActivity.REQUEST_VIDEO.toString(), media))
        }
    }
}