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
@ProviderTag(messageContent = ArticleMessage::class, showReadState = true, showPortrait = false, showSummaryWithName = false, centerInHorizontal = true)
class ArticleMessageProvider : IContainerItemProvider.MessageProvider<ArticleMessage>() {

    override fun getContentSummary(message: ArticleMessage?): Spannable {
        val s = if(TextUtils.isEmpty(message?.content)) "您有一条文章消息" else message?.content
        return SpannableString(s)
    }

    override fun bindView(view: View?, position: Int, message: ArticleMessage?, uiMessage: UIMessage?) {
        val resource = view?.resources
        val param = view?.layoutParams
        param?.width = (WindowUtil.getScreenW(view?.context) - resource?.getDimension(R.dimen.middle_text_size)!! * 2).toInt()

        val holder = view.tag as? ViewHolder
        ImageHelper.loadImage(view.context,holder?.imgPic,message?.pic,R.drawable.live_list_place_holder_120)

        holder?.tvTitle?.text = message?.title
        holder?.tvContent?.text = message?.content
        holder?.tvTime?.text = DateUtil.formatTime(message?.time,"yyyy-MM-dd HH:mm")

        when(message?.msgType){
            0,2->{
                holder?.tvDetailed?.visibility = View.VISIBLE
            }

            1->{
                holder?.tvDetailed?.visibility = View.GONE
            }
        }
    }

    override fun onItemClick(view: View?, position: Int, message: ArticleMessage?, uiMessage: UIMessage?) {

    }

    override fun newView(p0: Context?, p1: ViewGroup?): View {
        val view = View.inflate(p0, R.layout.item_article_provider, null)
        val holder = ViewHolder()

        holder.imgPic = view.findViewById(R.id.img_head) as? ImageView
        holder.tvTitle = view.findViewById(R.id.tv_name) as? TextView
        holder.tvTime = view.findViewById(R.id.tv_time) as? TextView
        holder.tvContent = view.findViewById(R.id.tv_title) as? TextView
        holder.tvDetailed = view.findViewById(R.id.tv_detailed) as? TextView

        view.tag = holder
        return view
    }

    class ViewHolder {
        var imgPic: ImageView? = null
        var tvTitle: TextView? = null
        var tvTime: TextView? = null
        var tvContent: TextView? = null
        var tvDetailed: TextView? = null
    }
}