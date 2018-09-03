package com.gxtc.huchuan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ListDialogAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/5/15.
 */

public class CustomDayDialog extends Dialog {

    private View     view;
    private EditText edText;
    private TextView cancel;
    private TextView confirm;
    OnItemClickListener mItemClickListener;
    public interface OnItemClickListener{
       public void confrm(String content);
       public void cancel();
    }
    public CustomDayDialog(@NonNull Context context, int mydialogstyle) {
        super(context,mydialogstyle);


        initView();
        initListener();
    }

    private void initView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_days_input, null);
        edText = (EditText) view.findViewById(R.id.et_input);
        cancel = (TextView) view.findViewById(R.id.tv_cancel);
        confirm = (TextView) view.findViewById(R.id.tv_confirm);
        setContentView(view);
    }

    private void initListener() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemClickListener != null)
                mItemClickListener.confrm(edText.getText().toString());
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemClickListener != null)
                mItemClickListener.cancel();
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
}
