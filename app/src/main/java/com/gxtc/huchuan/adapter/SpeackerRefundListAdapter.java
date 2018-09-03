package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;
import java.util.Locale;

/**
 * 来自 zzg on 17/9/15
 */

public class SpeackerRefundListAdapter extends BaseRecyclerAdapter<PurchaseListBean> {

    public SpeackerRefundListAdapter(Context context, List<PurchaseListBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, PurchaseListBean bean) {
        TextView tvTitle = (TextView) holder.getView(R.id.title);
        TextView tvMoney = (TextView) holder.getView(R.id.tv_cash);
        TextView tvStatus = (TextView) holder.getView(R.id.tv_status);
        TextView tvTime = (TextView) holder.getView(R.id.tv_time);


//        String time = DateUtil.formatTime(bean.getCreateTime(),"yyyy-mm-dd hh-MM-ss");
        String time = DateUtil.stampToDate(String.valueOf(bean.getCreateTime()));
        tvTime.setText(time);

        tvTitle.setText(bean.getTitle());

        String moeny = "￥" + String.format(Locale.CHINA, "%.2f", bean.getRefundMoney());
        tvMoney.setText(moeny);


        //未审核
        if(bean.getAudit() == 0){
//            Drawable d = getContext().getResources().getDrawable(R.drawable.person_deal_icon_refund);
//            d.setBounds(0,0,d.getMinimumWidth(),d.getMinimumHeight());
            tvStatus.setText("未审核");
            tvStatus.setTextColor(Color.parseColor("#9F9F9F"));
        }

        //审核通过
        if(bean.getAudit() == 1){

            //退款完成
            if(bean.getIsFinish() == 1){
//                Drawable d = getContext().getResources().getDrawable(R.drawable.person_deal_icon_refund_red);
//                d.setBounds(0,0,d.getMinimumWidth(),d.getMinimumHeight());
//                tvMsg.setCompoundDrawables(d,null,null,null);
                tvMoney.setText("退款金额:  " + moeny);
                tvStatus.setText("退款完成");
                tvStatus.setTextColor(Color.parseColor("#F26304"));

            }else{
//                Drawable d = getContext().getResources().getDrawable(R.drawable.person_deal_icon_refund);
//                d.setBounds(0,0,d.getMinimumWidth(),d.getMinimumHeight());
//                tvMsg.setCompoundDrawables(d,null,null,null);
                tvStatus.setText("退款处理中");
                tvStatus.setTextColor(Color.parseColor("#9F9F9F"));
            }

        }

        //审核不通过
        if(bean.getAudit() == 2){
//            Drawable d = getContext().getResources().getDrawable(R.drawable.person_deal_icon_refund);
//            d.setBounds(0,0,d.getMinimumWidth(),d.getMinimumHeight());
//            tvMsg.setCompoundDrawables(d,null,null,null);
            tvStatus.setText("拒绝");
            tvStatus.setTextColor(Color.parseColor("#9F9F9F"));
        }



    }
}
