package com.gxtc.huchuan.dialog

import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseDialogFragment
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.commlibrary.utils.EventBusUtil
import com.gxtc.commlibrary.utils.ToastUtil
import com.gxtc.commlibrary.utils.WindowUtil
import com.gxtc.huchuan.Constant
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.SeriesPageBean
import com.gxtc.huchuan.bean.event.EventSeriesPayBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.utils.UMShareUtils
import de.hdodenhof.circleimageview.CircleImageView

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/3/15.
 * 系列课邀请弹窗
 */
class SeriesCourseInviteDialog(): BaseDialogFragment() {

    private lateinit var bean: SeriesPageBean

    constructor(bean: SeriesPageBean): this(){
        this.bean = bean
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        context?.let {
            width = (WindowUtil.getScreenWidth(context) - it.resources.getDimension(R.dimen.margin_large) * 2).toInt()
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun initView(): View {
        val view = View.inflate(context, R.layout.dialog_series_invite, null)
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val tvContent = view.findViewById<TextView>(R.id.tv_content)
        val btn = view.findViewById<TextView>(R.id.btn)
        val imgHead = view.findViewById<ImageView>(R.id.img_head)
        val layout = view.findViewById<LinearLayout>(R.id.layout_invite)

        ImageHelper.loadCircle(context, imgHead, UserManager.getInstance().headPic)
        tvTitle.text = Html.fromHtml("讲师开启了邀请制免费听课功能，需邀请<font color='#FF0000'>${bean.invitationFreeNum}</font>位好友参与才能解锁后面课程")
        tvContent.text = Html.fromHtml("你已邀请<font color='#FF0000'>${bean.userInvitationSharNum}</font>位，还差<font color='#FF0000'>${bean.invitationFreeNum - bean.userInvitationSharNum}</font>位")

        val count = if(bean.invitationFreeNum > 5) 5 - 1  else bean.invitationFreeNum - 1
        for(i in 0..count){
            val dimensionPixelOffset = resources.getDimensionPixelOffset(R.dimen.px70dp)
            val layoutParams = LinearLayout.LayoutParams(dimensionPixelOffset, dimensionPixelOffset)
            val margin = resources.getDimensionPixelOffset(R.dimen.margin_tiny)
            layoutParams.setMargins(margin, margin, margin, margin)
            val imageView = ImageView(context)
            imageView.layoutParams = layoutParams
            if(bean.invitationBySharUser != null && i < bean.invitationBySharUser.size){
                ImageHelper.loadCircle(context, imageView, bean.invitationBySharUser[i].headPic)
            }else{
                imageView.setImageResource(R.drawable.live_icon_default)
            }
            layout.addView(imageView)
        }

        btn.setOnClickListener {
            bean.let {
                val utils = UMShareUtils(activity)
                val uri = if (TextUtils.isEmpty(bean.headpic)) Constant.DEFUAL_SHARE_IMAGE_URI else bean.headpic
                utils.shareClassInviteFree(uri, bean.seriesname, bean.introduce, bean.shareUrl)
            }
        }

        return view
    }



}