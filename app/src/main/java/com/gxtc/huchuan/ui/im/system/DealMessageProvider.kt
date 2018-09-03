package com.gxtc.huchuan.ui.im.system

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.utils.DateUtil
import io.rong.imkit.model.ProviderTag
import io.rong.imkit.model.UIMessage
import io.rong.imkit.widget.provider.IContainerItemProvider

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/15.
 */
@ProviderTag(messageContent = DealMessage::class, showReadState = true, showPortrait = false, showSummaryWithName = false, centerInHorizontal = true)
class DealMessageProvider : IContainerItemProvider.MessageProvider<DealMessage>() {

    override fun getContentSummary(message: DealMessage?): Spannable {
        val s = if(TextUtils.isEmpty(message?.content)) "您有一条交易消息" else message?.content
        return SpannableString(s)
    }

    override fun bindView(view: View?, position: Int, message: DealMessage?, uiMessage: UIMessage?) {
        val resource = view?.resources
        val param = view?.layoutParams
        param?.width = (WindowUtil.getScreenW(view?.context) - resource?.getDimension(R.dimen.middle_text_size)!! * 2).toInt()

        val holder = view.tag as? ViewHolder

        ImageHelper.loadRound(view.context, holder?.imgHead, message?.userPic, 2)

        //0:发起交易，1：卖家同意交易？，2，买家支付，3：卖家交付，4：买家确认交易完成  5：交易已关闭  10：卖家不同意
        var status = ""
        when (message?.status) {
            0 -> status = "等待卖家同意"
            1 -> status = "等待买家支付"
            2 -> status = "等待卖家交付"
            3 -> status = "等待买家确认"
            4 -> status = "交易已完成"
            5 -> status = "交易已关闭"
            10 -> status = "卖家不同意交易"
        }

        holder?.tvName?.text = message?.userName
        holder?.tvTitle?.text = message?.content
        holder?.tvGoodsName?.text = message?.title
        holder?.tvBtn?.visibility = View.VISIBLE

        when (message?.msgType) {
        //订单状态通知
            0 -> {
                setAllVisibility(holder,View.VISIBLE)
                holder?.tvOrderId?.text = message.orderNum
                holder?.tvTime?.text = DateUtil.formatTime(message.time, "yyyy-MM-dd HH:mm")
            }

        //退款通知
            1 -> {
                setAllVisibility(holder,View.VISIBLE)
                holder?.tvOrderId?.text = message.orderNum
                holder?.tvTime?.text = DateUtil.formatTime(message.time, "yyyy-MM-dd HH:mm")
            }

        //好友交易通知
            2 -> {
                setAllVisibility(holder,View.GONE)
            }

        //交易结算
            3 -> {
                holder?.tvBtn?.visibility = View.GONE
            }
        }

        holder?.tvStatus?.text = status
    }

    private fun setAllVisibility(holder: ViewHolder?, visible: Int) {
        holder?.tv2?.visibility = visible
        holder?.tv3?.visibility = visible
        holder?.tv4?.visibility = visible
        holder?.tvStatus?.visibility = visible
        holder?.tvTime?.visibility = visible
        holder?.tvOrderId?.visibility = visible
    }

    override fun onItemClick(view: View?, position: Int, message: DealMessage?, uiMessage: UIMessage?) {

    }

    override fun newView(p0: Context?, p1: ViewGroup?): View {
        val view = View.inflate(p0, R.layout.item_deal_provider, null)
        val holder = ViewHolder()

        holder.imgHead = view.findViewById(R.id.img_head) as? ImageView
        holder.tvName = view.findViewById(R.id.tv_name) as? TextView
        holder.tvTitle = view.findViewById(R.id.tv_title) as? TextView
        holder.tvGoodsName = view.findViewById(R.id.tv_goods_name) as? TextView
        holder.tvTime = view.findViewById(R.id.tv_time) as? TextView
        holder.tvStatus = view.findViewById(R.id.tv_status) as? TextView
        holder.tvOrderId = view.findViewById(R.id.tv_number) as? TextView
        holder.tv2 = view.findViewById(R.id.tv2) as? TextView
        holder.tv3 = view.findViewById(R.id.tv3) as? TextView
        holder.tv4 = view.findViewById(R.id.tv4) as? TextView
        holder.tvBtn = view.findViewById(R.id.tv_btn) as? TextView

        view.tag = holder
        return view
    }

    class ViewHolder {
        var imgHead: ImageView? = null
        var tvName: TextView? = null
        var tvTitle: TextView? = null
        var tvGoodsName: TextView? = null
        var tvTime: TextView? = null
        var tvStatus: TextView? = null
        var tvOrderId: TextView? = null
        var tv2: TextView? = null
        var tv3: TextView? = null
        var tv4: TextView? = null
        var tvBtn: TextView? = null

    }

}