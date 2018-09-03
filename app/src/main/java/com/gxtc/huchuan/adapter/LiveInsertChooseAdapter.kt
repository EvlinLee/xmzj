package com.gxtc.huchuan.adapter

import android.content.Context
import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerTypeAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.MyApplication
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.DealListBean
import com.gxtc.huchuan.bean.LiveInsertBean
import com.gxtc.huchuan.ui.live.conversation.LiveInsertChooseActivity
import com.gxtc.huchuan.utils.DateUtil
import com.umeng.socialize.utils.DeviceConfig.context

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/3/5.
 */
class LiveInsertChooseAdapter(var type: Int = 1, mContext: Context?, datas: MutableList<LiveInsertBean>?, vararg resid: Int)
    : BaseRecyclerTypeAdapter<LiveInsertBean>(mContext, datas, resid) {


    override fun getViewType(position: Int): Int {
        if(type != LiveInsertChooseActivity.TYPE_CLASS){
            return type
        }else{
            //课程
            if(datas[position].type == 1){
                return type
            //系列课
            }else{
                return LiveInsertChooseActivity.TYPE_SERISE
            }
        }
    }


    override fun bindData(holder: RecyclerView.ViewHolder?, position: Int, type: Int, t: LiveInsertBean?) {
        val holder = holder as BaseRecyclerTypeAdapter<LiveInsertBean>.ViewHolder
        when(type){
            //圈子
            LiveInsertChooseActivity.TYPE_CIRCLE -> {
                val imgFace = holder.getView(R.id.img_icon) as? ImageView
                ImageHelper.loadImage(getmContext(), imgFace, t?.cover)

                val tvTitle = holder.getView(R.id.tv_name) as? TextView
                tvTitle?.text = t?.title

                val tvContent = holder.getView(R.id.tv_title) as? TextView
                tvContent?.text = t?.content

                val tvBottom = holder.getView(R.id.tv_bottom) as? TextView
                tvBottom?.text = "动态${t?.infoNum}  关注${t?.attention}"

                val tvId = holder.getView(R.id.tv_id) as? TextView
                tvId?.text = "id:${t?.id}"
            }

            //文章
            LiveInsertChooseActivity.TYPE_ARTICLE -> {
                val imgFace = holder.getView(R.id.iv_news_collect_cover) as ImageView
                ImageHelper.loadImage(getmContext(), imgFace, t?.cover)

                val tvCount = holder.getView(R.id.tv_read_count) as TextView
                tvCount.text = "阅读:${t?.readCount}"

                val tvTime = holder.getView(R.id.tv_news_collect_time) as TextView
                tvTime.text = DateUtil.showTimeAgo(t?.date)

                val tvAuthor = holder.getView(R.id.tv_news_collect_author) as TextView
                tvAuthor.text = t?.author

                val tvTitle = holder.getView(R.id.tv_news_collect_title) as TextView
                tvTitle.text = t?.title
            }


            //课程
            LiveInsertChooseActivity.TYPE_CLASS -> {
                setTopData(holder, position, t)
            }


            //系列课
            LiveInsertChooseActivity.TYPE_SERISE -> {
                setSeriseData(holder, position, t)
            }

            //交易
            LiveInsertChooseActivity.TYPE_DEAL -> {
                val dealBean = t?.dealBean
                setDealData(holder, position, dealBean)
            }


            //商品
            LiveInsertChooseActivity.TYPE_MALL -> {
                val mallBean = t?.mallBean
                val title = holder.getView(R.id.item_goods_title) as TextView
                title.text = mallBean?.name
                val sourPrice = holder.getView(R.id.source_price) as TextView
                sourPrice.visibility = View.GONE
                sourPrice.paint.flags = Paint. STRIKE_THRU_TEXT_FLAG;
                val price = holder.getView(R.id.price) as TextView
                price.text = "￥ "+ mallBean?.price
                val image = holder.getView(R.id.item_live_room_image) as ImageView
                image.visibility = View.VISIBLE
                ImageHelper.loadImage(getmContext(), image, mallBean?.facePic)
            }
        }
    }

    private fun setDealData(holder: ViewHolder, position: Int, bean: DealListBean?) {
        val imgFace = holder.getView(R.id.img_face) as ImageView
        val imgStatus = holder.getView(R.id.img_status) as TextView
        val tvTitle = holder.getView(R.id.tv_title) as TextView
        val tvName = holder.getView(R.id.tv_name) as TextView
        val tvTime = holder.getView(R.id.tv_time) as TextView
        val tvType = holder.getView(R.id.tv_type) as TextView
        val tvLiuyan = holder.getView(R.id.tv_liuyan) as TextView
        val tvStatus = holder.getView(R.id.tv_status) as TextView
        val tvLook = holder.getView(R.id.tv_look) as TextView

        val status = bean?.tradeType
        val s: SpannableStringBuilder
        //出售
        if ("0" == status) {
            s = SpannableStringBuilder("[出售]")
            //购买
        } else {
            s = SpannableStringBuilder("[求购]")
        }
        val span = ForegroundColorSpan(getmContext().resources.getColor(R.color.color_price_ec6b46))
        s.setSpan(span, 1, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvStatus.text = s


        val isTop = bean?.isTop
        if ("0" == isTop) {
            val title = bean?.title
            tvTitle.text = title
        } else {
            val sb = SpannableString("置顶" + " " + bean?.title)
            val imageSpan = ImageSpan(getmContext(), R.drawable.deal_icon_top, ImageSpan.ALIGN_BASELINE)
            sb.setSpan(imageSpan, 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvTitle.text = sb
        }

        val name = bean?.userName
        //0不匿名   1匿名
        if (bean?.anonymous == "0") {
            tvName.text = name
        } else {
            if (name!!.length > 1) {
                tvName.text = name.substring(0, 1) + "**"
            } else {
                tvName.text = name + "**"
            }
        }
        if ("0" == bean.remainNum) {
            imgStatus.visibility = View.VISIBLE
            imgStatus.text = "已售完"
            imgStatus.setBackgroundResource(R.drawable.deal_homepage_sell_bottom)
            imgStatus.setTextColor(context.resources.getColor(R.color.white))
        } else {
            if (bean.workOff != null) {
                val wordOff = Integer.parseInt(bean.workOff)
                if (wordOff > 0 && wordOff <= 99) {
                    imgStatus.visibility = View.VISIBLE
                    imgStatus.text = "已售" + bean.workOff
                    imgStatus.setBackgroundResource(R.drawable.deal_homepage_in_progress_bottom)
                    imgStatus.setTextColor(context.resources.getColor(R.color.white))
                } else {
                    if (wordOff == 0) {
                        imgStatus.visibility = View.GONE
                    } else {
                        imgStatus.visibility = View.VISIBLE
                        imgStatus.text = "已售99+"
                        imgStatus.setBackgroundResource(R.drawable.deal_homepage_in_progress_bottom)
                        imgStatus.setTextColor(context.resources.getColor(R.color.white))
                    }
                }
            } else {
                imgStatus.visibility = View.GONE
            }

        }
        val type = bean.tradeTypeSonName
        tvType.text = type

        val time = bean.createTime
        tvTime.text = DateUtil.diffCurTime(java.lang.Long.valueOf(time)!!, "MM-dd  HH:mm")

        val liuyan = bean.liuYan
        tvLiuyan.text = liuyan + "留言"

        val look = bean.read
        tvLook.text = " / " + look + "浏览"

        ImageHelper.loadImage(getmContext(), imgFace, bean.picUrl, R.drawable.circle_place_holder_246)
    }


    private fun setSeriseData(holder: ViewHolder, position: Int, bean: LiveInsertBean?) {
        //系列课图片
        val imageView = holder.getView(R.id.iv_item_topic_image) as ImageView
        ImageHelper.loadRound(MyApplication.getInstance(), imageView, bean!!.cover, 4)

        holder.getView(R.id.tv_item_live_list_status).visibility = View.VISIBLE
        //标题
        val tvTitle = holder.getView(R.id.tv_item_topic_title) as TextView
        tvTitle.setText(bean.title)

        //购买人数
        val tvPerson = holder.getView(R.id.tv_item_topic_people) as TextView
        tvPerson.setText(bean.joinCount + "人次")

        //购买人数
        val tvToatal = holder.getView(R.id.tv_total) as TextView
        tvToatal.text = "共有" + bean.chatInfoCount + "节"


        //价格
        val tvPrice = holder.getView(R.id.tv_item_topic_price) as TextView
        if (0 == bean.isFree) { //免费
            tvPrice.setTextColor(getmContext().resources.getColor(R.color.series_free))
            tvPrice.text = "免费"
        } else {
            tvPrice.setTextColor(getmContext().resources.getColor(R.color.series_no_free))
            tvPrice.text = "￥" + bean.fee + "元"
        }

        val tvMore = holder.getView(R.id.tv_item_topic_manager) as TextView
        tvMore.visibility = View.GONE
    }

    private fun setTopData(holder: ViewHolder, position: Int, o: LiveInsertBean?) {
        val statusIame = holder.getView(R.id.iv_item_topic_isliving) as ImageView
        val price = holder.getView(R.id.tv_item_topic_price) as TextView
        val title = holder.getView(R.id.tv_item_topic_title) as TextView
        val content = holder.getView(R.id.tv_item_topic_content) as TextView
        val peopleCount = holder.getView(R.id.tv_item_topic_people) as TextView
        title.text = o!!.title
        content.text = "￥${o.fee}"
        content.setTextColor(content.resources.getColor(R.color.color_fb4717))
        peopleCount.text = "${o.joinCount}人次"
        val checkStatus = holder.getView(R.id.tv_check_status) as TextView
        holder.getView(R.id.tv_item_topic_manager).visibility = View.GONE
        if ("1" == o.isSelf) {
            price.visibility = View.GONE
        } else {
            price.visibility = View.VISIBLE
        }
        if (0 == o.isFree) {
            price.text = "免费"
            price.setTextColor(holder.itemView.resources.getColor(R.color.text_color_999))
        } else {
            price.text = o.fee + "元"
            price.setTextColor(holder.itemView.resources.getColor(R.color.color_fb4717))
        }

        //０未审核１：已审核，２：审核不通过   新媒之家官方审核
        when (o.audit) {
            "0" -> {
                statusIame.visibility = View.GONE
                checkStatus.text = "未审核"
            }

            "1" -> {
                statusIame.visibility = View.VISIBLE
                checkStatus.text = "审核成功"
            }

            "2" -> {
                statusIame.visibility = View.GONE
                checkStatus.text = "审核不通过"
            }
        }

        checkStatus.visibility = if (o.isSelf == "1") View.VISIBLE else View.GONE
        setStatus(o, statusIame, holder.getView(R.id.tv_time) as TextView)
        setTime(holder.getView(R.id.tv_time) as TextView, o)

        ImageHelper.loadRound(MyApplication.getInstance(), holder.getView(R.id.iv_item_topic_image) as ImageView, o.cover, 4)
    }


    private fun setStatus(o: LiveInsertBean, statusIame: ImageView, time: TextView) {

        when (o.status) {
        //预告
            "1" -> {
                time.visibility = View.VISIBLE
                statusIame.visibility = View.VISIBLE
                statusIame.setImageResource(R.drawable.class_no_start)
            }

        //直播中
            "2" -> {
                time.visibility = View.GONE
                statusIame.visibility = View.VISIBLE
                statusIame.setImageResource(R.drawable.playing)
            }

        //结束
            "3" -> {
                time.visibility = View.GONE
                statusIame.visibility = View.VISIBLE
                statusIame.setImageResource(R.drawable.live_list_yikaishi)
            }
        }

        //0  正常  1结束  2下架
        if ("2" == o.showinfo) {
            statusIame.visibility = View.VISIBLE
            statusIame.setImageResource(R.drawable.down)
        }
    }


    fun setTime(view: TextView, o: LiveInsertBean) {
        if (TextUtils.isEmpty(o.time)) {
            view.visibility = View.GONE
        } else {
            var aLong: Long? = 0L
            try {
                aLong = java.lang.Long.valueOf(o.time)
            } catch (e: Exception) {
                aLong = 0L
            }

            val l = aLong!! - System.currentTimeMillis()
            if (l > 0) {
                val strings = DateUtil.countDownNotAddZero(l)
                if (strings[0] != "0") {
                    view.text = strings[0] + "天后"
                } else if (strings[1] != "0") {
                    view.text = strings[1] + "小时后"
                }
            }
        }
    }
}