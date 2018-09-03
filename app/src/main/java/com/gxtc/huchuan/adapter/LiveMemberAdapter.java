package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ChatJoinBean;
import com.gxtc.huchuan.bean.LiveHeadTitleBean;

import java.util.List;
import java.util.Objects;

/**
 * 直播设置-成员-适配器
 */

public class LiveMemberAdapter extends BaseRecyclerAdapter<ChatJoinBean.MemberBean> {



    public LiveMemberAdapter(Context context, List<ChatJoinBean.MemberBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void bindData(ViewHolder holder, int position, ChatJoinBean.MemberBean o) {
        holder.setText(R.id.tv_name, o.getName());
        switch (o.getJoinType()){
            case "1":
                holder .setText(R.id.tv_type, "管理员");
                break;
            case "2":
                holder.setText(R.id.tv_type, "讲师");
                break;
            case "3":
                holder .setText(R.id.tv_type, "主持人");
                break;
            case "20":
                holder .setText(R.id.tv_type, "主持人");
                break;
            default:
                holder .getView(R.id.tv_type).setVisibility(View.GONE);
//                    holder .setText(R.id.tv_type, "免费学员");

        }
        ImageHelper.loadHeadIcon(getContext(), holder.getImageView(R.id.iv_head), R.drawable.common_head_icon_100, o.getHeadPic());
    }


    @Override
    public int getItemCount() {
        return super.getItemCount() > 5 ? 5 : super.getItemCount();
    }
}
