package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.base.BaseRecyclerTypeAdapter;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.DustbinListBean;
import com.gxtc.huchuan.utils.DateUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/6/21 0021.
 */

public class DustbinListAdapter extends BaseRecyclerAdapter<DustbinListBean> {

    private View.OnClickListener onClickListener;



    public DustbinListAdapter(Context context, List<DustbinListBean> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, DustbinListBean bean) {
        TextView tvTime = (TextView) holder.getView(R.id.tv_time);
        TextView tvTitle = (TextView) holder.getView(R.id.tv_title);
        TextView tvHuiFu = (TextView) holder.getView(R.id.tv_huifu);              ;

        tvTime.setText(bean.getTime());
        tvTitle.setText(bean.getTitle());

        tvHuiFu.setTag(bean);

        tvHuiFu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener != null)
                    onClickListener.onClick(v);
            }
        });

    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
