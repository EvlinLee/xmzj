package com.gxtc.huchuan.dialog;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BaseDialogFragment;
import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.ui.common.CommonWebViewActivity;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/8/5.
 */

public class CircleRuleDialog extends BaseDialogFragment implements View.OnClickListener {

    @BindView(R.id.tv_next)     TextView tvNext;
    @BindView(R.id.tv_agree)    TextView tvAgree;

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCanOutside(false);
        int margin = getResources().getDimensionPixelSize(R.dimen.margin_large);
        int width  = WindowUtil.getScreenWidth(getContext()) - margin * 2;
        setWidth(width);
        return super.onCreateDialog(savedInstanceState);
    }

    @NotNull
    @Override
    public View initView() {
        return View.inflate(getContext(), R.layout.dialog_circle_rule, null);
    }

    @OnClick({R.id.tv_agree, R.id.img_close,R.id.tv_protocol})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                dismiss();
                break;

            case R.id.tv_agree:
                changeStatus();
                break;

            case R.id.tv_protocol:
                CommonWebViewActivity.startActivity(getContext(), Constant.Url.PROTOCOL_CIRCLE,"圈子创建协议");
                break;

            case R.id.img_close:
                getActivity().finish();
                break;
        }
    }

    @Override
    public void initData(View dialogView) {
        tvAgree.performClick();
    }

    private void changeStatus() {
        if (tvAgree.isSelected()) {
            Drawable d = getResources().getDrawable(R.drawable.person_circle_manage_gou_normal);
            d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
            tvAgree.setCompoundDrawables(d, null, null, null);
            tvAgree.setSelected(false);
            tvNext.setOnClickListener(null);
            tvNext.setBackgroundResource(R.drawable.btn_normal);

        } else {
            Drawable d = getResources().getDrawable(R.drawable.person_circle_manage_gou_selecte);
            d.setBounds(0, 0, d.getMinimumWidth(), d.getMinimumHeight());
            tvAgree.setCompoundDrawables(d, null, null, null);
            tvAgree.setSelected(true);
            tvNext.setOnClickListener(this);
            tvNext.setBackgroundResource(R.drawable.btn_press);
        }
    }
}
