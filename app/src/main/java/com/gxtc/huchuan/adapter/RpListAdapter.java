package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.RedPacketBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/6.
 */

public class RpListAdapter extends BaseRecyclerAdapter<RedPacketBean> {

    private int type;

    public RpListAdapter(Context context, List<RedPacketBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, RedPacketBean bean) {
        ImageView imgHead = (ImageView) holder.getView(R.id.img_head);
        TextView  tvName = (TextView) holder.getView(R.id.tv_name);
        TextView  tvTime = (TextView) holder.getView(R.id.tv_time);
        TextView tvMoney = (TextView) holder.getView(R.id.tv_money);
        TextView tvBest = (TextView) holder.getView(R.id.tv_best);

        String url = bean.getUserPic();
        ImageHelper.loadRound(getContext(),imgHead,url,2);

        tvName.setText(bean.getUserName());
        String money = new BigDecimal(bean.getRewardAmt()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() + "元";
        tvMoney.setText(money);

        String time = DateUtil.showTimeAgo(bean.getCreateTime() + "");
        tvTime.setText(time);

        if(bean.getIsMax() == 1 && type == 0){
            tvBest.setVisibility(View.VISIBLE);
        }else{
            tvBest.setVisibility(View.GONE);
        }
    }

    public void setType(int type) {
        this.type = type;
    }
}
