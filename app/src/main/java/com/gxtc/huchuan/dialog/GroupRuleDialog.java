package com.gxtc.huchuan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.R;
import com.gxtc.huchuan.bean.GroupRuleBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupRuleDialog extends Dialog {

    @BindView(R.id.tv_confirm) TextView  tvConfirm;
    @BindView(R.id.iv_close)   ImageView ivClose;
    @BindView(R.id.tv_rule)    TextView  tvRule;

    private GroupRuleBean bean;

    private View.OnClickListener mOnClickListener;

    public GroupRuleDialog(@NonNull Context context,GroupRuleBean bean) {
        super(context, R.style.BottomDialog);
        this.bean = bean;

        initView();
        initData();
        initListener();
    }

    private void initData() {
        if(bean == null || bean.getRoletext() == null) return;
        String content = TextUtils.isEmpty(bean.getRoletext().trim()) ? "暂无群规" : bean.getRoletext();
        tvRule.setText(content);
    }


    private void initView() {
        View view = View.inflate(MyApplication.getInstance(), R.layout.dialog_group_rule, null);
        ButterKnife.bind(this, view);
        setContentView(view);
    }



    private void initListener() {
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
    }

    @OnClick({R.id.tv_confirm, R.id.iv_close})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;

            //确认
            case R.id.tv_confirm:
                if(mOnClickListener != null) mOnClickListener.onClick(v);
                dismiss();
                break;

        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public TextView getConfirmBtn() {
        return tvConfirm;
    }
}
