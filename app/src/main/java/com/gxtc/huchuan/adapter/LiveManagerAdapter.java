package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.LiveManagerBean;

import java.util.List;

/**
 * Describe:获取直播间管理员 > 列表
 * Created by ALing on 2017/4/13 .
 */

public class LiveManagerAdapter extends BaseRecyclerAdapter<LiveManagerBean.DatasBean>{

    public LiveManagerAdapter(Context context, List<LiveManagerBean.DatasBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, LiveManagerBean.DatasBean liveManagerBean) {
        ImageHelper.loadImage(holder.getItemView().getContext(),
                holder.getImageView(R.id.iv_head),
                liveManagerBean.getHeadPic(),
                R.drawable.person_icon_head_120);

        holder.setText(R.id.tv_name,liveManagerBean.getName());

        String joinType = liveManagerBean.getJoinType();
        TextView tv_sub_name = (TextView) holder.getView(R.id.tv_sub_name);
        if ("0".equals(joinType)){
            tv_sub_name.setText("创建者");
        }else if ("1".equals(joinType)){
            tv_sub_name.setText("管理员");
        }else {
            tv_sub_name.setText("讲师");
        }

    }
}
