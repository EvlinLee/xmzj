package com.gxtc.huchuan.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.R

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/3/22.
 */
class GuaranteeImageAdapter(context: Context?, list: MutableList<String>?, itemLayoutId: Int)
    : BaseRecyclerAdapter<String>(context, list, itemLayoutId) {

    override fun bindData(holder: ViewHolder?, position: Int, path: String?) {
        val ivImg = holder?.getView(R.id.iv_item_issue_dynamic) as? ImageView
        val width = WindowUtil.dip2px(context, 35f)
        Glide.with(context).load(path).apply(RequestOptions().override(width, width)).into(ivImg!!)
    }

    override fun ConfigView(view: View?, resId: Int) {
        super.ConfigView(view, resId)
        val width = WindowUtil.dip2px(context, 35f)
        val img = view?.findViewById<ImageView>(R.id.iv_item_issue_dynamic)
        val param = img?.layoutParams as? RelativeLayout.LayoutParams
        param?.width = width
        param?.height = width
    }

}