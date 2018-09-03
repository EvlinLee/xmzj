package com.gxtc.huchuan.adapter;

import android.content.Context;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.DealListBean;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/4/11 .
 */

public class PersonalDealMoreAdapter extends BaseRecyclerAdapter<DealListBean> {
    public PersonalDealMoreAdapter(Context context, List<DealListBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, DealListBean dealListBean) {
        holder.setText(R.id.tv_title,dealListBean.getTitle());
                /*.setText(R.id.tv_username,dealListBean.getUserName())
                .setText(R.id.tv_create_time,dealListBean.getCreateTime());*/
    }
}
