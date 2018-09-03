package com.gxtc.huchuan.adapter

import android.content.Context
import android.graphics.Paint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.CategoryBean

/**
 * Created by zzg on 2017/10/21.
 *
 */
class MallTabAdater(context :Context, list: ArrayList<CategoryBean>, res:Int) : BaseRecyclerAdapter<CategoryBean>(context,list,res) {
    var mAddGoodsListner : View.OnClickListener? = null

    fun setAddGoodsListner(addGoodsListner : View.OnClickListener){
        mAddGoodsListner = addGoodsListner
    }

    override fun bindData(holder: ViewHolder?, position: Int, bean: CategoryBean?) {
        var title = holder!!.getView(R.id.item_goods_title) as TextView
        title.text = bean!!.name
        var sourPrice = holder!!.getView(R.id.source_price) as TextView
        sourPrice.visibility = View.GONE
        sourPrice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
        var price = holder!!.getView(R.id.price) as TextView
        price.text = "￥ "+bean!!.price
        var image = holder!!.getView(R.id.item_live_room_image) as ImageView
        ImageHelper.loadImage(context,image,bean.facePic)
        holder!!.getView(R.id.text_add).setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                v?.tag = position
                mAddGoodsListner?.onClick(v)
            }

        })
    }
}