package com.gxtc.huchuan.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;

/**
 * Created by 宋家任 on 2017/5/16.
 * recyclervie嵌套recyclerview内部分割线问题
 */

public class CircleRecyclerView extends RecyclerView {


    public CircleRecyclerView(Context context) {
        this(context, null);

    }

    public CircleRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.BOTH_SET, WindowUtil.dip2px(context,5),context.getResources().getColor(R.color.white)));
    }
}
