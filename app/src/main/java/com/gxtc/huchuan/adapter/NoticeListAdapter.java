package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/24.
 */

public class NoticeListAdapter extends BaseRecyclerAdapter<CircleBean> {


    public NoticeListAdapter(Context context, List<CircleBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, CircleBean bean) {
        TextView tvTitle = (TextView) holder.getView(R.id.tv_title);
        TextView tvName = (TextView) holder.getView(R.id.tv_name);

        tvTitle.setText(bean.getTitle());

        String name = bean.getUserName();
        String time = DateUtil.showTimeAgo(bean.getCreateTime()+"");

        String temp = name + "   " + time;
        tvName.setText(temp);
    }
}
