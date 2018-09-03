package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.helper.ImageHelper;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.CircleBean;


import java.util.List;

public class CircleListAdapter extends BaseRecyclerAdapter<CircleBean> {

    private View.OnClickListener mClickListener;

    public CircleListAdapter(Context context, List<CircleBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, CircleBean bean) {
        ImageView img = (ImageView) holder.getView(R.id.img_icon);
        TextView tvName = (TextView) holder.getView(R.id.tv_name);
        TextView tvTitle = (TextView) holder.getView(R.id.tv_title);
        TextView tvBottom = (TextView) holder.getView(R.id.tv_bottom);
        TextView btn = (TextView) holder.getView(R.id.btn_join);
        TextView tvId = (TextView) holder.getView(R.id.tv_id);
        tvId.setText("ID:"+bean.getGroupCode());
        tvName.setText(bean.getGroupName());
        tvTitle.setText(bean.getContent());
        ImageHelper.loadRound(getContext(),img,bean.getCover(),2);

        //是否已加入。0：未加入。1：已加入
        if(bean.getIsJoin() == 0){
            btn.setVisibility(View.VISIBLE);
        }else{
            btn.setVisibility(View.INVISIBLE);
        }

        String temp = "成员" + bean.getAttention() + "  " + "动态" + bean.getInfoNum();
        tvBottom.setText(temp);

        btn.setTag(bean);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mClickListener != null){
                    mClickListener.onClick(v);
                }
            }
        });
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        mClickListener = clickListener;
    }
}
