package com.gxtc.huchuan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.gxtc.commlibrary.utils.WindowUtil;
import com.gxtc.huchuan.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/13.
 */

public class CircleJoinDialog extends Dialog {

    @BindView(R.id.tv_ok)        TextView tvOk;
    @BindView(R.id.edit_content) EditText edit;

    private String message = "";
    public String prompt = "";
    private View.OnClickListener mOnClickListener;

    public CircleJoinDialog(@NonNull Context context,String  prompt) {
        super(context, R.style.dialog_Translucent);
        this.prompt = prompt;
        initView();
    }

    private void initView() {
        View view = View.inflate(getContext(),R.layout.dialog_join_message,null);
        ButterKnife.bind(this,view);
        setContentView(view);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int) (WindowUtil.getScreenWidth(getContext()) * 0.8);
        edit.setHint(prompt);
        tvOk.setTag(message);
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                message = s.toString();
                tvOk.setTag(message);
            }
        });

    }

    @OnClick(R.id.tv_ok)
    public void onClick(View v){
        if(mOnClickListener != null){
            mOnClickListener.onClick(v);
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }
}
