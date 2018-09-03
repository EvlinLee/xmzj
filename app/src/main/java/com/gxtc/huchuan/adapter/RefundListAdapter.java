package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/9.
 */

public class RefundListAdapter extends BaseRecyclerAdapter<PurchaseListBean> {

    public RefundListAdapter(Context context, List<PurchaseListBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, PurchaseListBean bean) {
        TextView tvTime = (TextView) holder.getView(R.id.tv_time);
        TextView tvTitle = (TextView) holder.getView(R.id.tv_name);
        TextView tvMoney = (TextView) holder.getView(R.id.tv_money);
        TextView tvOrder = (TextView) holder.getView(R.id.tv_order);
        TextView tvMsg = (TextView) holder.getView(R.id.tv_msg);
        TextView tvRefundMoney = (TextView) holder.getView(R.id.tv_refund_money);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String time = sdf.format(new Date(Long.valueOf(bean.getOrderTime())));
        tvTime.setText(time);

        tvTitle.setText(bean.getTradeInfoTitle());

        String moeny = "￥"+bean.getOrderPrice();
        tvMoney.setText(moeny);
         String refundMoney = "￥" + String.format(Locale.CHINA, "%.2f", bean.getRefundMoney());

        tvOrder.setText(bean.getOrderId());
        //退款完成
        if(bean.getIsFinish() == 1){
            Drawable d = getContext().getResources().getDrawable(R.drawable.person_deal_icon_refund_red);
            d.setBounds(0,0,d.getMinimumWidth(),d.getMinimumHeight());
            tvMsg.setCompoundDrawables(d,null,null,null);
            tvMsg.setText("退款成功");
            tvRefundMoney.setText("退款金额:  " + refundMoney);
            tvRefundMoney.setTextColor(Color.parseColor("#F26304"));

        }else{
            Drawable d = getContext().getResources().getDrawable(R.drawable.person_deal_icon_refund);
            d.setBounds(0,0,d.getMinimumWidth(),d.getMinimumHeight());
            tvMsg.setCompoundDrawables(d,null,null,null);
            tvMsg.setText("未审核");
            tvRefundMoney.setText("退款金额:  " + refundMoney);
            tvRefundMoney.setTextColor(Color.parseColor("#F26304"));
        }

       /* if(TextUtils.isEmpty(bean.getAuditRemark())){
           tvMsg.setText("申请退款处理中");
        }else{
            tvMsg.setText(bean.getAuditRemark());
        }

        //未审核
        if(bean.getAudit() == 0){
            Drawable d = getContext().getResources().getDrawable(R.drawable.person_deal_icon_refund);
            d.setBounds(0,0,d.getMinimumWidth(),d.getMinimumHeight());
            tvMsg.setCompoundDrawables(d,null,null,null);
            tvRefundMoney.setText("未审核");
            tvRefundMoney.setTextColor(Color.parseColor("#9F9F9F"));
        }

        //审核通过
        if(bean.getAudit() == 1){

            //退款完成
            if(bean.getIsFinish() == 1){
                Drawable d = getContext().getResources().getDrawable(R.drawable.person_deal_icon_refund_red);
                d.setBounds(0,0,d.getMinimumWidth(),d.getMinimumHeight());
                tvMsg.setCompoundDrawables(d,null,null,null);
                tvRefundMoney.setText("退款金额:  " + moeny);
                tvRefundMoney.setTextColor(Color.parseColor("#F26304"));

            }else{
                Drawable d = getContext().getResources().getDrawable(R.drawable.person_deal_icon_refund);
                d.setBounds(0,0,d.getMinimumWidth(),d.getMinimumHeight());
                tvMsg.setCompoundDrawables(d,null,null,null);
                tvRefundMoney.setText("退款处理中");
                tvRefundMoney.setTextColor(Color.parseColor("#9F9F9F"));
            }

        }

        //审核不通过
        if(bean.getAudit() == 2){
            Drawable d = getContext().getResources().getDrawable(R.drawable.person_deal_icon_refund);
            d.setBounds(0,0,d.getMinimumWidth(),d.getMinimumHeight());
            tvMsg.setCompoundDrawables(d,null,null,null);
            tvRefundMoney.setText("审核不通过");
            tvRefundMoney.setTextColor(Color.parseColor("#9F9F9F"));
        }*/



    }
}
