package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleBean;


import java.util.List;

public class SearchCircleAdapter extends BaseRecyclerAdapter<CircleBean> {

    private View.OnClickListener mClickListener;

    public SearchCircleAdapter(Context context, List<CircleBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, CircleBean bean) {
        ImageView img      = (ImageView) holder.getView(R.id.img_icon);
        TextView  tvName   = (TextView) holder.getView(R.id.tv_name);
        TextView  tvTitle  = (TextView) holder.getView(R.id.tv_title);
        TextView  tvBottom = (TextView) holder.getView(R.id.tv_bottom);
        TextView  tvId     = (TextView) holder.getView(R.id.tv_id);
        tvId.setText("ID:" + bean.getGroupCode());
        tvName.setText(bean.getGroupName());
        tvTitle.setText(bean.getContent());
        ImageHelper.loadRound(getContext(), img, bean.getCover(), 2);


        String temp = "成员" + bean.getAttention() + "  " + "动态" + bean.getInfoNum();
        tvBottom.setText(temp);
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        mClickListener = clickListener;
    }
}
