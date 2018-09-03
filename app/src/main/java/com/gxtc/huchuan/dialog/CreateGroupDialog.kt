package com.gxtc.huchuan.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.KeyboardDialog
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.R

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/13.
 */
class CreateGroupDialog : DialogFragment(), View.OnClickListener {

    private var head_pic: ImageView? = null
    private var btn_next: TextView? = null
    private var edit_nicheng: EditText? = null

    private var clickListener: View.OnClickListener? = null

    var picUrl: String? = null
        set(value) {
            ImageHelper.loadRound(activity, head_pic, value, 4)
            field = value
        }

    var name: String? = null
        get() = edit_nicheng?.text.toString()
        set(value) {
            edit_nicheng?.setText(value)
            field = value
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val rootView = LayoutInflater.from(context).inflate(R.layout.dialog_create_group, null)
        val dialog = KeyboardDialog(context!!, R.style.dialog_Translucent)
        dialog.setContentView(rootView)

        val params = dialog.window?.attributes
        params?.width = (WindowUtil.getScreenW(context) * 0.8).toInt()

        head_pic = rootView.findViewById(R.id.head_pic) as? ImageView
        btn_next = rootView.findViewById(R.id.btn_next) as? TextView
        edit_nicheng = rootView.findViewById(R.id.edit_nicheng) as? EditText

        head_pic?.setOnClickListener(this)
        btn_next?.setOnClickListener(this)

        return dialog
    }

    override fun onClick(v: View?) {
        clickListener?.onClick(v)
    }


    fun setOnClickListener(listene: View.OnClickListener) {
        clickListener = listene
    }


}