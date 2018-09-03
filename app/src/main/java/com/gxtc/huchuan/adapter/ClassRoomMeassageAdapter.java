package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ClassMyMessageBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * Describe:我的 > 课堂  >  我的消息 适配器
 * Created by ALing on 2017/3/10.
 */

public class ClassRoomMeassageAdapter extends BaseRecyclerAdapter<ClassMyMessageBean> {

    public ClassRoomMeassageAdapter(Context context, List<ClassMyMessageBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, ClassMyMessageBean bean) {
        TextView tvContent = (TextView) holder.getView(R.id.tv_content);
        TextView tvCreateTime = (TextView) holder.getView(R.id.tv_create_time);

        tvContent.setText(bean.getContent());
        tvCreateTime.setText(DateUtil.showTimeAgo(bean.getCreateTime()));
    }
}
