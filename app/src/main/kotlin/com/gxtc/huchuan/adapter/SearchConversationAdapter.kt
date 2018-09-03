package com.gxtc.huchuan.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.widget.TextView
import com.gxtc.commlibrary.base.AbsBaseAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.SearchChatBean
import io.rong.imkit.widget.AsyncImageView

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/7/24.
 */
class SearchConversationAdapter(context : Context, datas : MutableList<SearchChatBean>, id : Int): AbsBaseAdapter<SearchChatBean>(context,datas,id) {

    override fun bindData(holder: AbsBaseAdapter<out Any>.ViewHolder?, bean: SearchChatBean?, position: Int) {
        val img = holder?.getView(R.id.img_head) as? AsyncImageView
        val tvName = holder?.getView(R.id.tv_name) as? TextView
        val tvRemark = holder?.getView(R.id.tv_remake) as? TextView

        ImageHelper.loadRound(context,img,bean?.pic,4)
        tvName?.text = bean?.name
        tvRemark?.text = bean?.remarkName

        if(bean?.type == 0){
            tvName?.setCompoundDrawables(null,null,null,null)
        }else{
            val span = "[图片]"
            val name = span + " " + bean?.name
            var d: Drawable? = null
            if (bean?.code?.contains("user")!!) {
                d = context.getResources().getDrawable(R.drawable.icon_list_group)
            } else {
                d = context.getResources().getDrawable(R.drawable.icon_list_circle)
            }
            d!!.setBounds(0, 0, d.minimumWidth, d.minimumHeight)
            val imageSpan = ImageSpan(d, ImageSpan.ALIGN_BASELINE)
            val spannableString = SpannableString(name)
            spannableString.setSpan(imageSpan, 0, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvName?.setCompoundDrawables(d,null,null,null)
        }
        tvName?.compoundDrawablePadding = context.resources.getDimension(R.dimen.margin_tiny).toInt()

    }

}