package com.gxtc.huchuan.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.MallBean
import com.gxtc.huchuan.bean.OrderDetailBean
import com.gxtc.huchuan.utils.StringUtil

/**
 * Created by zzg on 2017/10/25.
 */
class OrderDetailAdater(context: Context, datas:ArrayList<OrderDetailBean.OrderMallTypeBean>, res:Int): BaseRecyclerAdapter<OrderDetailBean.OrderMallTypeBean>(context,datas,res) {

    override fun bindData(holder: ViewHolder?, position: Int, bean: OrderDetailBean.OrderMallTypeBean?) {
        val image = holder!!.getView(R.id.item_live_room_image) as ImageView
        ImageHelper.loadImage(context,image,bean!!.facePic)
        val label = holder!!.getView(R.id.item_title) as TextView
        label.setText(bean!!.storeName )
        val type = holder!!.getView(R.id.item_type) as TextView
        type.setText(bean!!.priceName )
        val price = holder!!.getView(R.id.item_price) as TextView
        price.setText("￥:"+StringUtil.formatMoney(2,bean!!.price!!))
        val count= holder!!.getView(R.id.item_count) as TextView
        count.setText("x"+bean.amount)
    }
}