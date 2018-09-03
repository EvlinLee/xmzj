package com.gxtc.huchuan.ui.im.system

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gxtc.huchuan.R
import io.rong.imkit.model.ProviderTag
import io.rong.imkit.model.UIMessage
import io.rong.imkit.widget.provider.IContainerItemProvider

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/15.
 */
@ProviderTag(messageContent = SystemMessage::class, showReadState = true, showPortrait = false, showSummaryWithName = false, centerInHorizontal = true)
class SystemMessageProvider : IContainerItemProvider.MessageProvider<SystemMessage>() {

    override fun getContentSummary(message: SystemMessage?): Spannable {
        val s = if(TextUtils.isEmpty(message?.content)) "您有一条系统消息" else message?.content
        return SpannableString(s)
    }

    override fun bindView(view: View?, position: Int, message: SystemMessage?, uiMessage: UIMessage?) {
        /*val resource = view?.resources
        val param = view?.layoutParams
        param?.width = (WindowUtil.getScreenW(view?.context) - resource?.getDimension(R.dimen.middle_text_size)!! * 2).toInt()*/

        val holder = view?.tag as? ViewHolder
        holder?.tvTitle?.text = message?.content
    }

    override fun onItemClick(view: View?, position: Int, message: SystemMessage?, uiMessage: UIMessage?) = Unit

    override fun newView(p0: Context?, p1: ViewGroup?): View {
        val view = View.inflate(p0, R.layout.item_system_provider, null)
        val holder = ViewHolder()

        holder.tvTitle = view.findViewById(R.id.tv_title) as? TextView

        view.tag = holder
        return view
    }

    class ViewHolder {
        var tvTitle: TextView? = null
    }
}