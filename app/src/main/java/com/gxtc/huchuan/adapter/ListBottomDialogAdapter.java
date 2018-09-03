package com.gxtc.huchuan.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.commlibrary.base.AbsBaseAdapter;
import com.gxtc.huchuan.R;

import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/7/3.
 */

public class ListBottomDialogAdapter extends AbsBaseAdapter<String> {

    private int [] imgs;
    private int drawableLeftPadding;

    public ListBottomDialogAdapter(Context context, List<String> datas, int itemLayoutId,int [] leftImgs) {
        super(context, datas, itemLayoutId);
        imgs = leftImgs;
        drawableLeftPadding = context.getResources().getDimensionPixelOffset(R.dimen.margin_small);
    }

    @Override
    public void bindData(AbsBaseAdapter.ViewHolder holder, String s, int position) {
        TextView  tv = (TextView) holder.getView(R.id.tv_content);
        tv.setText(s);

        if(imgs != null && imgs.length > 0){
            Drawable d = getContext().getResources().getDrawable(imgs[position]);
            if(d != null){
                d.setBounds(0,0,d.getMinimumWidth(),d.getMinimumHeight());
                tv.setCompoundDrawables(d,null,null,null);
                tv.setCompoundDrawablePadding(drawableLeftPadding);
            }
        }
    }

    public void setDrawableLeftPadding(int drawableLeftPadding) {
        this.drawableLeftPadding = drawableLeftPadding;
    }
}
