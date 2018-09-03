package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseRecyclerAdapter;
import com.gxtc.huchuan.R;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/8/4.
 */

public class CircleInviteAdapter extends BaseRecyclerAdapter<String>{

    private int [] idRes;

    public CircleInviteAdapter(Context context, List<String> list, int itemLayoutId,int [] idRes) {
        super(context, list, itemLayoutId);
        this.idRes = idRes;
    }

    @Override
    public void bindData(ViewHolder holder, int position, String s) {
        TextView  tv = (TextView) holder.getView(R.id.tv_name);
        ImageView img = (ImageView) holder.getView(R.id.imgView);

        tv.setText(s);
        img.setImageResource(idRes[position]);
    }
}
