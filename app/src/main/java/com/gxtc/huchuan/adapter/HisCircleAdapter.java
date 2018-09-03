package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleBean;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/5/2 .
 */

public class HisCircleAdapter extends BaseRecyclerAdapter<CircleBean> {
    public HisCircleAdapter(Context context, List<CircleBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, CircleBean bean) {
        ImageView ivCircleCover = (ImageView) holder.getView(R.id.iv_circle_cover);
        TextView tvCirclleName = (TextView) holder.getView(R.id.tv_circle_name);
        TextView tvJoinCount = (TextView) holder.getView(R.id.tv_joinCount);
        TextView tvTopicCount = (TextView) holder.getView(R.id.tv_topicCount);

        ImageHelper.loadImage(getContext(),ivCircleCover,bean.getCover(),R.mipmap.ic_launcher);
        tvCirclleName.setText(bean.getGroupName());
        tvJoinCount.setText("成员"+bean.getAttention());
        tvTopicCount.setText("动态"+bean.getInfoNum());
    }
}
