package com.gxtc.huchuan.adapter

import android.app.Activity
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.NewDistributeBean
import com.gxtc.huchuan.bean.StatisticDetailBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity
import com.gxtc.huchuan.utils.DateUtil
import com.gxtc.huchuan.utils.StringUtil
import java.lang.Double

/**
 * Created by zzg on 2017/12/22.
 */
class StatisticIncomeDetailAdater(activity: Activity, data:MutableList<StatisticDetailBean>, res:Int) :BaseRecyclerAdapter<StatisticDetailBean>(activity,data,res) {

    override fun bindData(holder: ViewHolder?, position: Int, bean: StatisticDetailBean?) {
        holder?.setText(R.id.text_price,"支付:￥"+bean?.fee)
        holder?.setText(R.id.text_time,  DateUtil.stampToDate(bean?.signuptime,"yyyy-MM-dd"))
        ImageHelper.loadRound(context,holder?.getViewV2(R.id.head_pic), bean?.headPic,4)

        when(bean?.paySource){
         "0" -> holder?.setText(R.id.text_from,"来源:app")
         "1" -> holder?.setText(R.id.text_from,"来源:微信")
         "2" -> holder?.setText(R.id.text_from,"来源:网页")
          else ->  holder?.setText(R.id.text_from,"来源:其他")
        }
        holder?.setText(R.id.pent_cash,"佣金:￥"+bean?.saleFee)
        holder?.setText(R.id.platform,"平台分成:￥"+bean?.midFee)
        holder?.setText(R.id.income,"实收:￥"+bean?.realIncome)
        holder?.setText(R.id.text_username,"购买人:"+bean?.name)
    }

}