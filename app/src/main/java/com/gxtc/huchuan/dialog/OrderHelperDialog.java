package com.gxtc.huchuan.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.popup.base.BaseBubblePopup;
import com.gxtc.huchuan.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Steven on 17/4/21.
 */

public class OrderHelperDialog extends BaseBubblePopup<OrderHelperDialog> implements View.OnClickListener {

    @BindView(R.id.tv_helper)       TextView  tvHelper;
    @BindView(R.id.tv_home)         TextView  tvHome;

    private View.OnClickListener listener;

    public OrderHelperDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateBubbleView() {
        View view = View.inflate(getContext(), R.layout.dialog_order_helper,null);
        ButterKnife.bind(this,view);
        tvHelper.setOnClickListener(this);
        tvHome.setOnClickListener(this);
        return view;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if(listener != null){
            listener.onClick(v);
        }
    }
}
