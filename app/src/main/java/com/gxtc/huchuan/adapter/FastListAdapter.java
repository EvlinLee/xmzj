package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.PurchaseListBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class FastListAdapter extends BaseRecyclerAdapter<PurchaseListBean> {

    private SimpleDateFormat sdf ;

    public FastListAdapter(Context context, List<PurchaseListBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        sdf = new SimpleDateFormat("MM-dd",Locale.CHINA);
    }

    @Override
    public void bindData(ViewHolder holder, int position, PurchaseListBean bean) {
        TextView tvTitle = (TextView) holder.getView(R.id.tv_title);
        TextView tvTime = (TextView) holder.getView(R.id.tv_time);
        String name = bean.getTradeInfoTitle();
        tvTitle.setText(name);
        String time = DateUtil.showTimeAgo(bean.getCreateTime() + "");
        tvTime.setText(time);

    }
}
