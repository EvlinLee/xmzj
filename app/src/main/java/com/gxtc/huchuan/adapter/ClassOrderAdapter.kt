package com.gxtc.huchuan.adapter

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.widget.ImageView
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.ClassOrderBean
import com.gxtc.huchuan.utils.DateUtil
import com.gxtc.huchuan.utils.StringUtil
import com.gxtc.huchuan.utils.SystemUtil
import java.text.SimpleDateFormat

/**
 * 来自 伍玉南 的装逼小尾巴 on 18/3/30.
 */
class ClassOrderAdapter(context: Context?, list: MutableList<ClassOrderBean>?, itemLayoutId: Int)
    : BaseRecyclerAdapter<ClassOrderBean>(context, list, itemLayoutId) {

    override fun bindData(holder: ViewHolder?, position: Int, bean: ClassOrderBean?) {

        var paySource = "" ;
        var fee = StringUtil.formatMoney(2, bean?.fee as Double)
        var saleFee = StringUtil.formatMoney(2, bean?.saleFee as Double)
        var midFee = StringUtil.formatMoney(2, bean?.midFee as Double)
        var realIncome = StringUtil.formatMoney(2, bean?.realIncome as Double)

        var signuptime  = bean?.signuptime as Long
        if(bean?.paySource == "0"){
            paySource = "app"
        }
        else if(bean?.paySource == "1"){
            paySource = "微信"
        }
        else if(bean?.paySource == "5"){
            paySource = "其他"
        }

        holder?.getViewV2<TextView>(R.id.text_price)?.text = "支付："+ fee
        holder?.getViewV2<TextView>(R.id.text_username)?.text = "${bean?.name}"
        holder?.getViewV2<TextView>(R.id.text_time)?.text = DateUtil.formatTime(signuptime,"yyyy-MM-dd");
        holder?.getViewV2<TextView>(R.id.text_source)?.text = "来源："+paySource
        holder?.getViewV2<TextView>(R.id.pent_cash)?.text = "佣金："+saleFee
        holder?.getViewV2<TextView>(R.id.platform)?.text = "平台："+midFee
        var text1 = "实收："
        var text2 = realIncome
        val ss2 = SpannableString(text1 + text2 )
        val dealSpan2 = TextAppearanceSpan(context, R.style.circle_invite_text_color)
        ss2.setSpan(dealSpan2, text1.length, text1.length + text2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder?.getViewV2<TextView>(R.id.income)?.text = ss2
        ImageHelper.loadRound(context,holder?.getViewV2<ImageView>(R.id.iv_userlogo),bean?.headPic as String,5)
    }
}