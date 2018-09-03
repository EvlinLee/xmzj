package com.gxtc.huchuan.dialog;

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseDialogFragment
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.huchuan.R

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/12/25.
 */
@Deprecated(message = "已经弃用，将此ui转到picture_library中去了")
class GraffitiTextDialog: BaseDialogFragment(), View.OnClickListener {

    private lateinit var tvCancel: TextView
    private lateinit var tvFinish: TextView

    private var edit: EditText ? = null
    private var textListener: OnTextListener ? = null

    var color = 0
    var text = ""

    override fun initView(): View {
        val view = View.inflate(context, R.layout.dialog_graffiti_text, null)
        tvCancel = view.findViewById(R.id.btn_cancel) as TextView
        tvFinish = view.findViewById(R.id.btn_finish) as TextView
        edit = view.findViewById(R.id.edit_content) as EditText

        edit?.setTextColor(color)
        edit?.setText(text)
        edit?.setSelection(text.length)

        tvCancel.setOnClickListener(this)
        tvFinish.setOnClickListener(this)
        return view
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.MATCH_PARENT
        return super.onCreateDialog(savedInstanceState)
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_cancel -> {
                dismiss()
            }

            R.id.btn_finish -> {
                if(!TextUtils.isEmpty(edit?.text.toString())){
                    textListener?.textFinish(edit?.text.toString())
                    dismiss()
                }else{
                    ToastUtil.showShort(context, "请输入内容")
                }
            }
        }
    }


    override fun show(manager: FragmentManager?, tag: String?) {
        super.show(manager, tag)
        edit?.setText(text)
    }

    fun setOnTextListener(onTextListener: OnTextListener){
        textListener = onTextListener
    }

    interface OnTextListener{
        fun textFinish(text: String)
    }

}