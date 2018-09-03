package com.gxtc.huchuan.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.base.BaseRecyclerTypeAdapter
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.RecentBean

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/3/17.
 */
class RecentDynamicAdapter(context: Context?, list: MutableList<RecentBean>?, itemLayoutId: Int)
    : BaseRecyclerAdapter<RecentBean>(context, list, itemLayoutId) {

    override fun bindData(holder: ViewHolder?, position: Int, bean: RecentBean?) {
        val tvCount = holder?.getView(R.id.tv_name) as? TextView
        tvCount?.text = "新增动态: ${bean?.count}"

        val tvDate = holder?.getView(R.id.tv_date) as? TextView
        tvDate?.text = bean?.dateName
    }

}
