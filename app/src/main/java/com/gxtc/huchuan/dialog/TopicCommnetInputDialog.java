package com.gxtc.huchuan.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Gubr on 2017/3/2.
 */

public class TopicCommnetInputDialog extends DialogFragment {


    @BindView(R.id.tv_cancel)   TextView       mTvCancel;
    @BindView(R.id.tv_finish)   TextView       mTvFinish;
    @BindView(R.id.ll_top)      RelativeLayout mLlTop;
    @BindView(R.id.et_comment)  EditText       mEtComment;
    @BindView(R.id.iv_question) CheckBox       mIvQuestion;
    @BindView(R.id.ll_question) LinearLayout   mLlQuestion;


    private boolean isQuestion;
    private String  questionStr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_comment, container);
        ButterKnife.bind(this, view);
        initData();
        initListener();
        return view;

    }

    private void initData() {

    }

    private void initListener() {
        mIvQuestion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isQuestion = isChecked;
            }
        });

        mEtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                questionStr = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @OnClick({R.id.tv_cancel, R.id.tv_finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                this.dismiss();
                break;
            case R.id.tv_finish:
                finish();
                break;


        }
    }


    private void finish() {
        if (TextUtils.isEmpty(questionStr)) {
            ToastUtil.showShort(getActivity(), "请输入评论内容");
        } else {
            dismiss();
        }
    }
}
