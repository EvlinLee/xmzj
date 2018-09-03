package im.collect

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.handler.CircleShareHandler
import io.rong.imkit.model.ProviderTag
import io.rong.imkit.model.UIMessage
import io.rong.imkit.widget.provider.IContainerItemProvider
import io.rong.imlib.model.Message

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/7/15.
 */
@ProviderTag(messageContent = CollectMessage::class, showReadState = true)
class CollectMessageProvider() : IContainerItemProvider.MessageProvider<CollectMessage>() {

    var msgHandler : CircleShareHandler ? = null

    override fun newView(p0: Context?, p1: ViewGroup?): View {
        val view = View.inflate(p0, R.layout.item_collect_message,null)
        val holder = ViewHolder()
        holder.layoutContent = view.findViewById(R.id.layout_content)
        holder.layoutCollect = view.findViewById(R.id.layout_share)
        holder.layoutNote = view.findViewById(R.id.layout_note)
        holder.label = view.findViewById(R.id.tv_label) as? TextView
        holder.title = view.findViewById(R.id.tv_share) as? TextView
        holder.note = view.findViewById(R.id.tv_note) as? TextView
        holder.img = view.findViewById(R.id.img_share) as? ImageView
        view.tag = holder
        return view
    }

    override fun bindView(view: View?, position: Int, collectMessage: CollectMessage?, uiMessage: UIMessage?) {
        val holder = view?.tag as ViewHolder?

        //发送方
        if(uiMessage?.messageDirection == Message.MessageDirection.SEND){
            holder?.layoutContent?.background = view?.context?.resources?.getDrawable(R.drawable.rc_ic_bubble_right_file)
            val resource = view?.context?.resources
            val marginLeft = if(resource?.getDimensionPixelSize(R.dimen.margin_small) == 0) 0 else resource?.getDimensionPixelSize(R.dimen.margin_small)
            val marginTop = if(resource?.getDimensionPixelSize(R.dimen.margin_tiny) == 0) 0 else resource?.getDimensionPixelSize(R.dimen.margin_tiny)
            holder?.layoutContent?.setPadding(marginTop!!,marginTop!!,marginTop,marginTop)
        }else{
            holder?.layoutContent?.background = view?.context?.resources?.getDrawable(R.drawable.rc_ic_bubble_left_file)
            val resource = view?.context?.resources
            val marginTop = if(resource?.getDimensionPixelSize(R.dimen.margin_tiny) == 0) 0 else resource?.getDimensionPixelSize(R.dimen.margin_tiny)
            val marginLeft = if(resource?.getDimensionPixelSize(R.dimen.margin_middle) == 0) 0 else resource?.getDimensionPixelSize(R.dimen.margin_middle)
            holder?.layoutContent?.setPadding(marginLeft!!,marginTop!!,marginLeft,marginTop)
        }

        when(collectMessage?.type){
            //笔记
            "5"->{
                holder?.note?.text = collectMessage.content
                holder?.layoutCollect?.visibility = View.GONE
                holder?.layoutNote?.visibility = View.VISIBLE
                return
            }

            "1" -> holder?.label?.text = "文章"
            "2"-> holder?.label?.text = "课堂"
            "3"-> holder?.label?.text = "交易"
            "4"-> holder?.label?.text = "动态"
            "6"-> holder?.label?.text = "圈子"
            "7"-> holder?.label?.text = "系列课"
            "8"-> holder?.label?.text = "商品"
            "9"-> holder?.label?.text = "直播间"
        }
        holder?.title?.text = collectMessage?.content
        ImageHelper.loadImage(view?.context,holder?.img,collectMessage?.cover,R.drawable.share_icon_copy)
        holder?.layoutNote?.visibility = View.GONE
        holder?.layoutCollect?.visibility = View.VISIBLE
    }

    override fun onItemClick(view: View?, position: Int, cMessage: CollectMessage?, uiMessage: UIMessage?) {
        /*val type = cMessage?.type
        val id = cMessage?.id
        val url = cMessage?.url

        if (msgHandler == null) msgHandler = CircleShareHandler(view?.context)
        msgHandler?.collectHandle(id,type,url)*/
    }

    override fun onItemLongClick(view: View?, position: Int, collectMessage: CollectMessage?, uiMessage: UIMessage?) {
    }

    override fun getContentSummary(collectMessage: CollectMessage?): Spannable {
        return SpannableString(collectMessage?.content)
    }

    class ViewHolder {
        var img: ImageView? = null
        var title: TextView? = null
        var note: TextView? = null
        var label: TextView? = null
        var layoutContent: View? = null
        var layoutCollect: View? = null
        var layoutNote: View? = null

    }

}