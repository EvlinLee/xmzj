package com.gxtc.huchuan.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.popup.base.BaseBubblePopup;
import com.flyco.dialog.widget.popup.base.BasePopup;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.pop.UnifyPop;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Steven on 17/4/21.
 * 屏蔽动态或者屏蔽人
 * 这里应该优化做成通用的
 */

public class CircleHomeShieldDialogV5 extends UnifyPop<CircleHomeShieldDialogV5> implements
        View.OnClickListener {

    @BindView(R.id.tv_shield_dynamic) TextView tvShieldDynamic;
    @BindView(R.id.tv_shield_user)    TextView tvShieldUser;
    @BindView(R.id.tv_shield_circle_dynamic)    TextView tvShieldCircleDynamic;
    @BindView(R.id.line)                            View line;

    private View.OnClickListener listener;

    public CircleHomeShieldDialogV5(Context context) {
        super(context);
    }

    @Override
    public View onCreateBubbleView() {
        View view = View.inflate(getContext(), R.layout.dialog_circle_home_shield, null);
        ButterKnife.bind(this, view);
        tvShieldDynamic.setOnClickListener(this);
        tvShieldUser.setOnClickListener(this);
        tvShieldCircleDynamic.setOnClickListener(this);
        return view;
    }

    public void setShieldCircleDynamic(boolean isShow){
        if(isShow){
            tvShieldCircleDynamic.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        }else {
            tvShieldCircleDynamic.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        }
    }


    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) {
            listener.onClick(v);
        }
    }
}
