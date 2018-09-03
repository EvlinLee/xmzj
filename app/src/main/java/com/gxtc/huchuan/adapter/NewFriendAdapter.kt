package com.gxtc.huchuan.adapter

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.FocusBean

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/12.
 */
class NewFriendAdapter(context: Context?, list: MutableList<FocusBean>?, itemLayoutId: Int)
    : BaseRecyclerAdapter<FocusBean>(context, list, itemLayoutId) {

    var clickListener : View.OnClickListener ? = null

    override fun bindData(holder: ViewHolder?, position: Int, bean: FocusBean?) {
        val btn = holder?.getView(R.id.btn_follow) as? TextView
        val tvName = holder?.getView(R.id.tv_item_focus_name)  as? TextView
        val img = holder?.getView(R.id.riv_item_focus)  as? ImageView

        tvName?.text = bean?.userName
        ImageHelper.loadRound(context, img, bean?.userHeadPic, 4)
        btn?.visibility = View.VISIBLE

        if(!TextUtils.isEmpty(bean?.message)){
            holder?.setText(R.id.tv_author,bean?.message)
        }else{
            holder?.setText(R.id.tv_author,"请求添加你为好友")
        }

        when(bean?.isMyFriend){
            "0" ->  {
                btn?.text = "接受"
                btn?.setTextColor(context.resources.getColor(R.color.text_color_333))
                btn?.setBackgroundResource(R.drawable.shape_border_raido_accent)

            }
            "1" ->  {
                btn?.text = "已接受"
                btn?.setTextColor(context.resources.getColor(R.color.text_color_999))
                btn?.setBackgroundResource(0)
            }
        }
        bean?.position = position
        btn?.tag = bean
        btn?.setOnClickListener{
            v -> clickListener?.onClick(v)
        }
    }

    fun setOnClickListener(listener: View.OnClickListener){
        clickListener = listener
    }
}