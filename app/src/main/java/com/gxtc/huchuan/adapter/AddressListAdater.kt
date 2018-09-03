package com.gxtc.huchuan.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.PersonInfoBean

/**
 * Created by zzg on 2017/11/21.
 */
class AddressListAdater(context : Context, list: ArrayList<PersonInfoBean>, res:Int) : BaseRecyclerAdapter<PersonInfoBean>(context,list,res) {

     var addListenber :addFriendsListener? = null

    interface addFriendsListener{
        fun addFriend(user: PersonInfoBean?, position: Int)
    }

    override fun bindData(holder: ViewHolder?, position: Int, user: PersonInfoBean?) {
        holder?.getViewV2<TextView>(R.id.tv_item_focus_name)?.text = user?.name
        holder?.getViewV2<TextView>(R.id.tv_item_focus_introduce)?.text = "手机号：${user?.phones}"

        if(user?.headPic.isNullOrEmpty()){
            ImageHelper.loadRound(context, holder?.getViewV2<ImageView>(R.id.riv_item_focus), R.drawable.circle_head_icon_120, 4)
        }else{
            ImageHelper.loadRound(context, holder?.getViewV2<ImageView>(R.id.riv_item_focus), user?.headPic, 4)
        }

        holder?.getView(R.id.add_frends)?.setOnClickListener {
            addListenber?.addFriend(user,position)
        }
        setSendBtnUI(user!! ,holder?.getViewV2<TextView>(R.id.add_frends)!!)
    }

    private fun setSendBtnUI(mPersonInfoBean:PersonInfoBean,addFruends:TextView) {
        //已经注册app
        if(mPersonInfoBean.userCode != null){
            if (mPersonInfoBean.isFans == 1 && mPersonInfoBean.isFollow == 1) {
                addFruends.visibility = View.VISIBLE
                addFruends.text = "已添加"
                addFruends.setTextColor(context.resources.getColor(R.color.text_color_999))
                addFruends.setBackgroundResource(R.color.white)
                addFruends.isClickable = false
                mPersonInfoBean.chatStatus = -1
            }

            if (mPersonInfoBean.isFans == 0 && mPersonInfoBean.isFollow == 0) {
                addFruends.visibility = View.VISIBLE
                addFruends.text = "添加"
                addFruends.setTextColor(context.resources.getColor(R.color.colorAccent))
                addFruends.setBackgroundResource(R.drawable.shape_border_raido_accent)
                addFruends.isClickable = true
                mPersonInfoBean.chatStatus = 0
            }

            if (mPersonInfoBean.isFollow == 0 && mPersonInfoBean.isFans == 1) {
                addFruends.visibility = View.VISIBLE
                addFruends.text = "接受"
                addFruends.isClickable = true
                addFruends.setTextColor(context.resources.getColor(R.color.white))
                addFruends.setBackgroundResource(R.drawable.shape_audit_blue)
                mPersonInfoBean.chatStatus = 1
            }

            if (mPersonInfoBean.isFollow == 1 && mPersonInfoBean.isFans == 0) {
                addFruends.visibility = View.VISIBLE
                addFruends.text = "等待验证"
                addFruends.isClickable = false
                addFruends.setTextColor(context.resources.getColor(R.color.text_color_999))
                addFruends.setBackgroundResource(R.color.white)
                mPersonInfoBean.chatStatus = 2
            }

        }else{
            addFruends.visibility = View.VISIBLE
            addFruends.text = "邀请"
            addFruends.setTextColor(context.resources.getColor(R.color.text_color_666))
            addFruends.setBackgroundResource(R.drawable.shape_border_raido_grenn)
            addFruends.isClickable = true
            mPersonInfoBean.chatStatus = 0
        }

    }

     fun setOnAddListenber(l:addFriendsListener?):Unit{
         addListenber = l;
    }
}