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

public class ManagetMentCircleAdapter extends BaseRecyclerAdapter<MineCircleBean> {
    private Context mContext;
    private List<MineCircleBean> mDatas;
    private String type;

    public ManagetMentCircleAdapter(Context context, List<MineCircleBean> list, int itemLayoutId, String type) {
        super(context, list, itemLayoutId);
        this.mContext = context;
        this.mDatas = list;
        this.type = type;
    }


    @Override
    public void bindData(ViewHolder holder, int position, MineCircleBean mineCircleBean) {
        if (position != mDatas.size() - 1) {
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
        } else {
            LinearLayout ll = (LinearLayout) holder.getView(R.id.ll_item_mine_circle);
            ll.setVisibility(View.GONE);

            TextView tvCreate = (TextView) holder.getView(R.id.tv_item_mine_create);
            tvCreate.setVisibility(View.VISIBLE);
            if ("2".equals(type)) {
                tvCreate.setText("加入圈子");
            }else if ("3".equals(type)) {
                tvCreate.setVisibility(View.GONE);
                ll.setVisibility(View.VISIBLE);

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
            }
        }
    }
}
