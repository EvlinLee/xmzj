package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.AbsBaseAdapter;
import com.gxtc.huchuan.R;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/15.
 */

public class ListDialogAdapter extends AbsBaseAdapter<String> {
    boolean isShowIv;
    List<String> datas;
    public ListDialogAdapter(Context context, List<String> datas, int itemLayoutId) {
        super(context, datas, itemLayoutId);
    }
    public ListDialogAdapter(Context context, List<String> datas, int itemLayoutId,boolean isShowIv) {
        super(context, datas, itemLayoutId);
        this.isShowIv=isShowIv;
        this.datas=datas;
    }

    @Override
    public void bindData(AbsBaseAdapter.ViewHolder holder, String s, int position) {
        TextView tv = (TextView) holder.getView(R.id.tv_content);
        ImageView iv = (ImageView) holder.getView(R.id.right_more);
        if(isShowIv && position == datas.size() -1){
            iv.setVisibility(View.INVISIBLE);
            iv.setImageResource(R.drawable.person_icon_more);
        }else {
            iv.setVisibility(View.GONE);
        }
        tv.setText(s);
    }
}
