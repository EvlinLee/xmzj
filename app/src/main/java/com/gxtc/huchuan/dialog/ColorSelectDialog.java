package com.gxtc.huchuan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.gxtc.huchuan.R;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

/**
 * Created by sjr on 2017/2/24.
 * 颜色选择对话框
 */

public class ColorSelectDialog extends Dialog {
    private Context mContext;
    View view;

    private OnColorListener listener;

    public ColorSelectDialog(Context context) {
        super(context,R.style.dialog_Translucent);
        this.mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.dialog_color_selector, null);
        this.setContentView(view);

        final ColorPicker picker = (ColorPicker) view.findViewById(R.id.picker);
        SaturationBar saturationBar = (SaturationBar) view.findViewById(R.id.saturationbar);
        ValueBar valueBar = (ValueBar) view.findViewById(R.id.valuebar);

        picker.addSaturationBar(saturationBar);
        picker.addValueBar(valueBar);
        valueBar.setOnValueChangedListener(new ValueBar.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                picker.setNewCenterColor(value);
            }
        });
        //取消
        view.findViewById(R.id.btn_color_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorSelectDialog.this.dismiss();
            }
        });

        //确认
        view.findViewById(R.id.btn_color_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = picker.getColor();
                listener.onColor(color);
                ColorSelectDialog.this.dismiss();
            }
        });
    }

    public void setColorListener(OnColorListener listener) {
        this.listener = listener;
    }

    public interface OnColorListener {
        void onColor(int color);
    }
}
