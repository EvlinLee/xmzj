package com.gxtc.huchuan.pop

import android.content.Context
import android.view.WindowManager
import com.flyco.dialog.widget.popup.base.BaseBubblePopup

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/12/8.
 * 统一的基础弹窗， 只是改变背景层透明度
 */
abstract class UnifyPop<T : BaseBubblePopup<T>>(context: Context?): BaseBubblePopup<T>(context) {

    private var dimAmount = 0f


    fun setBgAlpha(float: Float): T{
        dimAmount = float
        return this as T
    }

    override fun show() {
        super.show()
        val param = window.attributes as WindowManager.LayoutParams
        param.dimAmount = dimAmount
        dimEnabled(true)
    }

}

