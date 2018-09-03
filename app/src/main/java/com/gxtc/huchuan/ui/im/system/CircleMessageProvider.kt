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
@ProviderTag(messageContent = CircleMessage::class, showReadState = true, showPortrait = false, showSummaryWithName = false, centerInHorizontal = true)
class CircleMessageProvider : IContainerItemProvider.MessageProvider<CircleMessage>() {

    override fun getContentSummary(message: CircleMessage?): Spannable {
        val s = if(TextUtils.isEmpty(message?.content)) "您有一条圈子消息" else message?.content
        return SpannableString(s)
    }

    override fun bindView(view: View?, position: Int, message: CircleMessage?, uiMessage: UIMessage?) {
        val resource = view?.resources
        val param = view?.layoutParams
        param?.width = (WindowUtil.getScreenW(view?.context) - resource?.getDimension(R.dimen.middle_text_size)!! * 2).toInt()

        val holder = view.tag as? ViewHolder

        ImageHelper.loadRound(view.context, holder?.imgHead, message?.groupPic, 2)
        holder?.tvName?.text = message?.groupName
        holder?.tvTitle?.text = message?.content

        when (message?.msgType) {
            0, 1, 4, 6, 7, 8 -> {
                holder?.tvDetailed?.visibility = View.VISIBLE
            }

            2, 3, 5 -> {
                holder?.tvDetailed?.visibility = View.GONE
            }
        }

    }

    override fun onItemClick(view: View?, position: Int, message: CircleMessage?, uiMessage: UIMessage?) {

    }

    override fun newView(p0: Context?, p1: ViewGroup?): View {
        val view = View.inflate(p0, R.layout.item_circle_provider, null)
        val holder = ViewHolder()

        holder.imgHead = view.findViewById(R.id.img_head) as? ImageView
        holder.tvName = view.findViewById(R.id.tv_name) as? TextView
        holder.tvTitle = view.findViewById(R.id.tv_title) as? TextView
        holder.tvDetailed = view.findViewById(R.id.tv_detailed) as? TextView

        view.tag = holder
        return view
    }

    class ViewHolder {
        var imgHead: ImageView? = null
        var tvName: TextView? = null
        var tvTitle: TextView? = null
        var tvDetailed: TextView? = null
    }
}