package ui.mine.collect

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.recyclerview.RecyclerView
import com.gxtc.commlibrary.recyclerview.base.ViewHolder
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.CircleHomeBean
import com.gxtc.huchuan.bean.CollectionBean
import com.gxtc.huchuan.handler.CircleShareHandler
import com.gxtc.huchuan.ui.mine.collect.CircleCollectItemView
import com.gxtc.huchuan.ui.mine.collect.CollectActivity

class CircleShareCollectItemView(collectActivity: CollectActivity, recyclerView: RecyclerView)
    : CircleCollectItemView<CollectionBean>(collectActivity, recyclerView) {

    var circleHandler : CircleShareHandler ? = null
    var mActivity : CollectActivity ? = null

    init {
        circleHandler = CircleShareHandler(collectActivity)
        mActivity = collectActivity
    }

    override fun getItemViewLayoutId(): Int {
        return R.layout.item_collect_circle_share
    }

    override fun isForViewType(item: CollectionBean, position: Int): Boolean {
        if ("4" == item.type) {
            val bean = item.getData<CircleHomeBean>()
            if (bean != null && 6 == bean.type) {
                return true
            }
        }
        return false
    }


    override fun convertContent(holder: ViewHolder, bean: CircleHomeBean, position: Int) {
        val layoutTime = holder.getView<View>(R.id.layout_time_share)
        val timeParam = layoutTime?.layoutParams as LinearLayout.LayoutParams
        timeParam.topMargin = 0

        val layoutShare = holder.getView<View>(R.id.layout_share)
        layoutShare?.visibility = View.VISIBLE
        /*val layoutParams = layoutShare?.layoutParams as LinearLayout.LayoutParams
        val res = layoutTime.context?.resources
        layoutParams.leftMargin = res?.let {
            it.getDimensionPixelSize(R.dimen.px130dp) - it.getDimensionPixelSize(R.dimen.margin_middle)
        } as Int*/
        val linearlayoutShare = holder.getView<View>(R.id.linear_lauout_share)
        val parms = linearlayoutShare.getLayoutParams() as FrameLayout.LayoutParams
        parms.topMargin = mActivity?.resources!!.getDimensionPixelSize(R.dimen.margin_tiny)
        parms.leftMargin = mActivity?.resources!!.getDimensionPixelSize(R.dimen.margin_middle)
        parms.rightMargin = mActivity?.resources!!.getDimensionPixelSize(R.dimen.margin_middle)

        val imgShare = holder.getView<View>(R.id.img_share) as ImageView
        val tvShare = holder.getView<View>(R.id.tv_share) as TextView
        ImageHelper.loadImage(layoutTime.context, imgShare, bean.typeCover, R.drawable.live_list_place_holder_120)
        tvShare.text = bean.typeTitle
        layoutShare.tag = bean
        layoutShare.setOnClickListener { view->
            val shareBean = view?.tag as CircleHomeBean ?
            circleHandler?.shareHandle(view?.context,shareBean?.typeId,shareBean?.infoType!!,null)
        }

    }
}
