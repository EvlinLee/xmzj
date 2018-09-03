package com.gxtc.huchuan.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.other.GlideCircularTransform
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.WithdrawRecordBean
import com.gxtc.huchuan.utils.DateUtil
import com.gxtc.huchuan.utils.StringUtil

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/8/31.
 */
class WithdrawRecordAdapter(context: Context?, list: MutableList<WithdrawRecordBean>?, itemLayoutId: Int)
    : BaseRecyclerAdapter<WithdrawRecordBean>(context, list, itemLayoutId) {

    override fun bindData(holder: ViewHolder?, position: Int, bean: WithdrawRecordBean?) {
        val tvAccount = holder?.getView(R.id.tv_account) as? TextView
        val tvTime = holder?.getView(R.id.tv_time) as? TextView
        val tvMoney = holder?.getView(R.id.tv_money) as? TextView
        val tvStatus = holder?.getView(R.id.tv_status) as? TextView
        val img = holder?.getView(R.id.img) as? ImageView

        tvAccount?.text = bean?.userAccount
        tvTime?.text = DateUtil.showTimeAgo(bean?.createTime)
        tvMoney?.text = StringUtil.formatMoney(2,bean?.money!!)

        when(bean.withdrawCashType){
            1->{ Glide.with(context).load(R.drawable.pay_wx).apply(RequestOptions().transform(GlideCircularTransform(context))).into(img!!) }
            2->{ Glide.with(context).load(R.drawable.pay_alipay).apply(RequestOptions().transform(GlideCircularTransform(context))).into(img!!) }
            3->{ Glide.with(context).load(R.drawable.icon_yinhangka).into(img!!) }
        }

        when(bean.withdrawStatus){
            0->{
                tvStatus?.setTextColor(getContext().resources.getColor(R.color.tool_bar_bg))
                tvStatus?.text = "审核中"
            }
            1->{
                tvStatus?.setTextColor(getContext().resources.getColor(R.color.tool_bar_bg))
                tvStatus?.text = "审核通过"
            }
            2->{
                tvStatus?.setTextColor(getContext().resources.getColor(R.color.pay_failure))
                tvStatus?.text = "审核拒绝"
            }
            3->{
                tvStatus?.setTextColor(getContext().resources.getColor(R.color.pay_finish))
                tvStatus?.text = "完成"
            }
        }
    }
}