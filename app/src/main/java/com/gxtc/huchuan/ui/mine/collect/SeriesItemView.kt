package com.gxtc.huchuan.ui.mine.collect

import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.recyclerview.base.ItemViewDelegate
import com.gxtc.commlibrary.recyclerview.base.ViewHolder
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.CollectionBean
import com.gxtc.huchuan.bean.SeriesPageBean
import com.gxtc.huchuan.utils.DateUtil
import com.gxtc.huchuan.widget.RoundImageView

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/10/6.
 */
class SeriesItemView : ItemViewDelegate<CollectionBean> {

    override fun getItemViewLayoutId(): Int = R.layout.item_collect_live

    override fun isForViewType(item: CollectionBean?, position: Int): Boolean = "9" == item?.type

    override fun convert(holder: ViewHolder?, t: CollectionBean?, position: Int) {
        val bean = t?.getData<SeriesPageBean>()

        val headUrl = if(t?.userPic.isNullOrEmpty()) bean?.anchorPic else t?.userPic
        val picRound = holder?.getView<ImageView>(R.id.iv_news_collect_property)
        ImageHelper.loadCircle(holder?.convertView?.context, picRound, headUrl, R.drawable.person_icon_head)

        val face = holder?.getView<ImageView>(R.id.iv_item_live_list_image)
        ImageHelper.loadImage(holder?.convertView?.context, face, bean?.headpic, R.drawable.live_list_place_holder)

        val time = holder?.getView<TextView>(R.id.tv_news_collect_time)
        time?.text = DateUtil.showTimeAgo(t?.createTime)

        val title = holder?.getView<TextView>(R.id.tv_item_live_list_title)
        title?.text = bean?.seriesname

        val name = if(t?.userName.isNullOrEmpty()) bean?.chatRoomName else t?.userName
        val tvName = holder?.getView<TextView>(R.id.tv_item_live_list_content)
        tvName?.text = name
    }
}