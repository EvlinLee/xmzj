package com.gxtc.huchuan.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import cn.jzvd.JZVideoPlayer
import cn.jzvd.JZVideoPlayerStandard
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.NewsBean
import com.gxtc.huchuan.data.ArticleSpecialBean
import com.gxtc.huchuan.ui.MainContract

/**
 * Created by zzg on 2018/5/12
 */
class SpecialVideoAdater(mActivity: Activity?, datas: MutableList<ArticleSpecialBean>, resId: Int,isPay:String) : BaseRecyclerAdapter<ArticleSpecialBean>(mActivity, datas, resId) {
    var seeListener: View.OnClickListener? = null
    val isPay: String = isPay
    override fun bindData(holder: ViewHolder?, position: Int, bean: ArticleSpecialBean?) {
        //视频标题
        val tvVideoTitle = holder?.getView(R.id.tv_news_video_title) as TextView
        val player = holder.getView(R.id.play_news_video_cover) as JZVideoPlayerStandard
        val status = holder.getView(R.id.to_do) as TextView
        tvVideoTitle.setText(bean?.title)
//        bean?.getVideoUrl().let {
//            player.setUp(it, JZVideoPlayer.SCREEN_WINDOW_LIST, "", bean?.getCover())
//            ImageHelper.loadImage(context, player.thumbImageView, bean?.getCover())
//        }
        if ("1" == isPay) {
            status.setText("立即观看")
            status.setTextColor(getContext().resources.getColor(R.color.pay_finish))
        } else {
            //0=非专题文章或视频，1=免费，2=收费，3=试看
            when (bean?.isFreeSee) {
                1 -> {
                    status.setText("免费观看")
                    status.setTextColor(getContext().resources.getColor(R.color.pay_finish))
                }
                2 -> {
                    status.setText("订阅观看")
                    status.setTextColor(getContext().resources.getColor(R.color.color_ff7531))
                }
                3 -> {
                    status.setText("试看")
                    status.setTextColor(getContext().resources.getColor(R.color.color_2b8cff))
                }
            }
        }

        status.setOnClickListener { v ->
            seeListener?.let {
                v.tag = bean
                it.onClick(v)
            }
        }
    }
}