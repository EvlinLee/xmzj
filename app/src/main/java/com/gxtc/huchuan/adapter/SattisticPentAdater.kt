package com.gxtc.huchuan.adapter

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.CircleBean
import com.gxtc.huchuan.bean.CircleSignBean
import com.gxtc.huchuan.bean.SeriseCountBean
import com.gxtc.huchuan.bean.StatisticBean
import com.gxtc.huchuan.utils.DateUtil
import org.jsoup.helper.DataUtil

/**
 * Created by zzg on 2017/12/20.
 */
class SattisticPentAdater(context: Context, data:MutableList<StatisticBean>, res: Int) : BaseRecyclerAdapter<StatisticBean>(context,data,res) {

    override fun bindData(holder: ViewHolder?, position: Int, bean: StatisticBean?) {
        holder?.setText(R.id.pent,"佣金:"+bean?.saleFee)
        holder?.setText(R.id.time,bean?.dateName)
    }
}