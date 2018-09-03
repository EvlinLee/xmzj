package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseRecyclerTypeAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.utils.ClipboardUtil;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.umeng.socialize.utils.DeviceConfig.context;

/**
 * Describe:
 * Created by ALing on 2017/5/22 .
 */

public class DealRecordAdapter extends BaseRecyclerAdapter<PurchaseListBean> {
    private SimpleDateFormat sdf;

    public DealRecordAdapter(Context mContext, List<PurchaseListBean> mDatas, int resId) {
        super(mContext, mDatas, resId);
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    }

    @Override
    public void bindData(ViewHolder holder, int position, final PurchaseListBean bean) {
        TextView name = (TextView) holder.getView(R.id.tv_name);
        name.setText("交易");
        TextView status = (TextView) holder.getView(R.id.tv_status);
        ImageView imghead = holder.getImageView(R.id.img_head);
        TextView price = (TextView) holder.getView(R.id.price);
        TextView number = (TextView) holder.getView(R.id.tv_time);
        TextView sum = (TextView) holder.getView(R.id.tv_money);
        TextView orderno = (TextView) holder.getView(R.id.tv_order_no);
        TextView goodsName = (TextView) holder.getView(R.id.tv_goods_name);
        TextView Subtitle = (TextView) holder.getView(R.id.tv_type);

        String s = "";
        switch (bean.getIsRefund()){
            case "0" :
                switch (bean.getIsFinish()){
                    case 0:
                        s = "等待同意";
                        break;

                    case 1:
                        s = "同意交易";
                        break;

                    case 2:
                        s = "已支付";
                        break;

                    case 3:
                        s = "已交付";
                        break;

                    case 4:
                        s = "交易完成";
                        break;

                    case 5:
                        s = "已关闭";
                        break;

                    case 10:
                        s = "拒绝交易";
                        break;
                }

                break;

            case "1" :
                s = "退款中";
                break;

            case "2" :
                s = "已退款";
                break;

            case "3" :
                s = "拒绝退款";
                break;
        }

        String orderType = bean.getBuyer() == 0 ? "购买" : "出售";
        Subtitle.setText("订单类型：" + orderType);
        orderno.setText("订单号：" + bean.getOrderId());
        status.setText(s);
//        Subtitle.setText( bean.get);
//        ImageHelper.loadCircle(context, imghead, bean.getTradeInfoPic());
        ImageHelper.loadRound(context,imghead,bean.getTradeInfoPic(), 5);
        goodsName.setText(bean.getTradeInfoTitle());
        String time = DateUtil.stampToDate(bean.getCreateTime()+"");
        number.setText(time.substring(0, time.length() - 3));

        sum.setText("金额：" + StringUtil.formatMoney(2 , bean.getTradeAmt()));
        price.setText(StringUtil.formatMoney(2 , bean.getTradeAmt()) );
        orderno.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardUtil.copyText(bean.getOrderId());
                return false;
            }
        });
    }

    private SpannableString setLocalTextColor(String text1, String text2){

        SpannableString ss2 = new SpannableString(text1 + text2);
        TextAppearanceSpan dealSpan2 = new TextAppearanceSpan(context, R.style.circle_invite_text_color);
        ss2.setSpan(dealSpan2, text1.length(), text1.length()+text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss2;
    }

//    @Override
//    protected int getViewType(int position) {
//        if(TextUtils.isEmpty(getDatas().get(position).getTradeInfoPic())){
//            return 0;
//        }else{
//            return 1;
//        }
//    }
//
//    @Override
//    protected void bindData(RecyclerView.ViewHolder h, int position, int type, PurchaseListBean bean) {
//        ViewHolder holder = (ViewHolder) h;
//        TextView tvTime = (TextView) holder.getView(R.id.tv_time);
//        TextView tvName = (TextView) holder.getView(R.id.tv_name);
//        TextView tvMoney = (TextView) holder.getView(R.id.tv_money);
//        TextView tvNum = (TextView) holder.getView(R.id.tv_number);
//        TextView tvStatus = (TextView) holder.getView(R.id.tv_status);
////        final TextView tvPay = (TextView) holder.getView(R.id.btn_pay);
//
//        String time = sdf.format(new Date(bean.getCreateTime()));
//        String name = bean.getTradeInfoTitle();
//        String money = "¥" + bean.getTradeAmt();
//        String num = bean.getBuyer() == 0 ? "购买":"出售";
//
//        /*tvPay.setTag(bean);
//        tvPay.setTextColor(getmContext().getResources().getColor(R.color.colorAccent));
//        tvPay.setBackground(getmContext().getResources().getDrawable(R.drawable.shape_border_raido_accent));
//        tvPay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PurchaseListBean bean = (PurchaseListBean) v.getTag();
//                EventBusUtil.post(new EventClickBean(tvPay.getText().toString(),bean));
//            }
//        });*/
//
//        int buyer = bean.getBuyer();   //是否是买家。0：买家；1：卖家
//        String status = "";            //0:卖家未同意，1：卖家同意交易，2：买家支付，3：卖家交付，4：交易完成，   //5:超过七天，交易关闭 ，自动退款，10：卖家不同意交易
//        if("0".equals(bean.getIsRefund())){   //0：正常，1：退款中，2：已退款
//            if(bean.getIsFinish() == 0){
//                status = "等待卖家同意";
//                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.pay_finish));
//            }
//            if(bean.getIsFinish() == 1){
//                status = "卖家同意交易";
//                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.colorAccent));
//            }
//            if(bean.getIsFinish() == 2){
//                status = "买家已支付";
//                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.colorAccent));
//            }
//            if(bean.getIsFinish() == 3){
//                status = "卖家已交付";
//                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.colorAccent));
//            }
//            if(bean.getIsFinish() == 4){
//                status = "交易完成";
//                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.color_7ecef4));
//            }
//            if(bean.getIsFinish() == 5){
//                status = "交易超时已关闭";
//                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.subscribe_item_selected_stroke));
//            }
//            if(bean.getIsFinish() == 10){
//                status = "卖家拒绝交易";
//                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.subscribe_item_selected_stroke));
//            }
//        }else {
//            if("1".equals(bean.getIsRefund())){
//                status = "退款中";
//                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.colorAccent));
//            }
//            if("2".equals(bean.getIsRefund())){
//                status = "已退款";
//                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.pay_finish));
//            }
//            if("3".equals(bean.getIsRefund())){
//                status = "卖家拒绝退款";
//                tvStatus.setTextColor(getmContext().getResources().getColor(R.color.subscribe_item_selected_stroke));
//            }
//        }
//
//
//        tvTime.setText(time);
//
//        //不带图片的样式
//        if(type == 0){
//            tvName.setText(name);
//            tvMoney.setText(money);
//            tvNum.setText(num);
//            tvStatus.setText(status);
//
//            //带图片的样式
//        }else{
//            ImageView img = (ImageView) holder.getView(R.id.img_code);
//            ImageHelper.loadImage(getmContext(),img,bean.getTradeInfoPic());
//
//            tvName.setText("商品名称:  " + name);
//            tvMoney.setText("订单金额:  " + money);
//            tvNum.setText("订单类型:  " + num);
//            tvStatus.setText(status);
//        }

//      }

}
