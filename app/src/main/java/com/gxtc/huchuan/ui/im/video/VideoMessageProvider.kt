package com.gxtc.huchuan.ui.im.video

import android.content.Context
import android.net.Uri
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import cn.jzvd.JZVideoPlayerStandard
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.utils.EventBusUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.event.EventUploadVideoBean
import com.gxtc.huchuan.utils.DateUtil
import com.gxtc.huchuan.widget.CircularProgress
import com.gxtc.huchuan.widget.ExpandVideoPlayer
import io.rong.imkit.model.ProviderTag
import io.rong.imkit.model.UIMessage
import io.rong.imkit.widget.AsyncImageView
import io.rong.imkit.widget.RCMessageFrameLayout
import io.rong.imkit.widget.provider.IContainerItemProvider
import io.rong.imlib.model.Message
import java.lang.ref.WeakReference

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/9/15.
 */
@ProviderTag(messageContent = VideoMessage::class, showReadState = true)
class VideoMessageProvider : IContainerItemProvider.MessageProvider<VideoMessage>() {

    private var ctxReference: WeakReference<Context> ? = null

    override fun bindView(view: View?, position: Int, message: VideoMessage?, uiMessage: UIMessage?) {
        if(view != null && message != null && uiMessage != null){
            val holder = view.tag as ViewHolder
            val resouce = view.resources
            val durationParam = holder.tvDuration.layoutParams as RelativeLayout.LayoutParams
            val playParam = holder.imgPlay.layoutParams as RelativeLayout.LayoutParams

            if(uiMessage.messageDirection == Message.MessageDirection.SEND){
                holder.layoutBg.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_right_file)
                durationParam.marginEnd = resouce.getDimension(R.dimen.margin_middle).toInt()
                playParam.marginEnd = 0

            }else{
                holder.layoutBg.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_no_left)
                durationParam.marginEnd = resouce.getDimension(R.dimen.margin_small).toInt()
                playParam.marginStart = resouce.getDimension(R.dimen.margin_tiny).toInt()
            }

            setImageSize(holder, view, message)

            val times = DateUtil.timeFormatMore(message.duration * 1000)
            if("00" == times[1]){
                holder.tvDuration.text = "${times[2]}:${times[3]}"
            }else{
                holder.tvDuration.text = "${times[1]}:${times[2]}:${times[3]}"
            }

            val url = message.url
            url?.let {
                if(it.contains("http")){
                    holder.progress.visibility = View.INVISIBLE
                    holder.imgReUpload.visibility = View.INVISIBLE
                    holder.imgPlay.visibility = View.VISIBLE
                }else{
                    //这个是视频上传失败的情况
                    if(uiMessage.progress == -2){
                        holder.progress.visibility = View.INVISIBLE
                        holder.imgReUpload.visibility = View.VISIBLE

                    }else{
                        holder.progress.setProgress(uiMessage.progress)
                        holder.progress.visibility = View.VISIBLE
                        holder.imgReUpload.visibility = View.INVISIBLE
                    }
                    holder.imgPlay.visibility = View.INVISIBLE
                }
            }

            //点击重新上传视频
            holder.imgReUpload.tag = uiMessage.message
            holder.imgReUpload.setOnClickListener{ v ->
                EventBusUtil.post(EventUploadVideoBean(v.tag as Message?))
            }
        }
    }

    override fun onItemClick(view: View?, position: Int, message: VideoMessage?, uiMessage: UIMessage?) {
        val url = message?.url
        if(!url.isNullOrEmpty() && view != null && view.context != null){
            if(url!!.contains("http")){
                if(ctxReference == null){
                    ctxReference = WeakReference(view.context)
                }
                ExpandVideoPlayer.startFullscreen(ctxReference!!.get(), ExpandVideoPlayer::class.java, url, "", message.cover)
            }
        }
    }

    override fun newView(p0: Context?, p1: ViewGroup?): View {
        val view = View.inflate(p0, R.layout.item_video_provider, null)
        val holder = ViewHolder()

        holder.layoutContent = view as RelativeLayout
        holder.layoutBg = view.findViewById(R.id.layout_bg) as RCMessageFrameLayout
        holder.imgPic = view.findViewById(R.id.img_face) as AsyncImageView
        holder.imgPlay = view.findViewById(R.id.img_play) as ImageView
        holder.imgReUpload = view.findViewById(R.id.img_reUpload) as ImageView
        holder.tvDuration = view.findViewById(R.id.tv_duration) as TextView
        holder.progress = view.findViewById(R.id.progress) as CircularProgress

        view.tag = holder
        return view
    }

    class ViewHolder {
        lateinit var layoutContent: RelativeLayout
        lateinit var layoutBg: RCMessageFrameLayout
        lateinit var imgPic: AsyncImageView
        lateinit var imgPlay: ImageView
        lateinit var imgReUpload: ImageView
        lateinit var tvDuration: TextView
        lateinit var progress: CircularProgress
    }

    override fun getContentSummary(message: VideoMessage?): Spannable = SpannableString("[视频]")


    private fun setImageSize(holder: ViewHolder, view: View, message: VideoMessage) {
        val imageView = holder.imgPic
        val width = message.width
        val height = message.height
        val params = imageView.layoutParams
        val url = message.cover

        val maxWidth = imageView.maxWidth
        val maxHeight = imageView.maxHeight

        if (width > height) {
            val vw = maxHeight - imageView.paddingLeft - imageView.paddingRight
            val scale = vw.toFloat() / width.toFloat()
            val vh = height * scale
            params.height = (vh + imageView.paddingTop + imageView.paddingBottom).toInt()
            params.width = maxHeight

        } else {
            val vw = maxWidth - imageView.paddingLeft - imageView.paddingRight
            val scale = vw.toFloat() / width.toFloat()
            val vh = height * scale
            params.height = (vh + imageView.paddingTop + imageView.paddingBottom).toInt()
            params.width = maxWidth
        }

        val layoutContent = holder.layoutContent
        val contentParam = layoutContent.layoutParams
        contentParam.width = params.width
        contentParam.height = params.height

        val tvParam = holder.tvDuration.layoutParams as RelativeLayout.LayoutParams
        tvParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        tvParam.addRule(RelativeLayout.ALIGN_PARENT_END)

        if(message.localCover.isNullOrEmpty()){
            imageView.setResource(url, R.color.grey_e5e5)
        }else{
            //imageView.setLocationResource(Uri.parse(message.localCover), R.color.grey_e5e5, )
            ImageHelper.loadImage(imageView.context, imageView, message.localCover, R.color.grey_e5e5)
        }
        imageView.invalidate()
    }
}
