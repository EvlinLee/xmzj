package com.gxtc.huchuan.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.CircleBean
import com.gxtc.huchuan.widget.CircleImageView

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/8/17.
 */
class ChatGroupAdapter(context: Context, datas: MutableList<CircleBean>,res: Int)
    : BaseRecyclerAdapter<CircleBean>(context,datas,res) {

    override fun bindData(holder: ViewHolder?, position: Int, bean : CircleBean?) {
        val tv = holder?.getView(R.id.tv_user_name) as? TextView
        val img = holder?.getView(R.id.iv_head) as? CircleImageView

        img?.visibility = View.GONE
        tv?.text = bean?.groupChatName
    }
}