package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerTypeAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.PurchaseListBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class PurchaseListAdapter extends BaseRecyclerTypeAdapter<PurchaseListBean> {

    private SimpleDateFormat sdf ;

    public PurchaseListAdapter(Context mContext, List<PurchaseListBean> mDatas, int[] resId) {
        super(mContext, mDatas, resId);
        sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
    }

    @Override
    protected int getViewType(int position) {
        if(TextUtils.isEmpty(getDatas().get(position).getTradeInfoPic())){
            return 0;
        }else{
            return 1;
        }
    }

    @Override
    protected void bindData(RecyclerView.ViewHolder h, int position, int type, PurchaseListBean bean) {
        ViewHolder holder = (ViewHolder) h;
        TextView tvTime = (TextView) holder.getView(R.id.tv_time);
        TextView tvName = (TextView) holder.getView(R.id.tv_name);
        TextView tvMoney = (TextView) holder.getView(R.id.tv_money);
        TextView tvNum = (TextView) holder.getView(R.id.tv_number);
        TextView tvStatus = (TextView) holder.getView(R.id.tv_status);
//        final TextView tvPay = (TextView) holder.getView(R.id.btn_pay);

        String time = sdf.format(new Date(bean.getCreateTime()));
        String name = bean.getTradeInfoTitle();
        String money = "¥" + bean.getTradeAmt();
        String num = bean.getBuyer() == 0 ? "购买":"出售";

        /*tvPay.setTag(bean);
        tvPay.setTextColor(getmContext().getResources().getColor(R.color.colorAccent));
        tvPay.setBackground(getmContext().getResources().getDrawable(R.drawable.shape_border_raido_accent));
        tvPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PurchaseListBean bean = (PurchaseListBean) v.getTag();
                EventBusUtil.post(new EventClickBean(tvPay.getText().toString(),bean));
            }
        });*/

        int buyer = bean.getBuyer();   //是否是买家。0：买家；1：卖家
        String status = "";            //0:卖家未同意，1：卖家同意交易，2：买家支付，3：卖家交付，4：交易完成，10：卖家不同意交易
        if("0".equals(bean.getIsRefund())){    //0：正常，1：退款中，2：已退款
            if(bean.getIsFinish() == 0){
                status = "等待卖家同意";
                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.pay_finish));
            }
            if(bean.getIsFinish() == 1){
                status = "卖家同意交易";
                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.colorAccent));
            }
            if(bean.getIsFinish() == 2){
                status = "买家已支付";
                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.colorAccent));
            }
            if(bean.getIsFinish() == 3){
                status = "卖家已交付";
                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.colorAccent));
            }
            if(bean.getIsFinish() == 4){
                status = "交易完成";
                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.color_7ecef4));
            }
            if(bean.getIsFinish() == 5){
                status = "交易已取消";
                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.color_7ecef4));
            }
            if(bean.getIsFinish() == 10){
                status = "卖家拒绝交易";
                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.subscribe_item_selected_stroke));
            }

        }else {
            if("1".equals(bean.getIsRefund())){
                status = "退款中";
                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.colorAccent));
            }
            if("2".equals(bean.getIsRefund())){
                status = "已退款";
                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.pay_finish));
            }
            if("3".equals(bean.getIsRefund())){
                status = "卖家拒绝退款";
                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.subscribe_item_selected_stroke));
            }
        }

        tvTime.setText(time);

        //不带图片的样式
        if(type == 0){
            tvName.setText(name);
            tvMoney.setText(money);
            tvNum.setText(num);
            tvStatus.setText(status);

        //带图片的样式
        }else{
            ImageView img = (ImageView) holder.getView(R.id.img_code);
            ImageHelper.loadImage(getmContext(),img,bean.getTradeInfoPic());

            tvName.setText("商品名称:  " + name);
            tvMoney.setText("订单金额:  " + money);
            tvNum.setText("订单类型:  " + num);
            tvStatus.setText(status);
        }
    }
}
