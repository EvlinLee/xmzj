package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.DistributionBean;

import java.util.List;

/**
 * Created by ALing on 2017/5/4 .
 *
 */

public class ProfitAdapter extends BaseRecyclerAdapter<DistributionBean> {
    public View.OnClickListener OnClickClassLayoutListenr;
    public View.OnClickListener OnClickClassBottomLayoutListenr;

    public void setOnClickClassLayoutListenr(View.OnClickListener onClickClassLayoutListenr) {
        OnClickClassLayoutListenr = onClickClassLayoutListenr;
    }

    public void setOnClickClassBottomLayoutListenr(
            View.OnClickListener onClickClassBottomLayoutListenr) {
        OnClickClassBottomLayoutListenr = onClickClassBottomLayoutListenr;
    }

    public ProfitAdapter(Context context, List<DistributionBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, final DistributionBean distributionBean) {
        ImageHelper.loadImage(holder.getItemView().getContext(), holder.getImageView(R.id
                .iv_facePic), distributionBean.getFacePic(), R.mipmap.ic_launcher);

        TextView tv_type = (TextView) holder.getView(R.id.tv_type);
        TextView tvCommission = (TextView) holder.getView(R.id.tv_commission);
        TextView tvPrice = (TextView) holder.getView(R.id.tv_price);

        String   fee     = distributionBean.getFee();
        if ("0.0".equals(fee)){
            tvPrice.setText("免费");
        }else {
            tvPrice.setText("价格："+fee+"元");
        }
        String type = distributionBean.getType();
        switch (type){
            case "1":
                tv_type.setText("课程");
                break;
            case "2":
                tv_type.setText("系列课");
                break;
            case "3":
                tv_type.setText("圈子");
                break;
        }
         holder.getView(R.id.class_layout).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 v.setTag(distributionBean);
                 if(OnClickClassLayoutListenr != null)
                 OnClickClassLayoutListenr.onClick(v);
             }
         });
         holder.getView(R.id.class_bottom_layout).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 v.setTag(distributionBean);
                 if(OnClickClassBottomLayoutListenr != null)
                     OnClickClassBottomLayoutListenr.onClick(v);
             }
         });
        holder.setText(R.id.tv_title,distributionBean.getTitle())
                .setText(R.id.tv_back_profit,"返"+distributionBean.getPent()+"%")
                .setText(R.id.tv_recommend_count, TextUtils.isEmpty(distributionBean.getJoinCount()) ? "0" : distributionBean.getJoinCount())
                .setText(R.id.tv_buy_count,TextUtils.isEmpty(distributionBean.getSumFaleFee()) ? "0" : distributionBean.getSumFaleFee())
                .setText(R.id.tv_commission_count,TextUtils.isEmpty(distributionBean.getSumFee()) ? "0" : distributionBean.getSumFee());

        SpannableStringBuilder s = new SpannableStringBuilder("￥"+distributionBean.getSaleFee());
        ForegroundColorSpan span = new ForegroundColorSpan(getContext().getResources().getColor(R.color.color_price_ec6b46));
        s.setSpan(span,0,s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvCommission.setText("佣金："+s);

    }

    private void setDrawableLeft(TextView textView,int drawableId){
        Drawable drawable = getContext().getResources().getDrawable(drawableId);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        textView.setCompoundDrawables(drawable,null,null,null);
    }
}
