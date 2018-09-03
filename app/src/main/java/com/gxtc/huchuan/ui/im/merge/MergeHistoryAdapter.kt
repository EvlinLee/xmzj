package com.gxtc.huchuan.ui.im.merge

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerTypeAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.utils.GsonUtil
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.MergeMessageBean
import com.gxtc.huchuan.utils.DateUtil
import com.gxtc.huchuan.utils.StringUtil
import com.gxtc.huchuan.widget.ExpandVideoPlayer
import io.rong.imkit.emoticon.AndroidEmoji

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/5/30.
 */
class MergeHistoryAdapter(mContext: Context?, mDatas: MutableList<MergeMessageBean>?, resId: IntArray?)
    : BaseRecyclerTypeAdapter<MergeMessageBean>(mContext, mDatas, resId) {

    private val key_content = "content"

    constructor(mContext: Context?, mDatas: MutableList<MergeMessageBean>?)
            : this(mContext, mDatas, intArrayOf(R.layout.item_merge_text,
                                                R.layout.item_merge_image,
                                                R.layout.item_merge_voice,
                                                R.layout.item_merge_video,
                                                R.layout.item_merge_no_support)) {

    }

    override fun getViewType(position: Int): Int {
        val bean = datas[position]
        return when(bean.objectName){
            "RC:TxtMsg", "RCBQMM:EmojiMsg", "RCBQMM:GifMsg"-> 0
            "RC:ImgMsg" -> 1
            "RC:VcMsg" -> 2
            "VideoMsg" -> 3
            else -> 4
        }
    }

    override fun bindData(holder: RecyclerView.ViewHolder?, position: Int, type: Int, bean: MergeMessageBean?) {
        val myHolder = holder as BaseRecyclerTypeAdapter<*>.ViewHolder
        fillCommon(myHolder,position, bean)

        when(type){
            0-> fillText(myHolder, position, bean)

            1-> fillImage(myHolder, position, bean)

            2-> fillVoice(myHolder, position, bean)

            3-> fillVideo(myHolder, position, bean)

            4-> fillNoSupport(myHolder, position, bean)
        }
    }

    //文本消息
    private fun fillText(holder: BaseRecyclerTypeAdapter<*>.ViewHolder?, position: Int, bean: MergeMessageBean?) {
        val content = GsonUtil.getJsonValue(bean?.content, key_content) as? String
        val tvContent = holder?.getView(R.id.tv_content) as TextView

        when(bean?.objectName){
            "RC:TxtMsg", "RCBQMM:EmojiMsg" -> tvContent.text = AndroidEmoji.ensure(content)

            "RCBQMM:GifMsg"-> tvContent.text = "[表情]"
        }
    }

    //图片消息
    private fun fillImage(holder: BaseRecyclerTypeAdapter<*>.ViewHolder?, position: Int, bean: MergeMessageBean?) {
        val url = GsonUtil.getJsonValue(bean?.content, "imageUri") as? String
        val img = holder?.getView(R.id.img_content) as ImageView

        ImageHelper.loadIntoUseFitWidthOrHeight(getmContext(), url, R.drawable.icon_placeholder, img)
    }

    //语音消息
    private fun fillVoice(holder: BaseRecyclerTypeAdapter<*>.ViewHolder?, position: Int, bean: MergeMessageBean?) {

    }

    //视频消息
    private fun fillVideo(holder: BaseRecyclerTypeAdapter<*>.ViewHolder?, position: Int, bean: MergeMessageBean?) {
        val duration = GsonUtil.getJsonValue(bean?.content, "duration") as? Int
        val imgUrl = GsonUtil.getJsonValue(bean?.content, "cover") as? String
        val videoUrl = GsonUtil.getJsonValue(bean?.content, "url") as? String

        val tvDuration = holder?.getView(R.id.tv_duration) as TextView
        val imgFace = holder.getView(R.id.img_video_face) as ImageView
        val layoutVideo = holder.getView(R.id.layout_video)

        layoutVideo.tag = "$videoUrl,$imgUrl"
        layoutVideo.setOnClickListener { v ->
            val tag = v.tag as String
            val array = tag.split(",")
            ExpandVideoPlayer.startFullscreen(getmContext(), ExpandVideoPlayer::class.java, array[0], "", array[1])
        }

        tvDuration.text = "${duration}秒"
        ImageHelper.loadImage(getmContext(), imgFace, imgUrl)
    }

    //未支持消息
    private fun fillNoSupport(holder: BaseRecyclerTypeAdapter<*>.ViewHolder?, position: Int, bean: MergeMessageBean?) {

    }

    private fun fillCommon(holder: BaseRecyclerTypeAdapter<*>.ViewHolder?, position:Int, bean: MergeMessageBean?){
        val tvName = holder?.getView(R.id.tv_name) as TextView
        val tvTime = holder.getView(R.id.tv_time) as TextView
        val imgHead = holder.getView(R.id.img_head) as ImageView

        if(position != 0){
            val lastBean = datas[position - 1]
            val lastUserCode = lastBean?.userCode
            lastUserCode?.apply {
                if(this == bean?.userCode){
                    imgHead.visibility = View.INVISIBLE
                }else{
                    imgHead.visibility = View.VISIBLE
                }
            }

        }else{
            imgHead.visibility = View.VISIBLE
        }

        tvTime.text = DateUtil.formatTime(StringUtil.toLong(bean?.createTime), "MM-dd HH:mm:ss")
        tvName.text = bean?.userName
        ImageHelper.loadImage(getmContext(), imgHead, bean?.headPic)
    }

}