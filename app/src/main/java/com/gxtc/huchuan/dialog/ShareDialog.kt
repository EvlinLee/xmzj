package com.gxtc.huchuan.dialog

import android.content.Context
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.gxtc.commlibrary.base.AbBasePagerAdapter
import com.gxtc.commlibrary.base.BaseDialogFragment
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.commlibrary.widget.MyRadioGroup
import com.gxtc.huchuan.R
import com.umeng.socialize.bean.SHARE_MEDIA

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/11/18.
 * 分享面板
 */
class ShareDialog : BaseDialogFragment(), View.OnClickListener {

    companion object {
        @JvmField val ACTION_WEIXIN = "微信"
        @JvmField val ACTION_WEIXIN_CIRCLE = "朋友圈"
        @JvmField val ACTION_QQ = "QQ"
        @JvmField val ACTION_QZONE = "QQ空间"

        @JvmField val ACTION_COLLECT = "收藏"
        @JvmField val ACTION_CANCLE_COLLECT = "取消收藏"
        @JvmField val ACTION_COPY = "复制链接"
        @JvmField val ACTION_COMPLAINTS = "投诉"
        @JvmField val ACTION_REFRESH = "刷新"
        @JvmField val ACTION_CIRCLE = "圈子动态"
        @JvmField val ACTION_FRIENDS = "好友"
        @JvmField val ACTION_QRCODE = "二维码"
        @JvmField val ACTION_INVITE = "邀请卡"
        @JvmField val ACTION_SAVE = "保存"
        @JvmField val ACTION_FREE_INVITE = "免费邀请"
    }

    var spanCount = 5
    var pageSize = spanCount * 2
    var textColor = 0
    var title = ""
    val platforms = mutableListOf(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE/*, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE*/) //分享里面的 QQ空间 QQ好友去掉
    val actions = mutableListOf<Action>()

    var shareLisntener: OnShareLisntener ? = null

    private var indicator: MyRadioGroup? = null
    private var viewPager: ViewPager? = null
    private var tvCancel: TextView? = null
    private var tvTitle: TextView? = null

    private lateinit var pagerAdapter: PagerAdapter

    override fun initView(): View {
        gravity = Gravity.BOTTOM

        val view = View.inflate(context, R.layout.dialog_share, null)
        indicator = view.findViewById(R.id.layoutIndicator) as MyRadioGroup
        viewPager = view.findViewById(R.id.viewpager) as ViewPager
        tvCancel = view.findViewById(R.id.btn_cancel) as TextView
        tvTitle = view.findViewById(R.id.tv_title) as TextView

        tvCancel?.setOnClickListener(this)
        if(!title.isNullOrEmpty()){
            tvTitle?.visibility = View.VISIBLE
            tvTitle?.text = title
        }
        if(textColor != 0){
            tvTitle?.setTextColor(textColor)
        }

        actions.let {
            val temp = mutableListOf<Action>()
            platforms.mapTo(temp){
                getAction(it)
            }

            it.addAll(0, temp)
        }

        val views = mutableListOf<View>()
        var arr = mutableListOf<Action>()
        val source = mutableListOf<MutableList<Action>>()

        for (i in actions.indices) {
            if (i % pageSize == 0) {
                arr = mutableListOf<Action>()
                arr.add(actions.get(i))

            } else {
                arr.add(actions.get(i))
                if (arr.size == pageSize || i == actions.size - 1) {
                    source.add(arr)
                    views.add(View.inflate(context, R.layout.layout_share_panels, null))
                }
            }
        }

        if(views.size == 1 && source.get(0).size <= spanCount ){
            val param = viewPager?.layoutParams as? LinearLayout.LayoutParams
            param?.height = WindowUtil.dip2px(context, 95F)
        }

        pagerAdapter = PagerAdapter(this, context, views, source, spanCount)
        pagerAdapter.shareLisntener = shareLisntener
        viewPager?.adapter = pagerAdapter
        indicator?.setCount(pagerAdapter.count, viewPager)
        if(pagerAdapter.count == 1) indicator?.visibility = View.GONE
        return view
    }

    override fun onClick(v: View?) = dismiss()


    /**
     * 设置要分享的平台
     */
    fun setPlatforms(platforms: MutableList<SHARE_MEDIA>): ShareDialog {
        this.platforms.clear()
        this.platforms.addAll(platforms)
        return this
    }

    /**
     * 添加要分享的平台
     */
    fun addPlatforms(media: SHARE_MEDIA): ShareDialog {
        this.platforms.add(media)
        return this
    }

    /**
     * 添加自定义按钮
     */
    fun addButton(action: Action): ShareDialog {
        actions.add(action)
        return this
    }

    fun addButtons(array: Array<Action>): ShareDialog {
        actions.addAll(array)
        return this
    }

    //去掉微信和QQ等选项（交易那里需要）
    fun clearDefaultePlatforms(): ShareDialog {
        platforms.clear()
        return this
    }

    fun setTitle(title: String, textColor: Int = 0): ShareDialog{
        this.title = title
        this.textColor = textColor
        return this
    }


    private fun getAction(media: SHARE_MEDIA): Action = when(media.name){

        SHARE_MEDIA.WEIXIN.name -> Action(ACTION_WEIXIN, R.drawable.share_icon_weixin, SHARE_MEDIA.WEIXIN)

        SHARE_MEDIA.WEIXIN_CIRCLE.name -> Action(ACTION_WEIXIN_CIRCLE, R.drawable.share_icon_friend, SHARE_MEDIA.WEIXIN_CIRCLE)

        SHARE_MEDIA.QQ.name -> Action(ACTION_QQ, R.drawable.share_icon_qq, SHARE_MEDIA.QQ)

        SHARE_MEDIA.QZONE.name -> Action(ACTION_QZONE, R.drawable.share_icon_kongjian, SHARE_MEDIA.QZONE)

        else -> Action("", 0, null)
    }


    class PagerAdapter(var dialog: ShareDialog, context: Context?, views: MutableList<View>?, datas: MutableList<MutableList<Action>>?, var spanCount: Int)
        : AbBasePagerAdapter<MutableList<Action>>(context, views, datas) {

        var shareLisntener: OnShareLisntener ? = null

        override fun bindData(view: View?, beans: MutableList<Action>?, position: Int) {

            val recyclerView = view?.findViewById<RecyclerView>(R.id.rv_share)
            recyclerView?.tag = beans
            val mAdapter = GridAdapter(context, beans, R.layout.item_invite_grid)
            val count = beans?.size
            if(count as Int >= 5)
              recyclerView?.layoutManager = GridLayoutManager(context, spanCount)
            else
                recyclerView?.layoutManager = GridLayoutManager(context, beans.size)

            recyclerView?.adapter = mAdapter

            mAdapter.setOnItemClickLisntener { parentView, _, position ->
                val temp = parentView.tag as MutableList<*>
                val action = temp[position] as? ShareDialog.Action

                shareLisntener?.onShare(action?.name,  action?.media)
                dialog.dismiss()
            }
        }

    }

    class GridAdapter(context: Context?, list: MutableList<Action>?, itemLayoutId: Int)
        : BaseRecyclerAdapter<Action>(context, list, itemLayoutId) {

        override fun bindData(holder: ViewHolder?, position: Int, bean: Action?) {
            val tv = holder?.getView(R.id.tv_name) as TextView
            val img = holder.getView(R.id.imgView) as ImageView

            tv.text = bean?.name
            img.setImageResource(bean?.res!!)
        }

    }


    interface OnShareLisntener {
        fun onShare(key: String ?, media: SHARE_MEDIA ?)
    }

    data class Action(var name: String, var res: Int, var media: SHARE_MEDIA ? = null)

}