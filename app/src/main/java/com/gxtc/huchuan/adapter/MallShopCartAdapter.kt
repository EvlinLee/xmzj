package com.gxtc.huchuan.adapter

import android.content.Context
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.guanaj.easyswipemenulibrary.EasySwipeMenuLayout
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.MallShopCartBean
import com.gxtc.huchuan.utils.StringUtil

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/10/23.
 */
class MallShopCartAdapter(context: Context?, list: MutableList<MallShopCartBean>?, itemLayoutId: Int)
    : BaseRecyclerAdapter<MallShopCartBean>(context, list, itemLayoutId) {

    var clickListener: View.OnClickListener ? = null

    override fun bindData(holder: ViewHolder?, position: Int, bean: MallShopCartBean?) {
        val img = holder?.getView(R.id.img_head) as ImageView
        val tvName = holder.getView(R.id.tv_title) as TextView
        val tvMoney = holder.getView(R.id.tv_money) as TextView
        val tvParam = holder.getView(R.id.tv_param) as TextView
        val tvCount = holder.getView(R.id.tv_count) as TextView
        val tvDelet = holder.getView(R.id.tv_delete) as TextView
        val checkbox = holder.getView(R.id.radio_btn) as CheckBox
        val swipeLayout = holder.getView(R.id.swipeLayout) as EasySwipeMenuLayout
        val layoutContent = holder.getView(R.id.content)

        ImageHelper.loadImage(context,img,bean?.picUrl)
        tvName.text = bean?.storeName
        tvMoney.text = "¥${StringUtil.formatMoney(2,bean?.storePricePrice)}"
        tvCount.text = "x${bean?.amount}"
        tvParam.text = bean?.storePriceName

        layoutContent.tag = bean
        layoutContent.setOnClickListener(clickListener)

        tvDelet.tag = bean
        tvDelet.setOnClickListener{ v->
            swipeLayout.resetStatus()
            clickListener?.onClick(v)
        }

        checkbox.tag = bean
        checkbox.isChecked = bean?.isSelect!!
        checkbox.setOnClickListener{ v ->
            val tag = v.tag as? MallShopCartBean
            val box = v as? CheckBox

            if(tag?.isSelect!!){
                tag.isSelect = false
                box?.isChecked = false
            }else{
                tag.isSelect = true
                box?.isChecked = true
            }
            clickListener?.onClick(v)
        }
    }


}