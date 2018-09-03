package com.gxtc.huchuan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxtc.huchuan.R;


/**
 * zzg
 */

public class RefundDialog extends Dialog implements View.OnClickListener {

    private View    view;
    private Context context;
    private View.OnClickListener mItemClickListener;
    private TextView tvNotice;
    private EditText editCase;
    private EditText editReson;

    public RefundDialog(@NonNull Context context) {
        super(context, R.style.dialog_Translucent);
        this.context = context;
        initView();
    }
    private void initView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.refund_layout, null);
        tvNotice= (TextView) view.findViewById(R.id.tv_nitice);
        editCase = (EditText) view.findViewById(R.id. edit_case);
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

    public void setNotice(String text){
        tvNotice.setText("您最多可以退款"+text+"元");
    }

    public String getCase(){ return editCase.getText().toString();}

    public String getResone(){
        return editReson.getText().toString();
    }
}
