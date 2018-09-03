package com.gxtc.huchuan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gxtc.huchuan.R;
import com.gxtc.huchuan.adapter.ListDialogAdapterV1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * zzg
 */

public class VertifanceFlowDialog extends Dialog implements View.OnClickListener {

    private View                view;
    Context context;
    private TextView tvCheckStatus;
    private TextView tvConplete;
    private ImageView ivCheckStatus;
    private View.OnClickListener mItemClickListener;
    private TextView tvEnter;

    public VertifanceFlowDialog(@NonNull Context context) {
        super(context, R.style.dialog_Translucent);
        this.context = context;
        initView();
    }
    private void initView() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.vertifance_flow_dialog, null);
        tvCheckStatus= (TextView) view.findViewById(R.id.check_status);
        tvConplete= (TextView) view.findViewById(R.id.complete);
        ivCheckStatus= (ImageView) view.findViewById(R.id.pic_check_result);
        tvEnter= (TextView) view.findViewById(R.id.tx_enter);
        setCanceledOnTouchOutside(true);
        tvConplete.setOnClickListener(this);
        tvEnter.setOnClickListener(this);
        setContentView(view);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8);
        view.setLayoutParams(layoutParams);
    }


    @Override
    public void onClick(View v) {
        if(mItemClickListener != null)
        mItemClickListener.onClick(v);
    }

    public void setFlowStatusPic(int resId){
        ivCheckStatus.setImageResource(resId);
    }

    public  void setOnCompleteListener(View.OnClickListener listener){
        mItemClickListener=listener;
    }

    public void setFlowStatus(int resId){
        tvCheckStatus.setText(context.getResources().getString(resId));
    }

    public void setFlowStatus(String text){
        if(text != null)
        tvCheckStatus.setText(text);
    }


    public void setUpButtonText(String text){
        tvConplete.setText(text);
    }

    public void setDownButtonText(String text){
        tvEnter.setText(text);
    }

    public void setDownButtonVisiblet(boolean isVisible){
        if(isVisible){
            tvEnter.setVisibility(View.VISIBLE);
        }else {
            tvEnter.setVisibility(View.GONE);
        }
    }
}
