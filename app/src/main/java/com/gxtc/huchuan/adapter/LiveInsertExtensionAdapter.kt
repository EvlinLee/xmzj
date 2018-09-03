package com.gxtc.huchuan.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.huchuan.R

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/4/20.
 */
class LiveInsertExtensionAdapter(context: Context?, list: MutableList<String>, itemLayoutId: Int, var ids: IntArray)
    : BaseRecyclerAdapter<String>(context, list, itemLayoutId) {


    override fun bindData(holder: ViewHolder?, position: Int, t: String?) {
        holder?.let {
            val img = it.getView(R.id.img) as ImageView
            val tvTitle = it.getView(R.id.tv_title) as TextView

            img.setImageResource(ids[position])
            tvTitle.text = t
        }
    }
}