package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleManagerBean;

import java.util.List;

/**
 * Created by sjr on 2017/5/2.
 * 圈子管理适配器
 */

public class CircleManagerAdapter extends BaseRecyclerAdapter<CircleManagerBean> {

    public CircleManagerAdapter(Context context, List<CircleManagerBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, CircleManagerBean circleManagerBean) {
        TextView textView = (TextView) holder.getView(R.id.tv_item_circle_manager);
        textView.setText(circleManagerBean.getName());

        ImageView imageView = (ImageView) holder.getView(R.id.iv_item_circle_manager);
        imageView.setImageResource(circleManagerBean.getResid());
    }
}
