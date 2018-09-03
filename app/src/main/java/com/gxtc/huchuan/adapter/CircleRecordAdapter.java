package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.PurchaseCircleRecordBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/22 .
 */

public class CircleRecordAdapter extends BaseRecyclerAdapter<PurchaseCircleRecordBean> {

    public CircleRecordAdapter(Context context, List<PurchaseCircleRecordBean> list,
            int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position,
            PurchaseCircleRecordBean bean) {
        ImageView      ivHead    = (ImageView) holder.getView(R.id.img_head);
        TextView       tvStatus   = (TextView) holder.getView(R.id.tv_status);
        TextView       tvTitle   = (TextView) holder.getView(R.id.tv_name);
        TextView       tvOrderNo = (TextView) holder.getView(R.id.tv_order_no);
        TextView       tvGoodsName   = (TextView) holder.getView(R.id.tv_goods_name);
        TextView       tvPrice   = (TextView) holder.getView(R.id.price);
        TextView       tvMoney   = (TextView) holder.getView(R.id.tv_money);
        TextView       tvTime    = (TextView) holder.getView(R.id.tv_time);
        TextView       tvSubtitle    = (TextView) holder.getView(R.id.tv_type);


//        ImageHelper.loadImage(getContext(),ivHead,bean.getGroupCover(),R.mipmap.ic_launcher);
        ImageHelper.loadRound(context,ivHead,bean.getGroupCover(), 5);
        if (!TextUtils.isEmpty(bean.getGroupName())){
            tvGoodsName.setText(bean.getGroupName());
        }
        tvTitle.setText("圈子");
//        tvOrderNo.setVisibility(View.GONE);
        String status = "";
        switch (bean.getIsPay()){
            case "0":
                status = "未支付";
                break;
            case "1":
                status = "已支付";
                break;

        }
        tvSubtitle.setText(bean.getSellerName());
        tvStatus.setText(status);
        tvMoney.setText("金额：" + bean.getFee());
        tvOrderNo.setText("订单号："+bean.getOrderId());
        tvPrice.setText(bean.getFee()+"元");
        String time = DateUtil.stampToDate(String.valueOf(bean.getSignuptime()));
        tvTime.setText(time.substring(0, time.length() - 3));
    }
}
