package com.gxtc.huchuan.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.popup.base.BaseBubblePopup;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.pop.PopEnterAnim;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Steven on 17/4/21.
 *
 */

public class FindCircleDialog extends BaseBubblePopup<FindCircleDialog> implements View.OnClickListener {

    @BindView(R.id.tv_all)       TextView  tvAllr;
    @BindView(R.id.tv_free)         TextView  tvFree;
    @BindView(R.id.tv_pay)         TextView  tvPay;

    private View.OnClickListener listener;

    public FindCircleDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateBubbleView() {
        View view = View.inflate(getContext(), R.layout.dialog_find_circle,null);
        ButterKnife.bind(this,view);
        tvAllr.setOnClickListener(this);
        tvFree.setOnClickListener(this);
        tvPay.setOnClickListener(this);
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
