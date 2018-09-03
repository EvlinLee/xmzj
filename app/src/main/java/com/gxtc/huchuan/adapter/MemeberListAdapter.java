package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;

import com.gxtc.commlibrary.base.AbTypeBaseAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.MemberBean;

import java.util.List;

/**
 * Created by Gubr on 2017/3/17.
 */

public class MemeberListAdapter extends AbTypeBaseAdapter<MemberBean> {
    public MemeberListAdapter(Context context, List<MemberBean> datas, int... itemLayoutId) {
        super(context, datas, itemLayoutId);
    }

    @Override
    public int getItemType(int position) {
        if (position == 0) return 0;
        if (position >= 1) {
            if (getDatas().get(position - 1).getType().equals(getDatas().get(position).getType())) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public void bindData(ViewHolder holder, int type, MemberBean o) {
        switch (type) {
            case 0:
                holder.getView(R.id.type_area).setVisibility(View.VISIBLE);
                holder.setText(R.id.tv_type_name, "课程参与人");
                break;
        }

        ImageHelper.loadHeadIcon(holder.getItemView().getContext(), holder.getImageView(R.id.iv_head), R.drawable.person_icon_head_120, "www.baidu.com");
        holder.setText(R.id.tv_name, "名字").setText(R.id.tv_type, "主持人");

    }
}
