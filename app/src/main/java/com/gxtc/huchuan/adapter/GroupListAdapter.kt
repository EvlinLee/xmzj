package com.gxtc.huchuan.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.MessageBean

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/12.
 */
class GroupListAdapter(context: Context?, list: MutableList<MessageBean>?, itemLayoutId: Int)
    : BaseRecyclerAdapter<MessageBean>(context, list, itemLayoutId) {

    override fun bindData(holder: ViewHolder?, position: Int, bean: MessageBean?) {
        val tvName = holder?.getView(R.id.tv_item_focus_name)  as? TextView
        val img = holder?.getView(R.id.riv_item_focus)  as? ImageView

        tvName?.text = bean?.groupName
        ImageHelper.loadRound(context, img, bean?.groupPic, 4)
    }
}