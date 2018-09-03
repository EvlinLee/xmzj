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
import com.gxtc.huchuan.utils.DateUtil
import org.jsoup.helper.DataUtil

/**
 * Created by zzg on 2017/12/20.
 */
class CircleActiveAdater(context: Context, data:ArrayList<CircleBean>, res: Int) : BaseRecyclerAdapter<CircleBean>(context,data,res) {

    override fun bindData(holder: ViewHolder?, position: Int, bean: CircleBean?) {
        holder?.setText(R.id.user_name,bean?.userName)?.setText(R.id.conten,bean?.content)
        holder?.setText(R.id.dynamic_count,bean?.infoNum?.toString())?.setText(R.id.comment_count,bean?.commNum?.toString())
        ImageHelper.loadImage(context,holder?.getViewV2(R.id.head_pic),bean?.userPic)
    }
}