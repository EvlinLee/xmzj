package com.gxtc.huchuan.dialog

import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseDialogFragment
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.im.adapter.MyConversationListAdapter
import io.rong.imlib.model.UserInfo

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/11/7.
 * 确认转发消息dialog
 */
class ConfirmRelayDialog: BaseDialogFragment(), View.OnClickListener {




    var imgHead: ImageView ? = null
    var tvName: TextView ? = null
    var tvCancel: TextView ? = null
    var tvSubTitle: TextView ? = null
    var editContent: EditText ? = null
    var tvOk: TextView ? = null

    var isSelectFriends = 0

    var userInfo: UserInfo ? = null
    var onClickListener: View.OnClickListener ? = null

    override fun initView(): View {
        val view = View.inflate(context, R.layout.dialog_confirm_relay,null)
        imgHead = view.findViewById(R.id.img_head) as ImageView
        tvName = view.findViewById(R.id.tv_name) as TextView
        editContent = view.findViewById(R.id.edit_content) as EditText
        tvCancel = view.findViewById(R.id.tv_cancel) as TextView
        tvSubTitle = view.findViewById(R.id.tv_sub_title) as TextView
        tvOk = view.findViewById(R.id.tv_ok) as TextView

        if (Constant.SELECT_TYPE_CARD == isSelectFriends) {
            tvSubTitle?.text = "发送名片:"
        }
        //申请担保交易那里需要把输入框隐藏掉
        if (Constant.SELECT_TYPE_GUARAN_DEAL == isSelectFriends) {
            editContent?.visibility = View.GONE
        }else{
            editContent?.visibility = View.VISIBLE
        }

        return view
    }

    override fun initListener() {
        tvCancel?.setOnClickListener(this)
        tvOk?.setOnClickListener(this)
    }

    override fun initData(dialogView: View?) {
        ImageHelper.loadRound(context, imgHead, userInfo?.portraitUri.toString(), 4)
        tvName?.text = userInfo?.name
    }

    override fun onClick(v: View?) {
        MyConversationListAdapter.mShareFlag = -1 //这里一定要置为-1，否则转发之后 mShareFlag 不会变回初始状态，点击聊天列表上的搜索框到搜索界面当搜到目标用户，则无法进入会话界面，而是会弹起这个弹窗
        when(v?.id){
            R.id.tv_cancel-> dismiss()

            R.id.tv_ok -> {
                onClickListener?.onClick(v)
            }
        }

    }

}
