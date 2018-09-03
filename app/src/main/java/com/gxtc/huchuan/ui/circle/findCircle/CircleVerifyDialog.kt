package com.gxtc.huchuan.ui.circle.findCircle

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseDialogFragment
import com.gxtc.commlibrary.utils.GotoUtil
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.ui.mine.editinfo.vertifance.VertifanceActivity

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/3/28.
 */
class CircleVerifyDialog: BaseDialogFragment(), View.OnClickListener {

    private lateinit var btn: Button
    private lateinit var tvContent: TextView

    private var isRealAudit: String ?= null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        width = (WindowUtil.getScreenWidth(context) - resources.getDimension(R.dimen.margin_large) * 2).toInt()
        return super.onCreateDialog(savedInstanceState)
    }

    override fun initView(): View {
        val view = View.inflate(context, R.layout.dialog_circle_join, null)
        view.findViewById<ImageView>(R.id.img_close).setOnClickListener { dismiss() }
        tvContent = view.findViewById(R.id.tv_content)
        btn = view.findViewById(R.id.btn_ok)
        btn.setOnClickListener(this)

        val user = UserManager.getInstance().user
        isRealAudit = user?.isRealAudit
        //是否实名 0、否；1、是;2、实名审核中 3,审核不通过
        when(isRealAudit){
            "0"->{
                tvContent.text = resources.getString(R.string.circle_join_no_ver)
            }
            "1"->{
                btn.isEnabled = false
                tvContent.text = resources.getString(R.string.circle_join_ver)
            }
            "2"->{
                btn.isEnabled = false
                tvContent.text = resources.getString(R.string.circle_join_ver_ing)
            }
            "3"->{
                tvContent.text = resources.getString(R.string.circle_join_no_pass)
            }
        }
        return view
    }

    override fun onClick(v: View?) {
        when(isRealAudit){
            "0", "3"->{
                GotoUtil.goToActivity(activity, VertifanceActivity::class.java)
                dismiss()
            }
            "1", "2"->{
                dismiss()
            }
        }
    }
}