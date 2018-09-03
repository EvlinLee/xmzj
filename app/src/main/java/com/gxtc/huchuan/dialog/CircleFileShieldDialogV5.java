package com.gxtc.huchuan.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.flyco.dialog.widget.popup.base.BasePopup;
import com.gxtc.huchuan.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Steven on 17/4/21.
 * 屏蔽动态或者屏蔽人
 * 这里应该优化做成通用的
 */

public class CircleFileShieldDialogV5 extends BasePopup<CircleFileShieldDialogV5> implements View.OnClickListener {

    @BindView(R.id.tv_create_folder)
    TextView tvShieldDynamic;
    @BindView(R.id.tv_audit_file)
    TextView tvShieldUser;

    private View.OnClickListener listener;

    public CircleFileShieldDialogV5(Context context) {
        super(context);
    }

    @Override
    public void setUiBeforShow() {

    }

    @Override
    public View onCreatePopupView() {
        View view = View.inflate(getContext(), R.layout.dialog_circle_file_shield, null);
        ButterKnife.bind(this, view);
        tvShieldDynamic.setOnClickListener(this);
        tvShieldUser.setOnClickListener(this);
        return view;
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
