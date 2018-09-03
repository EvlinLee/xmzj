package com.gxtc.huchuan.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.data.SpecialBean
import com.gxtc.huchuan.utils.DateUtil
import com.gxtc.huchuan.utils.StringUtil

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/5/12.
 */
class MySpecialListAdapter(context: Context?, list: MutableList<SpecialBean>?, itemLayoutId: Int) : BaseRecyclerAdapter<SpecialBean>(context, list, itemLayoutId) {

    constructor(context: Context?, list: MutableList<SpecialBean>?)
            : this(context, list, R.layout.item_my_special_list) {
    }

    override fun bindData(holder: ViewHolder?, position: Int, bean: SpecialBean?) {
        val tvTitle = holder?.getView(R.id.tv_title) as TextView
        val tvPrice = holder.getView(R.id.tv_prices) as TextView
        val tvCount = holder.getView(R.id.tv_count) as TextView
        val tvTime = holder.getView(R.id.tv_time) as TextView
        val imgHead = holder.getView(R.id.img_face) as ImageView

        bean?.run {
            if(0== isFee){
                tvPrice.setTextColor(getContext().resources.getColor(R.color.text_color_999))
                tvPrice.text = "免费"
            } else {
                tvPrice.setTextColor(getContext().resources.getColor(R.color.color_fb4717))
                tvPrice.text = "￥" +  StringUtil.formatMoney(2, price.toString())
            }
            tvTitle.text = name
            tvCount.text = "${subscribeSum}人订阅"
            tvTime.text = DateUtil.showTimeAgo(updateTime?.toString())

            ImageHelper.loadImage(context, imgHead, pic)
        }
    }
}