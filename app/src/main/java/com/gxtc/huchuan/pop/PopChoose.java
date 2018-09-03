package com.gxtc.huchuan.pop;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.gxtc.huchuan.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PopChoose extends UnifyPop<PopChoose> {

    @BindView(R.id.btn_free_1) TextView tvFree;
    @BindView(R.id.btn_pay_1)  TextView tvPay;

    private View.OnClickListener mlistenner;

    public PopChoose(Activity activity) {
        super(activity);
    }

    @OnClick({R.id.btn_free_1, R.id.btn_pay_1,R.id.btn_all})
    public void onClick(View v) {
        dismiss();
        if (mlistenner != null) {
            mlistenner.onClick(v);
        }
    }

    public void setMlistenner(View.OnClickListener mlistenner) {
        this.mlistenner = mlistenner;
    }

    @Override
    public View onCreateBubbleView() {
        View view = View.inflate(getContext(), R.layout.choose_layout, null);
        ButterKnife.bind(this, view);
        return view;
    }

}
