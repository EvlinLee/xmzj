package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.PreProfitBean;
import com.gxtc.huchuan.ui.live.series.SeriesActivity;
import com.gxtc.huchuan.ui.news.ShareMakeMoneyActivity;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/5/26.
 */

public class PreProfitAdapter extends BaseRecyclerAdapter<PreProfitBean> {

    public PreProfitAdapter(Context context, List<PreProfitBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, PreProfitBean bean) {
        //头像
        ImageView ivHead = (ImageView) holder.getView(R.id.iv_head);
        ImageHelper.loadImage(getContext(), ivHead, bean.getHeadPic(),R.drawable.person_icon_head_logo_62);
        //是否已经结算
        TextView tvIsSett = holder.getViewV2(R.id.tv_isSett);
        tvIsSett.setText("0".equals(bean.getIsSett())? "待结算" : "已结算");
        //时间
        TextView tvCreateTime = holder.getViewV2(R.id.tv_create_time);
        tvCreateTime.setText(DateUtil.stampToDate(bean.getCreateTime()));
        //自媒体名称
        TextView tvName = holder.getViewV2(R.id.tv_name);
        tvName.setText(bean.getName());
        //标题
        TextView tvTitle = holder.getViewV2(R.id.tv_title);
        tvTitle.setText(bean.getTitle());
        //佣金
        TextView tvCommission = holder.getViewV2(R.id.tv_commission);
        if ("0.00".equals(bean.getCommission())){
            tvCommission.setText("免费");
        }else {
            tvCommission.setText(bean.getCommission());
        }
        
        String type = bean.getType();
        if ("chatInfo".equals(type)){
            //课程
            Drawable drawable = getContext().getResources().getDrawable(R.drawable.person_date_distribution_icon_course);
            drawable.setBounds(0, 0, drawable.getBounds().width(), drawable.getBounds().height());
            tvTitle.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);

        } else if ("group".equals(bean.getType())) {
            //  圈子
            Drawable drawable = getContext().getResources().getDrawable(R.drawable.person_live_distribution_icon_circle);
            drawable.setBounds(0, 0, drawable.getBounds().width(), drawable.getBounds().height());
            tvTitle.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);

        } else if("chatSeries".equals(bean.getType())){
            //系列课
            Drawable drawable = getContext().getResources().getDrawable(R.drawable.person_date_distribution_icon_course);
            drawable.setBounds(0, 0, drawable.getBounds().width(), drawable.getBounds().height());
            tvTitle.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
        }

    }
}
