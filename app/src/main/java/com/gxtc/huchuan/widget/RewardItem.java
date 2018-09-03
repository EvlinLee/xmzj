package com.gxtc.huchuan.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gxtc.huchuan.R;


/**
 * Created by Gubr on 2017/3/17.
 */

public class RewardItem extends LinearLayout {

    private TextView mTextView;
    private TextView mTextView1;

    public RewardItem(Context context) {
        this(context,null);
    }

    public RewardItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RewardItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER_HORIZONTAL);
        this.setBackground(getResources().getDrawable(R.drawable.shape_reward_btn_bg));
        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new LayoutParams(getContext(), null);
        }
        layoutParams.weight = 1;

        setLayoutParams(layoutParams);
        int dimension = getResources().getDimensionPixelOffset(R.dimen.margin_tiny);
        this.setPadding(dimension, dimension, dimension, dimension);
        mTextView = new TextView(getContext());
        mTextView.setTextColor(getResources().getColor(R.color.red));
        mTextView.setMaxEms(2);
        mTextView.setMinEms(2);
        mTextView1 = new TextView(getContext());
        mTextView1.setTextColor(getResources().getColor(R.color.red));
        addView(mTextView);
        addView(mTextView1);
    }



}
