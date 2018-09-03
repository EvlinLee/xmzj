package com.gxtc.huchuan.utils

import android.support.v7.widget.RecyclerView
import android.view.View
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.utils.LogUtil

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/1/6.
 * 动态列表滚动工具类
 */
object ListScrollUtil {

    private var pressViewY = 0
    private var pressViewHeight = 0
    private var clear = false

    //按下view记录Y轴坐标
    fun onPressView(view: View?){
        view?.let {
            clear = false
            val size = IntArray(2)
            it.getLocationOnScreen(size)
            this.pressViewY = size[1]
            this.pressViewHeight = it.getHeight()
        }
    }


    /**
     *  根据键盘的弹出， 计算并滚动listview的位置
     *  @anchorY 锚点位置的Y坐标，这里选择 软键盘上边的贴着的view top点
     *  @see getLocationOnScreen
     */
    fun scrollList(recyclerView: RecyclerView, anchorY: Int){
        if(!clear){
            val pressBottom = pressViewY + pressViewHeight

            //被软键盘遮挡的情况
            if(pressBottom > anchorY){
                val offsetY = pressBottom - anchorY
                recyclerView.smoothScrollBy(0, offsetY)

            //在软键盘上方的情况
            }else{
                val offsetY = anchorY - pressBottom
                recyclerView.smoothScrollBy(0, -offsetY)
            }
            clear = true
        }
    }
}