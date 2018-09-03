package com.gxtc.huchuan.ui.deal.guarantee

import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseDialogFragment
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.R

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/3/22.
 */
class ConfirmGuaranteeDialog: BaseDialogFragment() {

    var clickListener: View.OnClickListener ? = null
    var content = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        context?.let {
            width = (WindowUtil.getScreenWidth(it) - it.resources.getDimension(R.dimen.margin_larger) * 2).toInt()
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun initView(): View {
        val view = View.inflate(context, R.layout.dialog_confirm_guarantee, null)
        val btn = view.findViewById<View>(R.id.btn_confirm)
        val tvContent = view.findViewById<TextView>(R.id.tv_name)
        btn.setOnClickListener { v ->
            clickListener?.onClick(v)
        }
        tvContent.text = Html.fromHtml(content)
        //tvContent.text = content
        view.findViewById<View>(R.id.tv_cancel).setOnClickListener { dismiss() }
        return view
    }

}