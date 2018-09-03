package com.gxtc.huchuan.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.AllPurchaseListBean
import com.gxtc.huchuan.bean.OrderBean
import com.gxtc.huchuan.utils.StringUtil

/**
 * Created by zzg on 2017/11/3.
 */
class OrderMallItemAdater(context:Context, datas: List<AllPurchaseListBean.Commodity>?, res:Int) : BaseRecyclerAdapter<AllPurchaseListBean.Commodity>(context,datas,res) {

    override fun bindData(holder: ViewHolder?, position: Int, bean: AllPurchaseListBean.Commodity?) {
         var image =  holder!!.getView(R.id.img_head) as ImageView
         ImageHelper.loadRound(context,image,bean?.pic, 5)
         var name =  holder!!.getView(R.id.tv_goods_name) as TextView
         name.text = bean?.title
         var price = holder!!.getView(R.id.price) as TextView
         price.text =  StringUtil.formatMoney(2,bean?.price!!)
         var type = holder!!.getView(R.id.tv_type) as TextView
         type.text =  bean?.formatName
         var count = holder!!.getView(R.id.count) as TextView
         count.text = "x"+bean.sum
    }
}