package com.gxtc.huchuan.adapter

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.UnifyClassBean
import com.gxtc.huchuan.utils.DateUtil

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/11/30.
 * 接口要换新的了  所以适配器要重新换掉
 * @see LiveRoomNewAdapter  此类后面将要弃用
 */
class UnifyClassAdapter(context: Context?, list: MutableList<UnifyClassBean>?, itemLayoutId: Int)
    : BaseRecyclerAdapter<UnifyClassBean>(context, list, itemLayoutId) {

    override fun bindData(holder: ViewHolder?, position: Int, t: UnifyClassBean?) {
        val bean = t?.data

        if(holder != null && bean != null){
            val price = holder.getViewV2<TextView>(R.id.item_live_room_price)
            if (bean.isfree == 0) {
                price.setTextColor(holder.getItemView().context.resources.getColor(R.color.text_color_999))
                price.text = "免费"
            } else {
                price.setTextColor(holder.getItemView().context.resources.getColor(R.color.color_fb4717))

                price.visibility = View.VISIBLE
                price.text = String.format("￥%s", bean.fee)
            }

            val tvTime = holder.getViewV2<View>(R.id.living_time) as TextView
            if (3 == bean.status) {
                tvTime.text = "已开始"
            } else {
                setTime(t, tvTime)
            }

            holder.setText(R.id.item_live_room_name, bean.chatRoomName).setText(R.id.item_live_room_title, bean.subtitle)

            val imgFace = holder.getImageView(R.id.item_live_room_image)
            ImageHelper.loadRound(getContext(), imgFace, bean.facePic, 4)

            val status = holder.getViewV2<TextView>(R.id.tv_item_live_list_status)
            //系列课标志
            if (1 == t.type) {
                status.visibility = View.GONE
                tvTime.visibility = View.VISIBLE
            } else {
                status.visibility = View.VISIBLE
                tvTime.visibility = View.GONE
            }

            val tvCount = holder.getViewV2<TextView>(R.id.living_count)
            tvCount.text = "${bean.joinCount}人次"
            tvCount.visibility = View.VISIBLE
        }
    }

    private fun setTime(data: UnifyClassBean, mTvStarttime: TextView) {
        val bean = data.data
        if (TextUtils.isEmpty(bean?.starttime)) return

        val aLong = java.lang.Long.valueOf(bean?.starttime)

        mTvStarttime.text = DateUtil.formatTime(aLong, "MM-dd HH:mm")
        val l = aLong!! - System.currentTimeMillis()
        if (l > 0) {
            val strings = DateUtil.countDownNotAddZero(l)
            if (strings[0] != "0") {
                mTvStarttime.text = strings[0] + "天后"

            } else if (strings[1] != "0") {
                mTvStarttime.text = strings[1] + "小时后"

            } else if (strings[2] != "0") {
                mTvStarttime.text = strings[2] + "分后"

            } else {
                mTvStarttime.text = "进行中"
            }
        }
    }

}