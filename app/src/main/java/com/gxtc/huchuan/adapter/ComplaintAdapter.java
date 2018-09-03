package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;

import java.util.List;

/**
 * Created by sjr on 2017/3/2
 * 投诉
 */

public class ComplaintAdapter extends BaseRecyclerAdapter<String> {
    private Context mContext;
    private OnSelectedItemListener listener;

    public ComplaintAdapter(Context context, List<String> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
        this.mContext = context;
    }


    @Override
    public void bindData(final ViewHolder holder, final int position, final String s) {
        final TextView tvTitle = (TextView) holder.getView(R.id.tv_complaint_msg);
        tvTitle.setText(s);
        final TextView tvCancel = (TextView) holder.getView(R.id.tv_complaint_cancel);

        RelativeLayout rl = (RelativeLayout) holder.getView(R.id.rl_item_complaint_root);
        rl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (tvCancel.getVisibility() == View.INVISIBLE) {
                    tvCancel.setVisibility(View.VISIBLE);
                    tvTitle.setTextColor(Color.rgb(153, 153, 153));
                    listener.onSelected(s, true);
                } else if (tvCancel.getVisibility() == View.VISIBLE) {
                    tvCancel.setVisibility(View.INVISIBLE);
                    tvTitle.setTextColor(Color.rgb(51, 51, 51));
                    listener.onSelected(s, false);
                }

            }
        });
    }


    public void setOnSelectedListener(OnSelectedItemListener listener) {
        this.listener = listener;
    }


    //选中监听
    public interface OnSelectedItemListener {
        void onSelected(String msg, boolean isSelect);
    }
}
