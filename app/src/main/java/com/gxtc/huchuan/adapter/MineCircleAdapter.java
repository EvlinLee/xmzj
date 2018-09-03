package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.MineCircleBean;

import java.util.List;


/**
 * Created by 宋家任 on 2017/4/26.
 * 我的圈子适配器
 */

public class MineCircleAdapter extends BaseRecyclerAdapter<MineCircleBean> {
    private Context mContext;
    private List<MineCircleBean> mDatas;
    private String type;

    public MineCircleAdapter(Context context, List<MineCircleBean> list, int itemLayoutId, String type) {
        super(context, list, itemLayoutId);
        this.mContext = context;
        this.mDatas = list;
        this.type = type;
    }


    @Override
    public void bindData(ViewHolder holder, int position, MineCircleBean mineCircleBean) {
        ImageView ivCover = (ImageView) holder.getView(R.id.iv_item_mine_circle);
        ImageHelper.loadImage(mContext, ivCover, mineCircleBean.getCover());

        ImageView ivStatus = (ImageView) holder.getView(R.id.iv_item_mine_circle_status);
        if (0 == mineCircleBean.getIsShow()) {//未审核
            ivStatus.setImageDrawable(getContext().getResources().getDrawable(R.drawable.circle_topic_icon_pass));
        } else if (2 == mineCircleBean.getIsShow()) {//2、审核不通
            ivStatus.setImageDrawable(getContext().getResources().getDrawable(R.drawable.circle_topic_icon_no_pass));
        }

        TextView tvName = (TextView) holder.getView(R.id.tv_item_mine_circle);
        tvName.setText(mineCircleBean.getGroupName());

        TextView tvCreate = (TextView) holder.getView(R.id.tv_item_mine_create);
        tvCreate.setVisibility(View.GONE);
    }
}
