package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.AbsBaseAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleBean;

import java.util.List;

/**
 * Describe:
 * Created by ALing on 2017/4/26 .
 */

public class PersonalHomePageTopAdapter extends AbsBaseAdapter<CircleBean> {
    private List<CircleBean> list;

    @Override
    public int getCount() {
        return list.size() <= 5 ? list.size() : 5;
    }

    public PersonalHomePageTopAdapter(Context context, List<CircleBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.list = list;
    }

    @Override
    public void bindData(AbsBaseAdapter.ViewHolder holder, CircleBean bean, int position) {
       TextView tvCircleName = (TextView) holder.getView(R.id.tv_circle_name);
        ImageView ivCircle = (ImageView) holder.getView(R.id.iv_circle_img);
        ImageView ivIsMy = (ImageView) holder.getView(R.id.iv_isMy);

        tvCircleName.setText(bean.getGroupName());
        ImageHelper.loadRound(getContext(),ivCircle,bean.getCover(), 4);

        if (bean.getIsMy() == 1){
            ivIsMy.setVisibility(View.VISIBLE);
        }else {
            ivIsMy.setVisibility(View.GONE);
        }

    }
}
