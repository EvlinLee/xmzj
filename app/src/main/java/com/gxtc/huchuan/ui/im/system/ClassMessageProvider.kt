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
@ProviderTag(messageContent = ClassMessage::class, showReadState = true, showPortrait = false, showSummaryWithName = false, centerInHorizontal = true)
class ClassMessageProvider : IContainerItemProvider.MessageProvider<ClassMessage>() {

    override fun getContentSummary(message: ClassMessage?): Spannable {
        val s = if(TextUtils.isEmpty(message?.content)) "您有一条课堂消息" else message?.content
        return SpannableString(s)
    }

    override fun bindView(view: View?, position: Int, message: ClassMessage?, uiMessage: UIMessage?) {
        val resource = view?.resources
        val param = view?.layoutParams
        param?.width = (WindowUtil.getScreenW(view?.context) - resource?.getDimension(R.dimen.middle_text_size)!! * 2).toInt()

        val holder = view.tag as? ViewHolder
        ImageHelper.loadRound(view.context, holder?.imgHead, message?.userPic, 2)
        ImageHelper.loadImage(view.context, holder?.imgCover, message?.pic)

        holder?.tvName?.text = message?.userName
        holder?.tvClassName?.text = message?.title
        holder?.tvTitle?.text = message?.content

        if (TextUtils.isEmpty(message?.content)) {
            holder?.tvTitle?.visibility = View.GONE
        } else {
            holder?.tvTitle?.visibility = View.VISIBLE
        }

        /**
         * 0、审核通知（跳课堂管理）1、好友开课通知（跳课堂）2、开课通知（跳课堂）
         * 3、课堂变动（不跳）4、结算通知 5、退款通知（退款申请列表） 6、创建课堂通知
         */
        when (message?.msgType) {
            0, 1, 2, 5, 6 -> {
                holder?.tvDetailed?.visibility = View.VISIBLE
            }

            3, 4 -> {
                holder?.tvDetailed?.visibility = View.GONE
            }
        }

        val time = DateUtil.formatTime(message?.orderTime, "yyyy-MM-dd HH:mm")
        holder?.tvTime?.text = time
    }

    override fun onItemClick(view: View?, position: Int, message: ClassMessage?, uiMessage: UIMessage?) {

    }

    override fun newView(p0: Context?, p1: ViewGroup?): View {
        val view = View.inflate(p0, R.layout.item_class_provider, null)
        val holder = ViewHolder()

        holder.imgHead = view.findViewById(R.id.img_head) as? ImageView
        holder.imgCover = view.findViewById(R.id.img_cover) as? ImageView
        holder.tvName = view.findViewById(R.id.tv_name) as? TextView
        holder.tvTitle = view.findViewById(R.id.tv_title) as? TextView
        holder.tvClassName = view.findViewById(R.id.tv_class_name) as? TextView
        holder.tvTime = view.findViewById(R.id.tv_time) as? TextView
        holder.tvDetailed = view.findViewById(R.id.tv_detailed) as? TextView

        view.tag = holder
        return view
    }

    class ViewHolder {
        var imgHead: ImageView? = null
        var imgCover: ImageView? = null
        var tvName: TextView? = null
        var tvTitle: TextView? = null
        var tvClassName: TextView? = null
        var tvTime: TextView? = null
        var tvDetailed: TextView? = null
    }
}