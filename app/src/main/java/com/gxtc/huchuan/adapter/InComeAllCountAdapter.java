package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.AccountWaterBean;
import com.gxtc.huchuan.bean.InComeAllCountBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/21.
 */

public class InComeAllCountAdapter extends BaseRecyclerAdapter<AccountWaterBean> {

    public InComeAllCountAdapter(Context context, List<AccountWaterBean> list,
            int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, AccountWaterBean bean) {
        ImageView ivType = (ImageView) holder.getView(R.id.iv_cover);
        String    type = bean.getType();
        if ("0".equals(type) || "2".equals(type) || "4".equals(type)){
            //交易
            ivType.setImageResource(R.drawable.person_income_detail_icon_deal);
            holder.setText(R.id.tv_income_type, "交易");
        }else if ("1".equals(type)){
            //课程
            ivType.setImageResource(R.drawable.person_income_detail_icon_live);
            holder.setText(R.id.tv_income_type, "课程");
        }
        else if ("3".equals(type)){
            //圈子
            ivType.setImageResource(R.drawable.person_income_detail_icon_circle);
            holder.setText(R.id.tv_income_type, "圈子");
        }  else if ("6".equals(type)){
            //系列课
            ivType.setImageResource(R.drawable.person_income_detail_icon_xilieke);
            holder.setText(R.id.tv_income_type, "系列课");
        }else if ("8".equals(type)){
            //打赏
            ivType.setImageResource(R.drawable.person_income_detail_icon_dashang);
            holder.setText(R.id.tv_income_type, "打赏");
        }else if ("66".equals(type)){
            //分销(分销包括 话题 系列课 圈子)
            ivType.setImageResource(R.drawable.person_income_detail_icon_fenxiao);
            holder.setText(R.id.tv_income_type, "分销");
        }


        //时间
        holder.setText(R.id.tv_income_time, DateUtil.stampToDate(bean.getCreateTime()));

        //金额
        TextView tvMoney      = (TextView) holder.getView(R.id.tv_income_money);
        String streamMoney = bean.getStreamMoney();
        if ("0.0".equals(streamMoney)){
            tvMoney.setText("免费");
        }else {
            tvMoney.setText(streamMoney);
        }
    }
}
