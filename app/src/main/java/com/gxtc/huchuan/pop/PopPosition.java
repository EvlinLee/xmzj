package com.gxtc.huchuan.pop;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.gxtc.commlibrary.base.BasePopupWindow;
import com.gxtc.huchuan.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class PopPosition  extends BasePopupWindow{

    @BindView(R.id.btn_ts_ok)           TextView          btnOk;
    @BindView(R.id.picker_position)     NumberPickerView  pickerView;
    @BindView(R.id.tv_ts_title)         TextView          tvTitle;

    private int index;

    private NumberPickerView.OnValueChangeListener listener;

    public PopPosition(Activity activity, int resId) {
        super(activity, resId);
    }

    @Override
    public void init(View layoutView) {
        ButterKnife.bind(this,layoutView);
    }


    @Override
    public void initListener() {
        pickerView.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                index = newVal;
            }
        });

    }

    @OnClick(R.id.btn_ts_ok)
    public void onClick(View v){
        closePop();
        if(listener != null){
            listener.onValueChange(null,0,index);
        }
    }


    public void setData(String [] itmes){
        pickerView.refreshByNewDisplayedValues(itmes);
    }
    public String [] getData( ){
        return pickerView.getDisplayedValues();
    }

    public void setValue(int value){
        pickerView.setValue(value);
    }

    public void setOnValueChangeListener(NumberPickerView.OnValueChangeListener listener) {
        this.listener = listener;
    }


    public void setTitle(String title){
        tvTitle.setText(title);
    }
}
