package com.gxtc.huchuan.adapter

import android.content.Context
import android.widget.ImageView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.FocusBean

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/7.
 */
class InviteChatSelectAdapter(context: Context?, list: MutableList<FocusBean>?, itemLayoutId: Int)
    : BaseRecyclerAdapter<FocusBean>(context, list, itemLayoutId) {

    override fun bindData(holder: ViewHolder?, position: Int, bean: FocusBean?) {
        val img = holder?.getView(R.id.img_head) as? ImageView
        ImageHelper.loadRound(context, img,bean?.userHeadPic, 4)
    }
}