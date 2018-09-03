package com.gxtc.huchuan.ui.im.merge

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gxtc.huchuan.R
import io.rong.imkit.emoticon.AndroidEmoji
import io.rong.imkit.model.ProviderTag
import io.rong.imkit.model.UIMessage
import io.rong.imkit.widget.provider.IContainerItemProvider
import io.rong.imlib.model.Message

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/5/28.
 */
@ProviderTag(messageContent = MergeHistoryMessage::class, showReadState = true)
class MergeHistoryMessageProvider: IContainerItemProvider.MessageProvider<MergeHistoryMessage>() {

    override fun newView(p0: Context?, p1: ViewGroup?): View {
        val view = View.inflate(p0, R.layout.item_merge_message_provider, null)
        val holder = MergeHistoryMessageProvider.ViewHolder()

        holder.tvTitle = view.findViewById(R.id.tv_title) as? TextView
        holder.tvContent = view.findViewById(R.id.tv_content) as? TextView
        holder.layoutContent = view.findViewById(R.id.layout_content)

        view.tag = holder
        return view
    }

    override fun bindView(view: View?, position: Int, message: MergeHistoryMessage?, uiMessage: UIMessage?) {
        val holder = view?.tag as? MergeHistoryMessageProvider.ViewHolder

        //发送方
        if (uiMessage?.getMessageDirection() == Message.MessageDirection.SEND) {
            holder?.layoutContent?.setBackgroundResource(R.drawable.rc_ic_bubble_right_file)

        //接收方
        } else {
            holder?.layoutContent?.setBackgroundResource(R.drawable.rc_ic_bubble_left_file)
        }

        holder?.tvTitle?.text = "${message?.title}的聊天记录"
        holder?.tvContent?.text = AndroidEmoji.ensure(message?.content)
    }

    override fun getContentSummary(p0: MergeHistoryMessage?): Spannable = SpannableString("[聊天记录]")

    override fun onItemClick(p0: View?, p1: Int, mergeMessage: MergeHistoryMessage?, p3: UIMessage?) {
        val context = p0?.context
        context?.apply {
            MergeHistoryActivity.startActivity(this, mergeMessage?.id, mergeMessage?.title)
        }
    }

    class ViewHolder {
        var layoutContent: View? = null
        var tvTitle: TextView? = null
        var tvContent: TextView? = null
    }

}