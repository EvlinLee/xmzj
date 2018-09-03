package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.AccountWaterBean;
import com.gxtc.huchuan.bean.NewsBean;
import com.gxtc.huchuan.bean.event.EventCollectSelectBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * Created by sjr on 2017/2/17.
 * 账户流水适配器
 */

public class AccountWaterAdapter extends BaseRecyclerAdapter<AccountWaterBean> {
    private Context mContext;


    public AccountWaterAdapter(Context context, List<AccountWaterBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.mContext = context;

    }

    @Override
    public void bindData(final ViewHolder holder, final int position, AccountWaterBean bean) {
        //标题
        holder.setText(R.id.tv_account_water_title, bean.getTitle());

        //时间
        holder.setText(R.id.tv_account_water_time, DateUtil.stampToDate(bean.getCreateTime()));

        //金额
        TextView tvMoney = (TextView) holder.getView(R.id.tv_account_water_money);
        if (bean.getStreamMoney().startsWith("-")) {
            tvMoney.setTextColor(mContext.getResources().getColor(R.color.text_color_333));
            tvMoney.setText("-" + bean.getStreamMoney().replace("-", " "));
        } else {
            tvMoney.setTextColor(mContext.getResources().getColor(R.color.green_btn_nornal));
            tvMoney.setText("+ " + bean.getStreamMoney());
        }
    }

}
