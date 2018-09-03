package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/10.
 */

public class PopRefundAdapter extends BaseRecyclerAdapter<String> {

    public PopRefundAdapter(Context context, List<String> list, int itemLayoutId) {
        super(context, list, itemLayoutId);
    }

    @Override
    public void bindData(ViewHolder holder, int position, String s) {
        TextView tv = (TextView) holder.getView(R.id.tv_complaint_msg);
        tv.setText(s);
    }
}
