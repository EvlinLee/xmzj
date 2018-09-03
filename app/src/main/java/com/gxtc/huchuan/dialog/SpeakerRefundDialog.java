package com.gxtc.huchuan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.gxtc.huchuan.R;


/**
 * zzg
 */

public class SpeakerRefundDialog extends Dialog implements View.OnClickListener {

    private View    view;
    private Context context;
    private View.OnClickListener mItemClickListener;
    private TextView tvNotice;
    private EditText editReson;

    public SpeakerRefundDialog(@NonNull Context context) {
        super(context, R.style.dialog_Translucent);
        this.context = context;
        initView();
    }
    private void initView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.speaker_refunds_layout, null);
        tvNotice= (TextView) view.findViewById(R.id.tv_nitice);
        editReson = (EditText) view.findViewById(R.id.edit_reson);
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
        view.findViewById(R.id.tv_sure).setOnClickListener(this);
        setCanceledOnTouchOutside(true);
        setContentView(view);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8);
        view.setLayoutParams(layoutParams);
    }

    public  void setOnCompleteListener(View.OnClickListener listener){ mItemClickListener=listener;}

    @Override
    public void onClick(View v) {
        if(mItemClickListener != null)
        mItemClickListener.onClick(v);
    }
    public String getResone(){
        return editReson.getText().toString();
    }
}
