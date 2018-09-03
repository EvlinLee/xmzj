package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleRankBean;
import com.gxtc.huchuan.bean.DealListBean;
import com.gxtc.huchuan.widget.RoundImageView;

import java.util.List;

/**
 * Created by Administrator on 2018/3/27.
 */

public class CircleInviteRankAdpter extends BaseRecyclerAdapter<CircleRankBean> {

    private Context context;
    public CircleInviteRankAdpter(Context context, List<CircleRankBean> datas, int itemLayoutId) {
        super(context, datas, itemLayoutId);
        this.context=context;
    }

    @Override
    public void bindData(ViewHolder holder, int position, CircleRankBean circleRankBean) {
        TextView rank= (TextView) holder.getView(R.id.tv_rank);
        ImageView logo = (ImageView) holder.getView(R.id.riv_logo);
        TextView nickname= (TextView) holder.getView(R.id.tv_nickname);
        TextView invitecount= (TextView) holder.getView(R.id.tv_invite_count);
        TextView commission= (TextView) holder.getView(R.id.tv_invite_commission);
        rank.setText((position+1)+".");
        ImageHelper.loadCircle(context, logo,circleRankBean.getHeadPic(), R.drawable.person_icon_head_120);
        nickname.setText(circleRankBean.getName());
        invitecount.setText("邀请："+circleRankBean.getCount()+"人");
        commission.setText("佣金：￥"+circleRankBean.getSaleFee());



    }
}
