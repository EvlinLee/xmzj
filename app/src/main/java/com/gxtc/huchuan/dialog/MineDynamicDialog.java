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
 * 动态精华设置对话框
 */

public class MineDynamicDialog extends BasePopup<MineDynamicDialog> implements View.OnClickListener {

    @BindView(R.id.tv_dynamic_essence)
    TextView tvEssence;
    @BindView(R.id.tv_dynamic_delete)
    TextView tvDelete;

    private View.OnClickListener listener;

    public MineDynamicDialog(Context context) {
        super(context);
    }

    @Override
    public void setUiBeforShow() {

    }

    @Override
    public View onCreatePopupView() {
        View view = View.inflate(getContext(), R.layout.dialog_dynamic_manager, null);
        ButterKnife.bind(this, view);
        tvEssence.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
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

    public void setTvEssenceText(String text) {
        if (!"".equals(text)) {
            tvEssence.setText(text);
        }
    }
}
