package com.gxtc.huchuan.adapter

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.*
import com.gxtc.huchuan.utils.DateUtil
import org.jsoup.helper.DataUtil

/**
 * Created by zzg on 2017/12/20.
 */
class SattisticVisitorDetailAdater(context: Context, data:MutableList<VisitorBean>, res: Int) : BaseRecyclerAdapter<VisitorBean>(context,data,res) {

    override fun bindData(holder: ViewHolder?, position: Int, bean: VisitorBean?) {
        holder?.setText(R.id.name,bean?.name)?.setText(R.id.time,DateUtil.stampToDate(bean?.createtime))
        ImageHelper.loadRound(context,holder?.getImageView(R.id.head_pic),bean?.headPic,4)
    }
}