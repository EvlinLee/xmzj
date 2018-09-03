package com.gxtc.huchuan.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.gxtc.huchuan.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Gubr on 2017/3/2.
 */

public class VoicRecordCancelDialog extends DialogFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_voicerecord_cancel, container);
        ButterKnife.bind(this, view);
        return view;

    }





    @OnClick({R.id.tv_cancel, R.id.tv_asok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_asok:
                mOnClickListener.onClick(view);
                break;
        }
    }


    private View.OnClickListener mOnClickListener;


    public void setOnOkClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }
}
