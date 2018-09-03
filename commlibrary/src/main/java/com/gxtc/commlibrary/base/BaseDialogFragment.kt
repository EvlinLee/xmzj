package com.gxtc.commlibrary.base

import android.app.Dialog
import android.app.FragmentTransaction
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.*
import butterknife.ButterKnife
import com.gxtc.commlibrary.R
import com.gxtc.commlibrary.utils.ClickUtil

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/8/4.
 * 如果对dialog有其他的设置 需要覆盖 onCreateDialog() 在super之前设置好
 * 点击显示dialog 的时候 要加个点击间隔判断 不然快速点击2次 会加载崩溃
 */
abstract class BaseDialogFragment constructor() : DialogFragment(){

    var dialog : KeyboardDialog ? = null
    var dialogView : View ? = null
    var gravity : Int ? = Gravity.CENTER
    var width : Int = WindowManager.LayoutParams.MATCH_PARENT
    var height : Int = WindowManager.LayoutParams.WRAP_CONTENT
    var animStyle : Int ? = R.style.mypopwindow_anim_style
    var canOutside : Boolean = true
    var isTransparentBg = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = initView()

        if(dialogView == null)  throw IllegalAccessError("dialogView is not null!!")
        ButterKnife.bind(this,dialogView!!)

        if(dialog == null)
            dialog = KeyboardDialog(activity!!,R.style.dialog_Translucent)
        dialog?.setCanceledOnTouchOutside(canOutside)
        dialog?.setContentView(dialogView)

        val param = dialog?.window?.attributes
        param?.let {
            it.gravity = gravity!!
            it.width = width
            it.height = height
            it.windowAnimations = animStyle!!
            if(isTransparentBg){
                it.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
                it.dimAmount = 0f
            }
        }
        initListener()
        initData(dialogView)

        return dialog!!
    }

    abstract fun initView() : View

    open fun initData(dialogView : View ?) = Unit

    open fun initListener() = Unit


    override fun show(manager: FragmentManager?, tag: String?) {
        if(!ClickUtil.isFastClick() && manager?.findFragmentByTag(tag) == null){
            manager?.beginTransaction()!!.add(this,tag!!).commitAllowingStateLoss()//不要使用父类的commit方法，容易在Activity因异常销毁而报错
        }
    }

}