package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.AbsBaseAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleMemberBean;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/6/6 .
 */

public class GroupMemberListAdapter extends AbsBaseAdapter<CircleMemberBean> {
    private  List<CircleMemberBean> list;
    private boolean isAll;
    public GroupMemberListAdapter(Context context, List<CircleMemberBean> datas, int itemLayoutId,boolean isAll) {
        super(context, datas, itemLayoutId);
        this.list = datas;
        this.isAll = isAll;
    }

    @Override
    public int getCount() {
        if (isAll){
            return list.size();
        }else {
            return list.size() > 30 ? 30 : list.size();
        }
    }

    @Override
    public void bindData(AbsBaseAdapter.ViewHolder holder, CircleMemberBean bean, int position) {
        TextView tvName= (TextView) holder.getView(R.id.tv_name);
        ImageView ivHead = (ImageView) holder.getView(R.id.iv_head);

        if("+".equals(bean.getUserCode())){
            tvName.setText("");
            ivHead.setImageResource(R.drawable.person_add_icon);
            ivHead.setScaleType(ImageView.ScaleType.FIT_XY);
        }else{
            ImageHelper.loadRound(getContext(),ivHead,bean.getUserPic(), 4);
            ivHead.setScaleType(ImageView.ScaleType.CENTER_CROP);
            tvName.setText(bean.getUserName());
        }
    }
}
