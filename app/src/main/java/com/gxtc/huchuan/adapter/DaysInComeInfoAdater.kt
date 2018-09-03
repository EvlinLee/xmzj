package com.gxtc.huchuan.adapter

import android.app.Activity
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TimeUtils
import android.view.View
import android.widget.TextView
import com.gxtc.commlibrary.base.BaseRecyclerAdapter
import com.gxtc.commlibrary.helper.ImageHelper
import com.gxtc.huchuan.R
import com.gxtc.huchuan.bean.NewDistributeBean
import com.gxtc.huchuan.bean.StatisticBean
import com.gxtc.huchuan.data.UserManager
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity
import com.gxtc.huchuan.utils.DateUtil
import com.gxtc.huchuan.utils.StringUtil
import java.lang.Double

/**
 * Created by zzg on 2017/12/22.
 */
class DaysInComeInfoAdater(activity: Activity, data:MutableList<StatisticBean>, res:Int) :BaseRecyclerAdapter<StatisticBean>(activity,data,res) {

    override fun bindData(holder: ViewHolder?, position: Int, bean: StatisticBean?) {
        holder?.setText(R.id.text_price,"支付:"+bean?.fee)
        holder?.setText(R.id.text_time,bean?.dateName)
        holder?.setText(R.id.text_count,"笔数:"+bean?.count)
        holder?.setText(R.id.pent_cash,"佣金:"+bean?.saleFee)
        holder?.setText(R.id.platform,"平台分成:"+bean?.midFee)
        holder?.setText(R.id.income,"实收:"+bean?.realIncome)
    }

}