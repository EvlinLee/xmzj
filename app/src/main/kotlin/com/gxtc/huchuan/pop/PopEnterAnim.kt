package com.gxtc.huchuan.pop

import android.animation.ObjectAnimator
import android.view.View
import com.flyco.animation.BaseAnimatorSet

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/7/27.
 * popwindow 进入动画
 */
class PopEnterAnim : BaseAnimatorSet() {

    override fun setAnimation(view: View?) {
        animatorSet?.playTogether(ObjectAnimator.ofFloat(view,"alpha", * floatArrayOf(0f, 1f)))
    }

}