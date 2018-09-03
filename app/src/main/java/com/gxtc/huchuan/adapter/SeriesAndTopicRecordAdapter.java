package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.PurchaseSeriesAndTopicBean;
import com.gxtc.huchuan.utils.DateUtil;
import com.gxtc.huchuan.utils.StringUtil;

import java.util.List;

/**
 * Describe:课程和系列课合并
 * Created by ALing on 2017/4/25.
 */

public class SeriesAndTopicRecordAdapter extends BaseRecyclerAdapter<PurchaseSeriesAndTopicBean>{
    public SeriesAndTopicRecordAdapter(Context context, List<PurchaseSeriesAndTopicBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, PurchaseSeriesAndTopicBean bean) {
//        ImageView ivHead = (ImageView) holder.getView(R.id.iv_head);
//        TextView tvTitle = (TextView) holder.getView(R.id.tv_title);
//        TextView tvOrderNo = (TextView) holder.getView(R.id.tv_order_no);
//        TextView tvPrice = (TextView) holder.getView(R.id.tv_price);
//        TextView tvTime = (TextView) holder.getView(R.id.tv_time);
//
//        ImageHelper.loadImage(getContext(),ivHead,bean.getFacePic(),R.drawable.circle_head_icon_120);
//        if (!TextUtils.isEmpty(bean.getTitle())){
//            tvTitle.setText(bean.getTitle());
//        }
//
//        tvOrderNo.setText(bean.getChatCreateMan());
//        tvPrice.setText(bean.getFee()+"元");
//        tvTime.setText("购买时间："+DateUtil.stampToDate(bean.getCreatetime()));

        TextView name = (TextView) holder.getView(R.id.tv_name);
        name.setText("课程");
        TextView status = (TextView) holder.getView(R.id.tv_status);
        ImageView imghead = holder.getImageView(R.id.img_head);
        TextView price = (TextView) holder.getView(R.id.price);
        TextView number = (TextView) holder.getView(R.id.tv_time);
        TextView tvSubtitle = (TextView) holder.getView(R.id.tv_type);
        if(bean.getIsPay().equals("1")){
            status.setText( "已开始");
        }else{
            status.setText("未开始");
        }
        tvSubtitle.setText(bean.getTitle());
        TextView goodsName = (TextView) holder.getView(R.id.tv_goods_name);
//        ImageHelper.loadCircle(context, imghead, bean.getFacePic());
        ImageHelper.loadRound(context,imghead,bean.getFacePic(), 5);
        goodsName.setText(bean.getChatRoomName());
        String time = DateUtil.stampToDate(bean.getCreatetime());
        number.setText( time.substring(0, time.length() - 3));
        TextView sum = (TextView) holder.getView(R.id.tv_money);
        sum.setText("金额：" + StringUtil.formatMoney(2 , bean.getFee()));
        price.setText("金额：" + StringUtil.formatMoney(2 , bean.getFee()) );
    }

}
