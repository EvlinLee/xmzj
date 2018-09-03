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
import io.rong.imkit.model.ProviderTag
import io.rong.imkit.model.UIMessage
import io.rong.imkit.widget.provider.IContainerItemProvider

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/15.
 */
@ProviderTag(messageContent = MallMessage::class, showReadState = true, showPortrait = false, showSummaryWithName = false, centerInHorizontal = true)
class MallMessageProvider : IContainerItemProvider.MessageProvider<MallMessage>() {

    override fun getContentSummary(message: MallMessage?): Spannable {
        val s = if(TextUtils.isEmpty(message?.title)) "您有一条订单消息" else message?.title
        return SpannableString(s)
    }

    override fun bindView(view: View?, position: Int, message: MallMessage?, uiMessage: UIMessage?) {
        val resource = view?.resources
        val param = view?.layoutParams
        param?.width = (WindowUtil.getScreenW(view?.context) - resource?.getDimension(R.dimen.middle_text_size)!! * 2).toInt()

        val holder = view.tag as? ViewHolder
        ImageHelper.loadImage(view.context, holder?.imgPic, message?.cover, R.drawable.live_list_place_holder_120)

        holder?.tvTitle?.text = message?.title
        var color = view.context.resources.getColor(R.color.order_status_red);
        when(message?.msgType){
            1 -> {
                holder?.tvStatus?.text = "订单待付款"
                color = view.context.resources.getColor(R.color.order_status_red);
            }

            2 -> {
                holder?.tvStatus?.text = "订单已付款"
                color = view.context.resources.getColor(R.color.order_status_yellow);
            }

            3 -> {
                holder?.tvStatus?.text = "订单已发货"
                color = view.context.resources.getColor(R.color.order_status_blue);
            }

            4 -> {
                holder?.tvStatus?.text = "订单已完成"
                color = view.context.resources.getColor(R.color.order_status_green);
            }

            5 -> {
                holder?.tvStatus?.text = "订单已退款"
                color = view.context.resources.getColor(R.color.order_status_orange);
            }

            6 -> {
                holder?.tvStatus?.text = "正在退款"
                color = view.context.resources.getColor(R.color.order_status_orange);
            }

        }
        holder?.tvStatus?.setTextColor(color)
        holder?.tvOrderNum?.text = message?.orderNum

    }

    override fun onItemClick(view: View?, position: Int, message: MallMessage?, uiMessage: UIMessage?) {

    }

    override fun newView(p0: Context?, p1: ViewGroup?): View {
        val view = View.inflate(p0, R.layout.item_mall_provider, null)
        val holder = ViewHolder()

        holder.imgPic = view.findViewById(R.id.img_cover) as? ImageView
        holder.tvTitle = view.findViewById(R.id.tv_title) as? TextView
        holder.tvOrderNum = view.findViewById(R.id.tv_orderNum) as? TextView
        holder.tvStatus = view.findViewById(R.id.tv_status) as? TextView

        view.tag = holder
        return view
    }

    class ViewHolder {
        var imgPic: ImageView? = null
        var tvTitle: TextView? = null
        var tvOrderNum: TextView? = null
        var tvStatus: TextView? = null
    }
}