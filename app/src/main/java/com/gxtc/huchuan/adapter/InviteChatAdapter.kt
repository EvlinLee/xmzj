package com.gxtc.huchuan.adapter

import android.content.Context
import android.support.v7.widget.AppCompatCheckBox
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.FocusBean

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/7.
 */
class InviteChatAdapter(context: Context?, list: MutableList<FocusBean>?, itemLayoutId: Int, var addFriend: Int)
    : BaseRecyclerAdapter<FocusBean>(context, list, itemLayoutId) {

    var checkListener : View.OnClickListener ? = null

    override fun bindData(holder: ViewHolder?, position: Int, bean: FocusBean?) {
        val imgHead = holder?.getView(R.id.riv_item_focus) as? ImageView
        val tvName = holder?.getView(R.id.tv_item_focus_name) as? TextView
        val checkbox = holder?.getView(R.id.checkbox) as? AppCompatCheckBox
        if(TextUtils.isEmpty(bean?.userHeadPic)){
            bean?.userHeadPic = bean?.userPic //因为踢人跟拉人获取列表所用的接口不一样，输出参数就是 userPic userHeadPic 这两个个不一样
            ImageHelper.loadRound(context, imgHead, bean?.userHeadPic, 4)
        }else{
            ImageHelper.loadRound(context, imgHead, bean?.userHeadPic, 4)
        }

        val name = bean?.userName
        tvName?.text = name

        checkbox?.visibility = View.VISIBLE
        checkbox?.isChecked = bean?.isSelect!!
        checkbox?.tag = bean
        checkbox?.setOnClickListener {
            v-> checkListener?.onClick(v)
        }

        //判断成员是否已经在群里，在就不可以点
        if(addFriend != 0){
            when(bean.isGroupMember){  //0、不是；1、是
                "1" -> {
                    checkbox?.isChecked = true
                    checkbox?.isEnabled = false
                    checkbox?.setOnClickListener(null)
                }
                else -> {
                    checkbox?.isEnabled = true
                    checkbox?.setOnClickListener {
                        v-> checkListener?.onClick(v)
                    }
                }
            }
        }
    }

}