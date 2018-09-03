package com.gxtc.huchuan.pop;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.NewNewsBean;
import com.gxtc.huchuan.bean.NewsBean;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Steven on 17/4/21.
 * 屏蔽动态或者屏蔽人
 * 这里应该优化做成通用的
 */

public class PopShield extends UnifyPop<PopShield> implements
        View.OnClickListener {

    @BindView(R.id.tv_shield_dynamic) TextView tvShieldDynamic;
    @BindView(R.id.tv_shield_user)    TextView tvShieldUser;

    private View.OnClickListener listener;

    private NewNewsBean data;

    public PopShield(Context context, NewNewsBean data) {
        super(context);
        this.data = data;
        tvShieldDynamic.setTag(data);
        tvShieldUser.setTag(data);
    }

    public PopShield(Context context,NewsBean data) {
        super(context);
        tvShieldDynamic.setTag(data);
        tvShieldUser.setTag(data);
    }

    @Override
    public View onCreateBubbleView() {
        View view = View.inflate(getContext(), R.layout.shield_layout, null);
        ButterKnife.bind(this, view);
        tvShieldDynamic.setOnClickListener(this);
        tvShieldUser.setOnClickListener(this);
        return view;
    }

    public  PopShield setTextNotice(String shieldDynamic,String shieldUser){
        tvShieldDynamic.setText(shieldDynamic);
        tvShieldUser.setText(shieldUser);
        return this;
    }


    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
        dismiss();
    }
}
