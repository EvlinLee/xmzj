package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.PurchaseSeriesRecordBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/4/25.
 */

public class SeriesRecordAdapter extends BaseRecyclerAdapter<PurchaseSeriesRecordBean>{

    public SeriesRecordAdapter(Context context, List<PurchaseSeriesRecordBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, PurchaseSeriesRecordBean bean) {
        ImageView ivHead = (ImageView) holder.getView(R.id.iv_head);
        TextView tvTitle = (TextView) holder.getView(R.id.tv_title);
        TextView tvOrderNo = (TextView) holder.getView(R.id.tv_order_no);
        TextView tvPrice = (TextView) holder.getView(R.id.tv_price);
        TextView tvTime = (TextView) holder.getView(R.id.tv_time);

        ImageHelper.loadCircle(getContext(),ivHead,bean.getHeadPic(),R.mipmap.ic_launcher);
        tvTitle.setText(bean.getChatSeriesName());
        tvOrderNo.setText(bean.getOrderId());
        tvPrice.setText("实付："+bean.getFee()+"元");
        tvTime.setText("购买时间："+ DateUtil.stampToDate(bean.getCreatetime()));
    }
}
