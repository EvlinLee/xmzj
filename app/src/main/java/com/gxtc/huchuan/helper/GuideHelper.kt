package com.gxtc.huchuan.helper

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.gxtc.commlibrary.utils.EventBusUtil
import com.gxtc.commlibrary.utils.LogUtil
import com.gxtc.commlibrary.utils.SpUtil
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.event.EventGuideBean
import com.gxtc.huchuan.widget.guide.Component
import com.gxtc.huchuan.widget.guide.GuideBuilder
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/10/24.
 */
class GuideHelper(var activity: Activity) {

    companion object {
        @JvmField
        var TYPE_ONE = 1

        @JvmField
        var TYPE_TWO = 2

        @JvmField
        var TYPE_THREE = 3

        @JvmField
        var GUIDE_TWO = "guide_two"
    }

    var GUIDE_ONE = "guide_one"

    fun show(type: Int, vararg view: View) {
        when (type) {
            TYPE_ONE -> showOne(*view)

            TYPE_TWO -> showTwo(*view)

            TYPE_THREE -> showThree(*view)
        }

    }

    private fun showThree(vararg views: View) {
        val builder = GuideBuilder()
        builder.setTargetViews(* views)
                .setAlpha(150)
                .setHighTargetPadding(-30)
                .setHighTargetGraphStyle(Component.CIRCLE)
                .setOverlayTarget(false)
                .setOutsideTouchable(false)
                .addComponent(ComponentThree())
                .setOnVisibilityChangedListener(object : GuideBuilder.OnVisibilityChangedListener {
                    override fun onShown() {}

                    override fun onDismiss() {}
                })

        val guide = builder.createGuide()
        guide.setShouldCheckLocInWindow(false)
        guide.show(activity)
    }


    fun showTwo(vararg views: View) {
        val isShow = SpUtil.getBoolean(activity, GUIDE_TWO, false)
        val isShowOne = SpUtil.getBoolean(activity, GUIDE_ONE, false)
        if (isShow && isShowOne) {
            val builder = GuideBuilder()
            builder.setTargetView(views[0])
                    .setAlpha(150)
                    .setHighTargetCorner(20)
                    .setHighTargetGraphStyle(Component.CIRCLE)
                    .setOverlayTarget(false)
                    .setOutsideTouchable(false)
                    .addComponent(ComponentTwo())
                    .addComponent(ComponentTwoBtn())
                    .setOnVisibilityChangedListener(object : GuideBuilder.OnVisibilityChangedListener {
                        override fun onShown() {}

                        override fun onDismiss() {
                            EventBusUtil.post(EventGuideBean(3))
                        }
                    })

            val guide = builder.createGuide()
            guide.setShouldCheckLocInWindow(true)
            guide.show(activity)
        }


    }

    private fun showOne(vararg views: View) {
        Observable.timer(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    SpUtil.putBoolean(activity, GUIDE_ONE, true)
                    val builder = GuideBuilder()
                    builder.setTargetView(views[0])
                            .setAlpha(150)
                            .setHighTargetCorner(20)
                            .setOverlayTarget(false)
                            .setOutsideTouchable(false)
                            .setHighTargetPaddingTop(-4)
                            .setHighTargetPaddingBottom(-4)
                            .addComponent(ComponentOne())
                            .addComponent(ComponentOneBtn())
                            .setOnVisibilityChangedListener(object : GuideBuilder.OnVisibilityChangedListener {
                                override fun onShown() {}

                                override fun onDismiss() {
                                    EventBusUtil.post(EventGuideBean(3))
                                }
                            })

                    val guide = builder.createGuide()
                    guide.setShouldCheckLocInWindow(true)
                    guide.show(activity)
                }
    }


    private class ComponentOne : Component {

        override fun getView(inflater: LayoutInflater?): View =
                ImageView(inflater?.context)
                        .let {
                            it.setImageResource(R.drawable.guide_news_text)
                            it
                        }

        override fun getAnchor(): Int = Component.ANCHOR_BOTTOM;
        override fun getFitPosition(): Int = Component.FIT_END;
        override fun getXOffset(): Int = 0
        override fun getYOffset(): Int = 20
    }

    private class ComponentOneBtn : Component {

        var context: Context? = null
        var height = 0

        override fun getView(inflater: LayoutInflater?): View {
            context = inflater?.context
            val img = ImageView(context)
            val bitmap = BitmapFactory.decodeResource(context?.resources, R.drawable.guide_next_icon)
            img.setImageBitmap(bitmap)

            height = bitmap.height
            return img
        }

        override fun getAnchor(): Int = Component.ANCHOR_BOTTOM;
        override fun getFitPosition(): Int = Component.FIT_CENTER;
        override fun getXOffset(): Int = 10
        override fun getYOffset(): Int = WindowUtil.px2dip(context, (WindowUtil.getScreenH(context) / 2 - height).toFloat())
    }

    private class ComponentTwo : Component {

        override fun getView(inflater: LayoutInflater?): View =
                ImageView(inflater?.context)
                        .let {
                            it.setImageResource(R.drawable.guide_yxrj_text)
                            it
                        }

        override fun getAnchor(): Int = Component.ANCHOR_BOTTOM;
        override fun getFitPosition(): Int = Component.FIT_START;
        override fun getXOffset(): Int = 70
        override fun getYOffset(): Int = 20
    }

    private class ComponentTwoBtn : Component {

        var context: Context? = null
        var height = 0

        override fun getView(inflater: LayoutInflater?): View {
            context = inflater?.context
            val img = ImageView(context)
            val bitmap = BitmapFactory.decodeResource(context?.resources, R.drawable.guide_tg_icon)
            img.setImageBitmap(bitmap)

            height = bitmap.height
            return img
        }

        override fun getAnchor(): Int = Component.ANCHOR_BOTTOM;
        override fun getFitPosition(): Int = Component.FIT_CENTER;
        override fun getXOffset(): Int = 50
        override fun getYOffset(): Int = 150
    }


    private class ComponentThree : Component {
        var context: Context? = null

        override fun getView(inflater: LayoutInflater?): View {
            val view = inflater?.inflate(R.layout.guide_view, null, false)
            return view!!
        }

        override fun getAnchor(): Int = Component.ANCHOR_TOP;
        override fun getFitPosition(): Int = Component.FIT_CENTER;
        override fun getXOffset(): Int = 70
        override fun getYOffset(): Int = -20
    }

}