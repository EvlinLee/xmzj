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
import com.gxtc.huchuan.ui.mine.personalhomepage.personalinfo.PersonalInfoActivity
import com.gxtc.huchuan.utils.DateUtil
import com.gxtc.huchuan.utils.StringUtil
import java.lang.Double

/**
 * Created by zzg on 2017/12/22.
 */
class InComeInfoAdater(activity: Activity,data:ArrayList<NewDistributeBean>,res:Int) :BaseRecyclerAdapter<NewDistributeBean>(activity,data,res) {

    override fun bindData(holder: ViewHolder?, position: Int, bean: NewDistributeBean?) {
        holder?.setText(R.id.text_buyer,"购买人:"+bean?.name)
        holder?.setText(R.id.text_time, DateUtil.stampToDate(bean?.createTime.toString()))
        holder?.setText(R.id.text_mouney,"￥"+bean?.commission)
        ImageHelper.loadRound(context,holder?.getViewV2(R.id.headPic),bean?.headPic,4)
        holder?.setText(R.id.text_price,"价格:￥"+bean?.fee)

        if("1".equals(bean?.isSett)){
            if("1".equals(bean?.isRefund)){
                holder?.setText(R.id.tv_type,"(已退款)")
            }else{
                holder?.setText(R.id.tv_type,"(已结算)")
            }
        }else{
            if("1".equals(bean?.isRefund)){
                holder?.setText(R.id.tv_type,"(已退款)")
            }else{
                holder?.setText(R.id.tv_type,"(未结算)")
            }
        }

        if(!"0".equals(bean?.commission) && !"0.0".equals(bean?.commission)){
            holder?.setText(R.id.text_pent,"返"+(StringUtil.formatMoney(2,bean?.pent!!.toDouble())))
        }else{
            holder?.setText(R.id.text_pent,"免费邀请")
        }
    }

}