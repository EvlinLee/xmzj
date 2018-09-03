package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.FreezeAccountBean;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.StringUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by sjr on 2017/3/22.
 * 冻结金额适配器
 */

public class FreezeAccountAdapter extends BaseRecyclerAdapter<FreezeAccountBean> {

    public FreezeAccountAdapter(Context context, List<FreezeAccountBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, FreezeAccountBean freezeAccountBean) {

        String type = freezeAccountBean.getOrderType();
        FreezeAccountBean.OrderInfoBean infoBean = freezeAccountBean.getOrderInfo();

        if ("chatSignup".equals(type)) {
            holder.setText(R.id.result_status,infoBean.getIsSett().equals("0") ? "未结算":"已结算");
            holder.setText(R.id.tv_freeze_account_title, "课程订单")
                    .setText(R.id.tv_freeze_account_content, infoBean.getChatInfoTitle())
                    .setText(R.id.tv_freeze_account_time, DateUtil.stampToDate(infoBean.getCreatetime()))
                    .setText(R.id.tv_freeze_account_money, calculate(infoBean.getFee(),infoBean.getSaleFee()) +"元");

        } else if ("chatSeries".equals(type)) {
            holder.setText(R.id.result_status,infoBean.getIsSett().equals("0") ? "未结算":"已结算");
            holder.setText(R.id.tv_freeze_account_title, "系列课订单")
                    .setText(R.id.tv_freeze_account_content, infoBean.getChatSeriesName())
                    .setText(R.id.tv_freeze_account_time, DateUtil.stampToDate(infoBean.getCreatetime()))
                    .setText(R.id.tv_freeze_account_money, infoBean.getFee() +"元");

        } else if ("tradeOrder".equals(type)) {
            TextView status= (TextView) holder.getView(R.id.result_status);
            if(infoBean.getIsRefund().equals("0")){
                status.setText("正常");
            }else if(infoBean.getIsRefund().equals("1")){
                status.setText("退款中");
            }else if(infoBean.getIsRefund().equals("2")){
                status.setText("已退款");
            }
            holder.setText(R.id.tv_freeze_account_title, "交易订单")
                    .setText(R.id.tv_freeze_account_content, infoBean.getTradeInfoTitle())
                    .setText(R.id.tv_freeze_account_time, DateUtil.stampToDate(String.valueOf(infoBean.getCreateTime())))
                    .setText(R.id.tv_freeze_account_money, StringUtil.formatMoney(2,infoBean.getTradeAmt()) +"元");

        } else if ("groupJoin".equals(type)) {
            holder.setText(R.id.result_status,infoBean.getIsSett().equals("0") ? "未结算":"已结算");
            holder.setText(R.id.tv_freeze_account_title, "圈子订单")
                    .setText(R.id.tv_freeze_account_content, infoBean.getGroupName())
                    .setText(R.id.tv_freeze_account_time, DateUtil.stampToDate(infoBean.getCreatetime()))
                    .setText(R.id.tv_freeze_account_money, calculate(infoBean.getFee(),infoBean.getSaleFee()) +"元");

        } else if ("invite".equals(type)){
            holder.setText(R.id.result_status,infoBean.getIsSett().equals("0") ? "未结算":"已结算");
            holder.setText(R.id.tv_freeze_account_title, "分享佣金")
                  .setText(R.id.tv_freeze_account_content, infoBean.getTitle())
                  .setText(R.id.tv_freeze_account_time, DateUtil.stampToDate(String.valueOf(infoBean.getCreateTime())))
                  .setText(R.id.tv_freeze_account_money, infoBean.getCommission() +"元");
        }
    }

    /**
     * @param total 原价
     * @param saleFee 分销费用
     */
    private double calculate(String total, String saleFee){
        try {
            double totalD = Double.valueOf(total);
            double saleFeeD = Double.valueOf(saleFee);
            return new BigDecimal(totalD).subtract(new BigDecimal(saleFeeD)).setScale(3,BigDecimal.ROUND_HALF_UP).doubleValue();
        } catch (Exception e){
            return 0;
        }
    }

}
