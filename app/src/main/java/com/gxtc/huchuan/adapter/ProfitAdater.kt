package com.gxtc.huchuan.adapter

import android.app.Activity
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.NewDistributeBean
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity
import com.gxtc.huchuan.utils.DateUtil
import java.lang.Double

/**
 * Created by zzg on 2017/12/22.
 */
class ProfitAdater(activity: Activity,data:ArrayList<NewDistributeBean>,res:Int) :BaseRecyclerAdapter<NewDistributeBean>(activity,data,res) {

    override fun bindData(holder: ViewHolder?, position: Int, bean: NewDistributeBean?) {
        holder?.setText(R.id.text_buyer,"购买人:"+bean?.name)
        holder?.setText(R.id.text_time, DateUtil.stampToDate(bean?.createTime.toString()))
        holder?.setText(R.id.text_mouney,"￥"+bean?.sumFee)
        ImageHelper.loadImage(context,holder?.getViewV2(R.id.headPic),bean?.facePic)
        holder?.setText(R.id.text_price,"价格:￥"+bean?.fee)
        holder?.getViewV2<TextView>(R.id.text_mouney_label)?.text = "收益"
        holder?.getViewV2<TextView>(R.id.text_pent)?.visibility = View.INVISIBLE
    }
}