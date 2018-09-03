package com.gxtc.huchuan.ui.im.call

import android.Manifest
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.support.v4.app.Fragment
import android.widget.Toast
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.data.UserManager
//import com.gxtc.huchuan.ui.im.video.VideoPlugin
import com.gxtc.huchuan.widget.MyActionSheetDialog
import io.rong.callkit.RongCallKit
import io.rong.callkit.VideoPlugin
import io.rong.calllib.RongCallClient
import io.rong.calllib.RongCallCommon
import io.rong.imkit.RongExtension
import io.rong.imkit.utilities.PermissionCheckUtil
import io.rong.imlib.model.Conversation
import java.util.ArrayList

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/1/11.
 * 通话
 */
class CallPlugin : VideoPlugin() {

    private var allMembers: ArrayList<String>? = null
    private var context: Context? = null

    private var conversationType: Conversation.ConversationType? = null
    private var targetId: String? = null

    override fun obtainTitle(context: Context?): String = "通话"

    override fun obtainDrawable(context: Context?): Drawable = context!!.resources.getDrawable(R.drawable.plugin_talk_selector)


    override fun onClick(currentFragment: Fragment?, extension: RongExtension?) {
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        if (!PermissionCheckUtil.requestPermissions(currentFragment, permissions)) {
            return
        }

        context = currentFragment?.context
        conversationType = extension?.conversationType
        targetId = extension?.targetId

        if(!targetId.isNullOrEmpty() && targetId.equals(UserManager.getInstance().userCode)){
            ToastUtil.showShort(context, "不能与自己发起语音通话")
            return
        }

        val profile = RongCallClient.getInstance().callSession
        if (profile != null && profile.activeTime > 0) {
            Toast.makeText(context,
                    if (profile.mediaType == RongCallCommon.CallMediaType.AUDIO)
                        context?.getString(io.rong.callkit.R.string.rc_voip_call_audio_start_fail)
                    else
                        context?.getString(io.rong.callkit.R.string.rc_voip_call_video_start_fail),
                    Toast.LENGTH_SHORT)
                    .show()
            return
        }

        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected || !networkInfo.isAvailable) {
            Toast.makeText(context, context?.getString(io.rong.callkit.R.string.rc_voip_call_network_error), Toast.LENGTH_SHORT).show()
            return
        }

        showCallDialog()
    }


    private fun showCallDialog(){
        val items = arrayOf("视频聊天", "语音聊天")
        val dialog = MyActionSheetDialog(context, items, null)
        dialog.isTitleShow(false)
                .titleTextSize_SP(14.5f)
                .widthScale(1f)
                .cancelMarginBottom(0)
                .cornerRadius(0f)
                .dividerHeight(1f)
                .itemTextColor(context?.resources?.getColor(R.color.black)!!)
                .cancelText("取消")
                .show()

        dialog.setOnOperItemClickL { parent, view, position, id ->
            when (position){
                0 -> startVideoChat()

                1 -> startVoiceChat()
            }
            dialog.dismiss()
        }
    }


    private fun startVideoChat() =
            RongCallKit.startSingleCall(context, targetId, RongCallKit.CallMediaType.CALL_MEDIA_TYPE_VIDEO)


    private fun startVoiceChat()=
            RongCallKit.startSingleCall(context, targetId, RongCallKit.CallMediaType.CALL_MEDIA_TYPE_AUDIO)

}