package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.ArticleListBean;
import com.gxtc.huchuan.bean.CircleMemberBean;

import java.util.List;

/**
 * Created by zzg on 17/3/21.
 */

public class AllMembersListAdapter extends BaseRecyclerAdapter<CircleMemberBean>{

    public AllMembersListAdapter(Context context, List<CircleMemberBean> datas, int itemLayoutId) {
        super(context, datas, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, CircleMemberBean bean) {
        ImageView ivHead = (ImageView) holder.getView(R.id.iv_head);
        ImageHelper.loadImage(getContext(),ivHead,bean.getUserPic(),R.drawable.circle_head_icon_120);

        TextView tvName= (TextView) holder.getView(R.id.tv_name);
        tvName.setText(bean.getUserName());

    }
}
