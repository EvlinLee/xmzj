package com.gxtc.huchuan.adapter

import android.content.Context
import android.widget.TextView
import com.gxtc.commlibrary.base.AbsBaseAdapter
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.CircleBean

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/8/28.
 */
class PopCircleListTypeAdapter(context: Context?, datas: MutableList<CircleBean>?, itemLayoutId: Int)
    : AbsBaseAdapter<CircleBean>(context, datas, itemLayoutId) {
    private var mPosition :Int = -1

    override fun bindData(holder: AbsBaseAdapter<out Any>.ViewHolder?, t: CircleBean?, position: Int) {
        val tv = holder?.getView(R.id.tv_name) as? TextView
//        tv?.isSelected = t?.isSelect!!
        tv?.text = t?.typeName
        if(mPosition == position){
            tv?.setTextColor(context.getResources().getColor(R.color.pts_color))
        }else{
            tv?.setTextColor(context.getResources().getColor(R.color.text_color_999))
        }

    }

    fun setPostion(position:Int) {
        this.mPosition = position
    }
}