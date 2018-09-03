package com.gxtc.huchuan.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.AbsBaseAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.MessageBean

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/13.
 */
class GroupInfoAdapter(context: Context?, datas: MutableList<MessageBean>?, itemLayoutId: Int)
    : AbsBaseAdapter<MessageBean>(context, datas, itemLayoutId) {

    override fun bindData(holder: AbsBaseAdapter<out Any>.ViewHolder?, bean: MessageBean?, position: Int) {
        val ivHead = holder?.getView(R.id.iv_head) as? ImageView
        val tvName = holder?.getView(R.id.tv_name) as? TextView

        if("+" == bean?.userCode){
            ivHead?.setImageResource(R.drawable.person_add_icon)
            ivHead?.scaleType = ImageView.ScaleType.FIT_XY
            tvName?.text = ""
            return
        }

        if("-" == bean?.userCode){
            ivHead?.setImageResource(R.drawable.person_jian_icon)
            ivHead?.scaleType = ImageView.ScaleType.FIT_XY
            tvName?.text = ""
            return
        }

        ivHead?.scaleType = ImageView.ScaleType.CENTER_CROP
        ImageHelper.loadRound(context, ivHead, bean?.userPic, 4)
        tvName?.text = bean?.userName
    }

}